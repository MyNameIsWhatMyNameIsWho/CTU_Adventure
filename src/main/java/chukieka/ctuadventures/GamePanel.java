package chukieka.ctuadventures;

import chukieka.ctuadventures.data.SaveLoad;
import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.entity.Player;
import chukieka.ctuadventures.tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;

// For parsing the JSON file
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;

public class GamePanel extends JPanel implements Runnable { // subclass of the JPanel, GamePanel has all of the function
    // SCREEN SETTINGS

    final int originalTileSize = 16; // 16x16 chukieka.ctuadventures.tile (Variables marked as final can't be reassigned).
    final int scale = 3;
    public int tileSize = originalTileSize * scale; // 16*3 = 48 - common size for 2D games (public to access from the other package)
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWight = tileSize * maxScreenCol; // 768 px
    public final int screenHeight = tileSize * maxScreenRow; // 576 px

    // WORLD SETTINGS

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // FPS, TIMER
    int FPS = 60;
    // Define variables to store minutes and seconds
    public int gameTimeInSeconds = 0;
    long gameTimeElapsed = 0; // Variable to store the elapsed game time
    public int gameOverTimeInSeconds;

    TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Thread gameThread; // for updating (working with) the time
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    SaveLoad saveLoad = new SaveLoad(this);

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity[] obj = new Entity[15]; // 10 obj in the same time is enough for this small game
    public Entity[] monster = new Entity[15]; // number of monsters that can be displayed AT THE SAME TIME
    ArrayList<Entity> entityList = new ArrayList<>(); // array for the order of the rendering

