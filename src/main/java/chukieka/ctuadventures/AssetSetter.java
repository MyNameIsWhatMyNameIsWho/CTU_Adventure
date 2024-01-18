package chukieka.ctuadventures;

import chukieka.ctuadventures.monster.MON_professor;
import chukieka.ctuadventures.monster.MON_youtube;
import chukieka.ctuadventures.object.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.logging.log4j.LogManager; // To log the collision detector
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setObject(boolean loadPressed){
        if (loadPressed) {
            parsePositions(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameSaved);
        } else parsePositions(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameDefault);
    }

    public void setMonster(boolean loadPressed){
        if (loadPressed) {
            parsePositions(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameSaved);
        } else parsePositions(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameDefault);
    }

    public void parsePositions(String filePath) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

//            // Parse player positions and life
//            JSONObject playerJson = (JSONObject) jsonObject.get("player");
//            long playerWorldX = (long) playerJson.get("worldX");
//            long playerWorldY = (long) playerJson.get("worldY");
//           // long playerLife = (long) playerJson.get("lifes");
//
//            // Set player positions and life
//            gp.player.worldX = (int) playerWorldX;
//            gp.player.worldY = (int) playerWorldY;
////            gp.player.life = (int) playerLife;
////            gp.player.maxLife = (int) playerLife;

            // Parse monsters
            JSONArray monstersArray = (JSONArray) jsonObject.get("monsters");
            for (int i = 0; i < monstersArray.size(); i++) {
                JSONObject monsterJson = (JSONObject) monstersArray.get(i);
                String monsterName = (String) monsterJson.get("name");
                int monsterWorldX = ((Long) monsterJson.get("worldX")).intValue();
                int monsterWorldY = ((Long) monsterJson.get("worldY")).intValue();
                int monsterLife = ((Long) monsterJson.get("life")).intValue();

                // Create a new instance of the MON_youtube monster
                if (monsterName.equals("Youtube")){
                    MON_youtube youtube = new MON_youtube(gp);
                    youtube.worldX = monsterWorldX * gp.tileSize;
                    youtube.worldY = monsterWorldY * gp.tileSize;
                    // Assign the monster to the monster array
                    youtube.life = monsterLife;
                    gp.monster[i] = youtube;
                } else {
                    MON_professor professor = new MON_professor(gp);
                    professor.worldX = monsterWorldX * gp.tileSize;
                    professor.worldY = monsterWorldY * gp.tileSize;
                    professor.life = monsterLife;
                    gp.monster[i] = professor;
                }
            }

            JSONArray objectsArray = (JSONArray) jsonObject.get("objects");
            for (int i = 0; i < objectsArray.size(); i++) {
                JSONObject objectJson = (JSONObject) objectsArray.get(i);
                String objectName = (String) objectJson.get("name");
                int objectWorldX = ((Long) objectJson.get("worldX")).intValue();
                int objectWorldY = ((Long) objectJson.get("worldY")).intValue();

                // Create a new instance of the OBJ_Key object
                if (objectName.equals("Key")){
                    OBJ_Key objKey = new OBJ_Key(gp);
                    objKey.worldX = objectWorldX * gp.tileSize;
                    objKey.worldY = objectWorldY * gp.tileSize;

                    // Assign the object to the gp.obj array
                    gp.obj[i] = objKey;
                } else if (objectName.equals("Door")){
                    OBJ_Door objDoor = new OBJ_Door(gp);
                    objDoor.worldX = objectWorldX * gp.tileSize;
                    objDoor.worldY = objectWorldY * gp.tileSize;

                    // Assign the object to the gp.obj array
                    gp.obj[i] = objDoor;
                } else if (objectName.equals("Boots")) {
                    OBJ_Boots objBoots = new OBJ_Boots(gp);
                    objBoots.worldX = objectWorldX * gp.tileSize;
                    objBoots.worldY = objectWorldY * gp.tileSize;

                    // Assign the object to the gp.obj array
                    gp.obj[i] = objBoots;
                } else if (objectName.equals("Pen")) {
                    OBJ_Weapon objPen = new OBJ_Weapon(gp);
                    objPen.worldX = objectWorldX * gp.tileSize;
                    objPen.worldY = objectWorldY * gp.tileSize;

                    // Assign the object to the gp.obj array
                    gp.obj[i] = objPen;
                } else if (objectName.equals("Coffee")){
                    OBJ_Coffee objCoffee = new OBJ_Coffee(gp);
                    objCoffee.worldX = objectWorldX * gp.tileSize;
                    objCoffee.worldY = objectWorldY * gp.tileSize;

                    // Assign the object to the gp.obj array
                    gp.obj[i] = objCoffee;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
