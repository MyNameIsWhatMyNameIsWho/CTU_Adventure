package chukieka.ctuadventures;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.object.OBJ_Weapon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.logging.log4j.LogManager; // To log the collision detector
import org.apache.logging.log4j.Logger;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    public boolean loadPressed = false;

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {} // Don't need

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // returns a number of the key that was pressed

        // TITLE STATE
        if (gp.gameState == gp.titleState){
            if (code == KeyEvent.VK_W) { // If pressed "W" and so on
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0){
                    gp.ui.commandNum = 2;
                }
            }
            else if (code == KeyEvent.VK_S){
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2){
                    gp.ui.commandNum = 0;
                }
            }
            else if (code == KeyEvent.VK_ENTER){
                if (gp.ui.commandNum == 0){ // If New Game
                    gp.gameState = gp.playState;
                    gp.resetPlayer();
                    gp.loadGame(loadPressed);
                } else if (gp.ui.commandNum == 1){ // If load
                    loadPressed = true;
                    gp.loadGame(loadPressed);
                    gp.resetPlayer();
                    gp.player.loadPlayer(loadPressed);
                    gp.ui.showMessage("Game loaded!");
                } else if (gp.ui.commandNum == 2){
                    System.exit(0);
                }
            }
        }

        // PLAY STATE
        if (gp.gameState == gp.playState){
            if (code == KeyEvent.VK_W) { // If pressed "W" and so on
                upPressed = true;
            }
            else if (code == KeyEvent.VK_S){
                downPressed = true;
            }
            else if (code == KeyEvent.VK_A){
                leftPressed = true;
            }
            else if (code == KeyEvent.VK_D){
                rightPressed = true;
            }
            else if (code == KeyEvent.VK_P){ // For pause mode
                gp.gameState = gp.pauseState;
            }
            else if (code == KeyEvent.VK_ENTER){
                for (Entity item : gp.player.inventory) { // If weapon is not in the inventory, player cannot fight
                    if (item instanceof OBJ_Weapon) {
                        enterPressed = true;
                        break;
                    }
                }
            }
            else if (code == KeyEvent.VK_E){
                gp.gameState = gp.dialogueState;
            }
            else if (code == KeyEvent.VK_L) { // If pressed "L"
                gp.saveGame();
                System.out.println("Game saved!");
            }
        }

        // PAUSE STATE
        else if (gp.gameState == gp.pauseState){
            if (code == KeyEvent.VK_P){ // For pause mode
                gp.gameState = gp.playState;
            }
        }

        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState){
            if (code == KeyEvent.VK_E){ // For dialogue mode
                gp.gameState = gp.playState;
            }
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) { // If pressed "W" and so on
            upPressed = false;
        }
        else if (code == KeyEvent.VK_S){
            downPressed = false;
        }
        else if (code == KeyEvent.VK_A){
            leftPressed = false;
        }
        else if (code == KeyEvent.VK_D){
            rightPressed = false;
        }
    }
}
