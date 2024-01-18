package chukieka.ctuadventures.monster;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

import java.util.Random;

public class MON_youtube extends Entity {
    GamePanel gp;

    public MON_youtube(GamePanel gp){
        super(gp);

        this.gp = gp;

        type = 2;
        name = "Youtube";
        speed = 1;
        maxLife = 4;
        life = maxLife;

        solidArea.x = 0;
        solidArea.y = 9; // empty area of the sprite
        solidArea.width = 48;
        solidArea.height = 39;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage(){
        up1 = setup("/monster/youtube_down1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/youtube_down2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/youtube_down1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/youtube_down2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/youtube_down1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/youtube_down2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/youtube_down1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/youtube_down2", gp.tileSize, gp.tileSize);
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
         direction = gp.player.direction;
         speed += 1;
    }

}
