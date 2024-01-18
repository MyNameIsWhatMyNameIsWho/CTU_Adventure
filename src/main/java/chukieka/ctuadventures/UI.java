package chukieka.ctuadventures;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.object.OBJ_Heart;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager; // To log the collision detector
import org.apache.logging.log4j.Logger;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    BufferedImage heart_full, heart_half, heart_blank;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;

    public UI(GamePanel gp){
        this.gp = gp;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD , 80);

        // CREATE PLAYER'S HP OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }

    public void showMessage(String text){
        message = text;

        int x = gp.tileSize*2 + gp.tileSize;
        int y = gp.tileSize/2 + gp.tileSize;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        g2.drawString(message, x, y);
    }

    public void draw(Graphics2D g2){
        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // TITLE STATE
        if (gp.gameState == gp.titleState){
            drawTitleScreen();
        }

        // PLAY STATE
        if (gp.gameState == gp.playState){
            // Do play state
            drawPlayerLife();

        // PAUSE STATE
        } else if (gp.gameState == gp.pauseState){
            drawPauseScreen();
            drawPlayerLife();
        }

        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }

        // INVENTORY
        if (gp.gameState == gp.dialogueState){
            drawInventory();
        }

        // GAME OVER STATE
        if (gp.gameState == gp.gameOverState){
            drawGameOverScreen();
        }

        // WIN STATE
        if (gp.gameState == gp.gameWinState){
            drawWinScreen();
        }
    }

    public void drawWinScreen(){
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWight, gp.screenHeight);

        int x, y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

        text = "AWESOME";
        // SHADOW
        g2.setColor(Color.black);
        x = getXForCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);

        // LETTERS
        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);

        text = "You did it! You got A!";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60f));
        x = getXForCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);

        text = "Go relax now :D";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50f));
        x = getXForCenteredText(text);
        y += gp.tileSize * 5;
        g2.drawString(text, x, y);

    }

    public void drawGameOverScreen(){
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWight, gp.screenHeight);

        int x, y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

        text = "Game Over";
        // SHADOW
        g2.setColor(Color.black);
        x = getXForCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);

        // LETTERS
        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);

        if (gp.player.life > 0){
            // Reason of dying
            text = "Too slow! Time is gone";
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60f));
            x = getXForCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
        } else {
            // Reason of dying
            text = "Too weak! You were killed";
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60f));
            x = getXForCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
        }

        // Back to the title screen
        text = "Restart the Game";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50f));
        x = getXForCenteredText(text);
        y += gp.tileSize * 5;
        g2.drawString(text, x, y);
    }

    public void drawPlayerLife(){

        //gp.player.life = 2; CHECK, TODO: Delete

        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;

        // DRAW BLANK HEART
        while (i < gp.player.maxLife/2){ // divided by 2, cuz 2 is one full heart
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }

        // RESET
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;

        // DRAW CURRENT LIFE
        while (i < gp.player.life){
            g2.drawImage(heart_half, x, y, null);
            i++;
            if (i < gp.player.life){
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += gp.tileSize;
        }
    }

    public void drawTitleScreen(){
        // the background
        g2.setColor(new Color(70, 120, 180));
        g2.fillRect(0, 0, gp.screenWight, gp.screenHeight);

        // Title name
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
        String text = "CTU Adventure";
        int x = getXForCenteredText(text);
        int y = gp.tileSize * 3;

        // SHADOW
        g2.setColor(Color.black);
        g2.drawString(text, x+5, y+5);

        // Main color
        g2.setColor(Color.white);
        g2.drawString(text, x, y);

        // Menu
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));

        text = "New Game";
        x = getXForCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);

        if (commandNum == 0){
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "Load Game";
        x = getXForCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);

        if (commandNum == 1){
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "Quit";
        x = getXForCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);

        if (commandNum == 2){
            g2.drawString(">", x - gp.tileSize, y);
        }

    }

    public void drawPauseScreen(){
        String text = "PAUSED";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }

    public void drawDialogueScreen(){
        int x = gp.tileSize*2 + gp.tileSize;
        int y = gp.tileSize/2 + gp.tileSize;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        for (String line : currentDialogue.split("\n")){
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public int getXForCenteredText(String text){

        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWight/2 - length/2;
    }

    public void drawInventory(){
        int frameX = gp.tileSize * 9, frameY = gp.tileSize, frameWidth = gp.tileSize * 6, frameHeight = gp.tileSize * 5;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // SLOT
        final int slotXstart = frameX + 20;
        int slotX = slotXstart;
        final int slotYstart = frameY + 20;
        int slotY = slotYstart;

        // DRAW PLAYER'S ITEMS
        for (int i = 0; i < gp.player.inventory.size(); i++) {
            g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);

            slotX += gp.tileSize;
            if (i == 4){
                slotX = slotXstart;
                slotY += gp.tileSize;
            }
        }
    }

    public void drawSubWindow(int x, int y, int width, int height){
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y ,width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width - 10, height - 10, 25, 25 );
    }
}

