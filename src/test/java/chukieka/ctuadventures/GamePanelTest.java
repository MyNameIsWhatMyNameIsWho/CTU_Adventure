package chukieka.ctuadventures;

import junit.framework.TestCase;

public class GamePanelTest extends TestCase {

    public void testGameStateChangedToGameOverStateWhenTimeIsGone() {
        // Create an instance of the GamePanel
        GamePanel gamePanel = new GamePanel();

        // Set the game state to playState
        gamePanel.gameState = gamePanel.playState;

        // Set the current game time to the game over time
        gamePanel.gameTimeInSeconds = gamePanel.gameOverTimeInSeconds;

        // Call the update method to simulate the game loop
        gamePanel.update();

        // Check if the game state has changed to gameOverState
        assertEquals(gamePanel.gameOverState, gamePanel.gameState);
    }
}
