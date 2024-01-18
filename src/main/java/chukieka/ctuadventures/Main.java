package chukieka.ctuadventures;

import javax.swing.*;

public class Main extends GamePanel {
    public static void main(String[] args) {

        JFrame window = new JFrame(); // New window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to close window properly
        window.setResizable(false); // cannot resize this window
        window.setTitle("You won't believe it, bro");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel); // add gamePanel to this window

        window.pack(); // to get window "packed"

        window.setLocationRelativeTo(null); // window will be at the centre of the screen
        window.setVisible(true); //obvious

        gamePanel.setupGame();
        gamePanel.startGameThread();

    }

    // test
}