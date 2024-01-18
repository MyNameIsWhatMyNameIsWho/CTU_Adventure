package chukieka.ctuadventures;

import chukieka.ctuadventures.entity.Entity;

import org.apache.logging.log4j.LogManager; // To log the collision detector
import org.apache.logging.log4j.Logger;

public class CollisionChecker {

    GamePanel gp;
    private static final Logger logger = LogManager.getLogger(CollisionChecker.class); // Define logger

    public CollisionChecker(GamePanel gp){
        this.gp = gp;
    }

    public void checkTile(Entity entity){
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width; // coods of the corners of the "block" to check on collision and create the sides of the collision box
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize; // sides then
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        int tileNum1, tileNum2; // We need to check two tiles to make sure if character can/not pass

        switch (entity.direction) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize; // see if player is gonna hit the collision in his next move (by speed)
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    //logger.info("Player collided with a tile at position ({}, {})", entity.worldX, entity.worldY);
                }
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize; // see if player is gonna hit the collision in his next move (by speed)
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    //logger.info("Player collided with a tile at position ({}, {})", entity.worldX, entity.worldY);
                }
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize; // see if player is gonna hit the collision in his next move (by speed)
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    //logger.info("Player collided with a tile at position ({}, {})", entity.worldX, entity.worldY);
                }
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize; // see if player is gonna hit the collision in his next move (by speed)
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                    entity.collisionOn = true;
                    //logger.info("Player collided with a tile at position ({}, {})", entity.worldX, entity.worldY);
                }
            }
        }
    }

    public int checkObject(Entity entity, boolean player){
        int index = 999;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null){
                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's solid area position
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch (entity.direction) {
                    case "up" -> entity.solidArea.y -= entity.speed;
                    case "down" -> entity.solidArea.y += entity.speed;
                    case "left" -> entity.solidArea.x -= entity.speed;
                    case "right" -> entity.solidArea.x += entity.speed;
                }
                if (entity.solidArea.intersects(gp.obj[i].solidArea)) {  // If object cross the character
                    if (gp.obj[i].collision) {
                        entity.collisionOn = true;
                        //logger.info("Player collided with object {} at position ({}, {})", i, entity.worldX, entity.worldY);
                    }
                    if (player) {
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY; // reset the values
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    // NPC OR MONSTER COLLISION
    public int checkEntity(Entity entity, Entity[] target){
        int index = 999;

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null){
                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's solid area position
                target[i].solidArea.x = target[i].worldX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].worldY + target[i].solidArea.y;

                switch (entity.direction) {
                    case "up" -> entity.solidArea.y -= entity.speed;
                    case "down" -> entity.solidArea.y += entity.speed;
                    case "left" -> entity.solidArea.x -= entity.speed;
                    case "right" -> entity.solidArea.x += entity.speed;
                }
                if (entity.solidArea.intersects(target[i].solidArea)) {  // If object cross the character
                    if (target[i] != entity){ // this is to avoid detection itself as a collision target
                        entity.collisionOn = true;
                        //logger.info("Player collided with entity {} at position ({}, {})", i, entity.worldX, entity.worldY);
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY; // reset the values
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    public boolean checkPlayer(Entity entity) {

        boolean contactPlayer = false; // to let monster hit the player

        // Get entity's solid area position
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;

        // Get the object's solid area position
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch (entity.direction) {
            case "up" -> entity.solidArea.y -= entity.speed;
            case "down" -> entity.solidArea.y += entity.speed;
            case "left" -> entity.solidArea.x -= entity.speed;
            case "right" -> entity.solidArea.x += entity.speed;
        }
        if (entity.solidArea.intersects(gp.player.solidArea)) {  // If object cross the character
            entity.collisionOn = true;
            contactPlayer = true;
        }
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY; // reset the values
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }
}