    // GAME STATE
    public int gameState;
    public final int titleState = 0; // Menu
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public int gameWinState = 4;
    public final int gameOverState = 6;


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWight, screenHeight)); // set the size of the class JPanel
        this.setBackground(Color.DARK_GRAY); // not necessary
        this.setDoubleBuffered(true); // just to help improve game's rendering performance
        this.addKeyListener(keyH); // so this panel can recognise the key input
        this.setFocusable(true); // so the panel can "focus" to receive the key
    }

    public void resetPlayer() {
        player = new Player(this, keyH);
    }

    public void setupGame() {
        gameState = titleState; // play state from the beginning of the game
        gameOverTimeInSeconds = readGameOverTimeFromJSON();
        gameTimeElapsed = readGameTimeElapsed();
    }

    public void startGameThread() {
        gameThread = new Thread(this); // this
        gameThread.start();
    }

    public void run() {
        double drawInterval = (double) 1000000000 / FPS; // Because of nanoseconds: 100000000ns = 1s; Draw the
                                                         // screen every 0,01666 sec.
        long gameTimeStartTime = gameTimeElapsed; // Variable to store the start time of the game
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;

                // Perform necessary operations using the updated gameTimeElapsed
                if (gameState == playState || gameState == dialogueState) {
                    if (gameTimeStartTime == 0) {
                        gameTimeStartTime = currentTime; // Start the game time
                    } else {
                        gameTimeElapsed = currentTime - gameTimeStartTime; // Calculate the elapsed game time
                        gameTimeInSeconds = (int) (gameTimeElapsed / 1_000_000_000L); // Convert to seconds
                    }
                } else {
                    gameTimeStartTime = 0; // Reset the game time start when not in playState
                }
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == titleState) {
            // Nothing to update
        } else if (gameState == playState) {
            player.update();
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    if (monster[i].alive && !monster[i].dying) {
                        monster[i].update();
                    }
                    if (!monster[i].alive) {
                        monster[i] = null;
                    }
                }
            }
            if (gameTimeInSeconds == gameOverTimeInSeconds) {
                gameState = gameOverState;
            }
        } else if (gameState == pauseState) {
            //nothing
        }
    }

    private int readGameOverTimeFromJSON() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(new FileReader(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameDefault));
            return Integer.parseInt(config.get("gameOverTimeInSeconds").toString());
        } catch (IOException | ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 15; // Default value if the JSON file couldn't be read or the value couldn't be parsed
    }

    private int readGameTimeElapsed() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(new FileReader(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameSaved));
            return Integer.parseInt(config.get("elapsedTime").toString());
        } catch (IOException | ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0; // Default value if the JSON file couldn't be read or the value couldn't be parsed
    }

    public void paintComponent(Graphics g) { // Graphics has many functions to draw objects on the screen
        super.paintComponent(g); //formality to use paintComponent class
        Graphics2D g2 = (Graphics2D) g; // Changes g to g2

        // TITLE SCREEN
        if (gameState == titleState) {
            ui.draw(g2);
        } else {
            // TILE
            tileM.draw(g2);

            // ADD ENTITIES TO THE LIST
            entityList.add(player);
            // next can be NPCs
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    entityList.add(obj[i]);
                }
            }
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    entityList.add(monster[i]);
                }
            }

            // Draw the game time in minutes and seconds format
            int minutes = gameTimeInSeconds / 60;
            int seconds = gameTimeInSeconds % 60;
            String timeString;

            if (gameState == gameOverState || gameState == titleState)
                timeString = ""; // To show off the timer when game is over.
            else {
                timeString = String.format("Time: %02d:%02d", minutes, seconds);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.drawString(timeString, 13 * tileSize, 40);
            }


            // SORT
            entityList.sort(Comparator.comparingInt(e -> e.worldY));

            // DRAW ENTITIES
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).draw(g2);
            }

            // EMPTY ENTITY LIST
            entityList.clear();

            // UI
            ui.draw(g2);
        }

        g2.dispose(); // formality to finish and save some memory
    }

    public void saveGame() {
        JSONObject gameData = new JSONObject();

        // Save monster positions
        JSONArray monsterArray = new JSONArray();
        for (Entity monster : monster) {
            if (monster != null) {
                JSONObject monsterData = new JSONObject();
                monsterData.put("worldX", monster.worldX / tileSize);
                monsterData.put("worldY", monster.worldY / tileSize);
                monsterData.put("name", monster.name);
                monsterData.put("life", monster.life);
                monsterArray.add(monsterData);
            }
        }
        gameData.put("monsters", monsterArray);

        // Save player's inventory items
        JSONArray inventoryArray = new JSONArray();
        for (Entity item : player.inventory) {
            if (item != null) {
                JSONObject itemData = new JSONObject();
                itemData.put("name", item.name);
                // Add any other item data you want to save
                inventoryArray.add(itemData);
            }
        }
        gameData.put("inventory", inventoryArray);


        // Save object positions
        JSONArray objectArray = new JSONArray();
        for (Entity object : obj) {
            if (object != null) {
                JSONObject objectData = new JSONObject();
                objectData.put("worldX", object.worldX / tileSize);
                objectData.put("worldY", object.worldY / tileSize);
                objectData.put("name", object.name);
                objectArray.add(objectData);
            }
        }
        gameData.put("objects", objectArray);

        // Save other player's and game state
        gameData.put("worldX", player.worldX / tileSize);
        gameData.put("worldY", player.worldY / tileSize);
        gameData.put("lifes", player.life);
        gameData.put("speed", player.speed);
        gameData.put("elapsedTime", gameTimeInSeconds); // Save the elapsed game time
        gameData.put("gameOverTimeInSeconds", gameOverTimeInSeconds); // Save the game over time

        // Save the game data to a JSON file
        try (FileWriter fileWriter = new FileWriter(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameSaved)) {
            fileWriter.write(gameData.toJSONString());
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame(boolean loadPressed) {
        if (!loadPressed) {
            aSetter.setObject(false); // "start" the objects
            aSetter.setMonster(false); // "start" the monsters
        } else {
            aSetter.setObject(true); // "start" the objects
            aSetter.setMonster(true); // "start" the monsters
        }
    }
}