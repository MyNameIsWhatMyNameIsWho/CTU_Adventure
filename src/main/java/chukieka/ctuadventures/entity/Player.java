package chukieka.ctuadventures.entity;

import chukieka.ctuadventures.JsonPathConfig;
import chukieka.ctuadventures.KeyHandler;
import chukieka.ctuadventures.GamePanel;
import chukieka.ctuadventures.object.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager; // To log the collision detector
import org.apache.logging.log4j.Logger;

public class Player extends Entity {
    KeyHandler keyH;
    public final int screenX, screenY; // indicate where we draw player on the screen
    public int level = 1;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;
    public int hasKey = 0;
    public int maxLife = 6;

    public Player(GamePanel gp, KeyHandler keyH){ // Player class, to use into GamePanel
        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWight/2 - (gp.tileSize/2); // to point the character in the centre of the map.
        screenY = gp.screenHeight/2 - (gp.tileSize/2);

        solidArea = new Rectangle(8, 16, 28, 28); // collision for one "block". (A bit smaller to let player fit easily)
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        attackArea.width = gp.tileSize; // the "reach" of the weapon
        attackArea.height = gp.tileSize;

        loadPlayer(gp.keyH.loadPressed);
        getPlayerImage();
        getPlayerAttackImage();
    }

