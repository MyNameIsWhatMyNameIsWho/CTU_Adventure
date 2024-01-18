package chukieka.ctuadventures;

import chukieka.ctuadventures.entity.Player;
import junit.framework.TestCase;
import chukieka.ctuadventures.GamePanel;
import chukieka.ctuadventures.KeyHandler;
import chukieka.ctuadventures.object.OBJ_Door;
import chukieka.ctuadventures.object.OBJ_Key;

public class PlayerTest extends TestCase {
    private Player player;
    private GamePanel gp;
    private KeyHandler keyH;

    public void setUp() {
        gp = new GamePanel();
        keyH = new KeyHandler(gp);
        player = new Player(gp, keyH);
    }

    public void testPickUpObject() {
        int initialInventorySize = player.inventory.size();
        int objectIndex = 1; // Index of the object to be picked up

        // Create an object to be picked up
        OBJ_Key key = new OBJ_Key(gp);

        // Set the object at the specified index in the game panel
        gp.obj[objectIndex] = key;

        // Call the pickUpObject method
        player.pickUpObject(objectIndex);

        // Assert that the object was added to the inventory
        assertEquals(initialInventorySize + 1, player.inventory.size());
        assertTrue(player.inventory.contains(key));
        assertNull(gp.obj[objectIndex]);
    }

    public void testOpenDoorWithKey() {
        // Create a GamePanel and KeyHandler object
        GamePanel gp = new GamePanel();
        KeyHandler keyHandler = new KeyHandler(gp);

        // Create a player object
        Player player = new Player(gp, keyHandler);

        // Add a key to the player's inventory
        OBJ_Key key = new OBJ_Key(gp);
        player.inventory.add(key);
        player.hasKey++;

        // Create a door object
        OBJ_Door door = new OBJ_Door(gp);
        gp.obj[0] = door;

        // Call the pickUpObject method to check if the player can open the door
        player.pickUpObject(0);

        // Assert that the door is null, indicating it was opened
        assertNull(gp.obj[0]);

    }
}
