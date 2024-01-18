package chukieka.ctuadventures.tile;

import chukieka.ctuadventures.GamePanel;
import chukieka.ctuadventures.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum; // For reading the maps from the file (.txt)

    public TileManager(GamePanel gp){
        this.gp = gp;
        tile = new Tile[10]; // amount of the types of the tiles
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/maps/world01.txt");
    }

    public void getTileImage(){
        setup(0, "grass", false);
        setup(1, "wall", true);
        setup(2, "water", true);
        setup(3, "earth", false);
        setup(4, "tree", true);
        setup(5, "sand", false);
    }

    public void setup(int index, String imagePath, boolean collision){
        UtilityTool uTool = new UtilityTool();

        try{
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imagePath + ".png"));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath){ // Func for reading the map.txt from file
        try{
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;
            int col = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line =  br.readLine(); // readLine - real the line of text.

                while (col < gp.maxWorldCol){
                    String  numbers[] = line.split(" "); // as in Python

                    int num = Integer.parseInt(numbers[col]); // change string from file to integer

                    mapTileNum[col][row] = num; // fill it up
                    col++;
                }
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){
        int worldCol = 0;
        int worldRow = 0;

        while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow){

            int tileNum = mapTileNum[worldCol][worldRow]; // exact chukieka.ctuadventures.tile (number) from file

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX; // for the camera and ground moving (proper character moving)
            int screenY = worldY - gp.player.worldY + gp.player.screenY;
            
            if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){
//                System.out.println("tileNum: " + tileNum);
//                System.out.println("plX: " + gp.player.worldX);
//                System.out.println("plY: " + gp.player.worldY);
                g2.drawImage(tile[tileNum].image, screenX, screenY, null); // for efficiency, draw only what we see.
            }

            worldCol++;

            if (worldCol == gp.maxWorldCol){
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
