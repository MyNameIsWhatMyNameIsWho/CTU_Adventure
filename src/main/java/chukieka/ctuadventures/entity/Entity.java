package chukieka.ctuadventures.entity;

import org.apache.logging.log4j.LogManager; // To log the collision detector
import org.apache.logging.log4j.Logger;
import chukieka.ctuadventures.GamePanel;
import chukieka.ctuadventures.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Entity {
    GamePanel gp;
    private static final Logger logger = LogManager.getLogger(Entity.class); // Define logger
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2,
            attackRight1, attackRight2;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // for a collision
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    String[] dialogues = new String[20];

    // STATE
    public int worldX, worldY;
    public String direction = "down"; // any direction is fine
    public int spriteNum = 1;
    int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false; // For letting the monster attack only once in a second
    boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;

    // COUNTER
    public int spriteCounter = 0;
    public int actionLockCounter = 0;
    public int invincibleCounter = 0;
    int dyingCounter = 0;

    // CHARACTER
    public int type; // 0 - player, 1 - npc*, 2 - monster
    public String name;
    public int speed;
    public int maxLife;
    public int life;
    public int attack;
    public Entity currentWeapon;

    // ITEM ATTRIBUTES
    public int attackValue;

    public Entity(GamePanel gp){
        this.gp = gp;
    }

    public void setAction(){}

    public void damageReaction(){}

    public void update(){
        setAction();

        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.monster);

        boolean contactPLayer =  gp.cChecker.checkPlayer(this);

        if (this.type == 2 && contactPLayer){ // if monster has touched player
            if (!gp.player.invincible){ // if player hasn't been attacked yet (in 60 secs)
                gp.player.life -= 1; // Give damage
                logger.info("Enemy hit the player");
                gp.player.invincible = true;
            }
        }
        
        // IF COLLISION IS TRUE, PLAYER CANNOT MOVE
        if(!collisionOn) {
            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                // collision is checking anf moving player if collisionOn is true
                case "right" -> worldX += speed;
            }
            //logger.info("You are touching the solid object (chukieka.ctuadventures.tile)");
        }

        spriteCounter++;
        if (spriteCounter > 10){
            if (spriteNum == 1){
                spriteNum = 2;
            } else if(spriteNum == 2){
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        if (invincible){
            invincibleCounter++;
            if (invincibleCounter > 40){ // For monsters. A bit shorter than for player (40 < 60), so player can attack sooner
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void speak(){
        if (dialogues[dialogueIndex] == null){
            dialogueIndex = 0;
        }
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        switch (gp.player.direction) { // change a position of any npc to look to the character while speaking
            case "up" -> direction = "down";
            case "down" -> direction = "up";
            case "right" -> direction = "left";
            case "left" -> direction = "right";
        }
    }

    public void draw(Graphics2D g2){
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){
            switch (direction) {
                case "up" -> {
                    if (spriteNum == 1) image = up1;
                    if (spriteNum == 2) image = up2;
                }
                case "down" -> {
                    if (spriteNum == 1) image = down1;
                    if (spriteNum == 2) image = down2;
                }
                case "left" -> {
                    if (spriteNum == 1) image = left1;
                    if (spriteNum == 2) image = left2;
                }
                case "right" -> {
                    if (spriteNum == 1) image = right1;
                    if (spriteNum == 2) image = right2;
                }
            }

            // Monster HP bar
            if (type == 2){
//                g2.setColor(new Color(35, 35, 35));
//                g2.fillRect(screenX = 1, screenY - 11, gp.tileSize + 2, 7); TODO: Delete, if still bug
                double oneScale = (double)gp.tileSize/maxLife; // For calculating the current scale of the bar
                double hpBarValue = oneScale * life;

                g2.setColor(new Color(200, 0, 40));
                g2.fillRect(screenX, screenY - 10, (int)hpBarValue, 5);
            }

            if (invincible){
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)); // Makes the player transparent when hit (in invincible time)
                logger.info("You hit the monster! Wow!");
            }

            if (dying){ // for the killed mobs
                dyingAnimation(g2);
            }

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            // Reset the transparency of the graphics 2D
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    public void dyingAnimation(Graphics2D g2){
        dyingCounter++;

        if (dyingCounter <= 5){
            changeAlpha(g2, 0); // Makes the player transparent
        }
        if (dyingCounter > 5 && dyingCounter < 10){
            changeAlpha(g2, 1);
        }
        if (dyingCounter > 10 && dyingCounter < 15){
            changeAlpha(g2, 0);
        }
        if (dyingCounter > 15 && dyingCounter < 20){
            changeAlpha(g2, 1);
        }
        if (dyingCounter > 25 && dyingCounter < 30){
            changeAlpha(g2, 0);
        }
        if (dyingCounter > 35 && dyingCounter < 40){
            changeAlpha(g2, 1);
        }
        if (dyingCounter > 40){
            dying = false;
            alive = false;
        }
    }

    public void changeAlpha(Graphics2D g2, float alphaValue){
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }

    public BufferedImage setup(String imagePath, int width, int height){
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
            image = uTool.scaleImage(image, width, height);
        } catch (IOException e){
            e.printStackTrace();
        }

        return image;
    }

}