    public  void setDefaultValues(String jsonFilePath){ // For everybody (player, enemies...)
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFilePath)){
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            worldX = (int) ((long) jsonObject.get("worldX") * gp.tileSize);
            worldY = (int) ((long) jsonObject.get("worldY") * gp.tileSize);
            life = (int) ((long) jsonObject.get("lifes"));
            speed = (int) ((long) jsonObject.get("speed"));
        } catch (ParseException | IOException e){
            e.printStackTrace();
        }

        direction = "down"; //any direction is OK.
        currentWeapon = new OBJ_Weapon(gp);
        attack = getAttack();
    }

    public int getAttack(){
        return attack = currentWeapon.attackValue;
    }

    public void setItems(String jsonFilePath) {
        try {
            // Read the JSON file
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(jsonFilePath));

            // Read objects array from JSON
            JSONArray objectsArray = (JSONArray) json.get("inventory");

            // Iterate over objects array and add items to inventory
            for (Object object : objectsArray) {
                JSONObject objJson = (JSONObject) object;
                String itemName = (String) objJson.get("name");

                // Create object based on the item name
                if (itemName.equals("Key")) {
                    inventory.add(new OBJ_Key(gp));
                    hasKey++;
                } else if (itemName.equals("Boots")) {
                    inventory.add(new OBJ_Boots(gp));
                } else if (itemName.equals("Door")) {
                    inventory.add(new OBJ_Door(gp));
                } else if (itemName.equals("Pen")){
                    inventory.add(new OBJ_Weapon(gp));
                }
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during JSON parsing
            e.printStackTrace();
        }
    }

    public void getPlayerImage(){
        up1 = setup("/player/ch_up1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/ch_up2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/ch_down1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/ch_down2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/ch_left1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/ch_left2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/ch_right1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/ch_right2", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttackImage(){
        attackUp1 = setup("/player/ch_attack_up1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setup("/player/ch_attack_up2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setup("/player/ch_attack_down1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setup("/player/ch_attack_down2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setup("/player/ch_attack_left1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setup("/player/ch_attack_left2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setup("/player/ch_attack_right1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setup("/player/ch_attack_right2", gp.tileSize * 2, gp.tileSize);
    }

    public void loadPlayer(boolean loadPressed){
        if (!loadPressed){
            setDefaultValues(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameDefault);
            setItems(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameDefault);
        } else {
            setDefaultValues(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameSaved);
            setItems(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameSaved);
        }
    }

    public void update() {
        if (attacking){
            attacking();
        }
        else if (keyH.upPressed || keyH.rightPressed || keyH.leftPressed || keyH.downPressed || keyH.enterPressed) {
            if (keyH.upPressed){
                direction = "up";
            }
            else if (keyH.downPressed){
                direction = "down";
            }
            else if (keyH.leftPressed){
                direction = "left";
            }
            else if (keyH.rightPressed){
                direction = "right";
            }

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // CHECK ATTACK
            int index = 999;
            attack(index); // 999 is a random number, it's not used in the method

            // CHECK MONSTERS' COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            contactMonster(monsterIndex);

            // CHECK EVENT
            gp.eHandler.checkEvent();

            // IF COLLISION IS TRUE, PLAYER CANNOT MOVE
            if(!collisionOn && !keyH.enterPressed) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    // collision checking anf moving player if collisionOn is true
                    case "right" -> worldX += speed;
                }
            }

            gp.keyH.enterPressed = false;

            spriteCounter++;
            if (spriteCounter > 10){
                if (spriteNum == 1){
                    spriteNum = 2;
                } else if(spriteNum == 2){
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
        if (invincible){
            invincibleCounter++;
            if (invincibleCounter > 60){ // More than 60 per second
                invincible = false;
                invincibleCounter = 0;
            }
        }
        if (life > maxLife){
            life = maxLife;
        }
        if (life <= 0){ // If player is dead
            gp.gameState = gp.gameOverState;
        }
    }

    public void attacking(){
        spriteCounter++; // for the animation

        if (spriteCounter <= 5){
            spriteNum = 1;
        }
        if (spriteCounter > 5 && spriteCounter <= 25){ // second attack sprite is displayed longer
            spriteNum = 2;

            // Save the current word x and y
            int currentWorldX = worldX;
            int currentWorldY = worldY; //
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust player's world x and y for the attackArea
            switch (direction){
                case "up" -> worldY -= attackArea.height;
                case "down" -> worldY += attackArea.height;
                case "left" -> worldX -= attackArea.width;
                case "right" -> worldX += attackArea.width;
            }

            // Attack area becomes solid area
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            // Check chukieka.ctuadventures.monster collision with the updated "hit box" od the player
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            damageMonster(monsterIndex);

            // Restore the solid area after checking
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        if (spriteCounter > 25){
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    public void pickUpObject(int i) {
        if (i != 999) {

            String objectName = gp.obj[i].name;

            if (inventory.size() != maxInventorySize) {
                switch (objectName) {
                    case "Key" -> {
                        inventory.add(gp.obj[i]);
                        hasKey++;
                        gp.obj[i] = null;
                    }
                    case "Door" -> {
                        if (hasKey > 0) {
                            gp.obj[i] = null;
                            hasKey--;
                            inventory.removeIf(item -> item instanceof OBJ_Key); // Delete the key from the inventory
                        }
                    }
                    case "Boots" -> {
                        gp.obj[i] = null;
                        if (gp.player.life != 6) {
                            gp.player.life += 1;
                        }
                    }
                    case "Pen" -> {
                        inventory.add(gp.obj[i]);
                        gp.obj[i] = null;
                    }
                    case "A" -> {
                        inventory.add(gp.obj[i]);
                        gp.gameState = gp.gameWinState; // WIN THE GAME
                        gp.obj[i] = null;
                    }
                    case "Coffee" -> {
                        speed += 2;
                        gp.obj[i] = null;
                    }
                }
            }
        }
    }


    public void attack(int i){
        if (gp.keyH.enterPressed){
            attacking = true;
        }
    }

    public void contactMonster(int i){
        if (i != 999){
            if (!invincible){
                life -= 1; // if touches the chukieka.ctuadventures.monster - half heart
                invincible = true;
            }
        }
    }

    public void damageMonster(int i){
        if (i != 999){
            if (!gp.monster[i].invincible){
                gp.monster[i].life -= currentWeapon.attackValue;
                gp.monster[i].invincible = true;
                gp.monster[i].damageReaction();

                if (gp.monster[i].life <= 0){
                    gp.monster[i].dying = true;
                    if (gp.monster[i].name.equals("Youtube")){
                        gp.obj[7] = new OBJ_Boots(gp);
                        gp.obj[7].worldX = gp.monster[i].worldX;
                        gp.obj[7].worldY = gp.monster[i].worldY;
                    } else {
                        gp.obj[7] = new OBJ_markA(gp);
                        gp.obj[7].worldX = gp.monster[i].worldX;
                        gp.obj[7].worldY = gp.monster[i].worldY;
                    }
                }
            }
        }
    }

    public void draw(Graphics2D g2){
        BufferedImage image = null;
        int tempScreenX = screenX; // temporary coords for adjusting the animation sprites
        int tempScreenY = screenY;

        switch (direction) {
            case "up" -> {
                if (!attacking){ // if not attacking then just walking animation
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                }
                if (attacking){ // if attacking then attack animation
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {image = attackUp1;}
                    if (spriteNum == 2) {image = attackUp2;}
                }
            }
            case "down" -> {
                if (!attacking){
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                }
                if (attacking){
                    if (spriteNum == 1) {image = attackDown1;}
                    if (spriteNum == 2) {image = attackDown2;}
                }
            }
            case "left" -> {
                if (!attacking){
                    if (spriteNum == 1) {image = left1;}
                    if (spriteNum == 2) {image = left2;}
                }
                if (attacking){
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) {image = attackLeft1;}
                    if (spriteNum == 2) {image = attackLeft2;}
                }
            }
            case "right" -> {
                if (!attacking){
                    if (spriteNum == 1) {image = right1;}
                    if (spriteNum == 2) {image = right2;}
                }
                if (attacking){
                    if (spriteNum == 1) {image = attackRight1;}
                    if (spriteNum == 2) {image = attackRight2;}
                }
            }
        }

        if (invincible){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); // Makes the player transparent when hit (in invincible time)
        }
        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // Reset the transparency of the graphics 2D
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // DEBUG FOR THE INVINCIBLE TIME
//        g2.setFont(new Font("Arial", Font.PLAIN, 26));
//        g2.setColor(Color.white);
//        g2.drawString("Invincible: " + invincibleCounter, 10, 400);
    }

}
