package chukieka.ctuadventures.monster;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

import java.util.Random;

public class MON_professor extends Entity {
    GamePanel gp;

    public MON_professor(GamePanel gp) {
        super(gp);

        this.gp = gp;

        type = 2;
        name = "Professor";
        speed = 3;
        maxLife = 10;
        life = maxLife;

        solidArea.x = 0;
        solidArea.y = 0; // empty area of the sprite
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage(){
        up1 = setup("/monster/professor_up1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/professor_up2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/professor_down1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/professor_down2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/professor_left1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/professor_left2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/professor_right1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/professor_right2", gp.tileSize, gp.tileSize);
    }

    public void setAction(){
        actionLockCounter++;

        if (actionLockCounter == 120){
            Random random = new Random();
            int i = random.nextInt(100)+1; // pick up a number from 1 to 100

            if (i <= 25) {
                direction = "up";
            }
            if (i > 25 && i <= 50) {
                direction = "down";
            }
            if (i > 50 && i <= 75) {
                direction = "left";
            }
            if (i > 75 && i <= 100) {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }

    public void damageReaction(){
        actionLockCounter = 0;
        if (gp.player.direction.equals("down")){
            direction = "up";
        }
        if (gp.player.direction.equals("up")){
            direction = "down";
        }
        if (gp.player.direction.equals("left")){
            direction = "right";
        }
        if (gp.player.direction.equals("right")){
            direction = "left";
        }
        speed += 0.1;
    }

}
