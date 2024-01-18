package chukieka.ctuadventures;

import chukieka.ctuadventures.monster.MON_professor;
import junit.framework.TestCase;
import chukieka.ctuadventures.entity.Entity;

public class CollisionCheckerTest extends TestCase {

    private CollisionChecker cChecker;
    private GamePanel gp;

    public void setUp() {
        // Create a GamePanel instance
        gp = new GamePanel();
        cChecker = new CollisionChecker(gp);
    }

    public void testCheckTileCollision() {
        // Create an entity and set its properties for testing
        Entity entity = new Entity(gp);
        entity.worldX = 10;
        entity.worldY = 10;
        // Set other necessary properties for entity and gamePanel

        // Call the method to be tested
        cChecker.checkTile(entity);

        // Assert the expected results
        assertTrue(entity.collisionOn); // Assert that collisionOn is true if a collision occurs
    }

    public void testCheckObjectCollision() {
        // Create an entity and set its properties for testing
        Entity entity = new Entity(gp);
        entity.worldX = 10;
        entity.worldY = 10;
        // Set other necessary properties for entity and gamePanel

        // Call the method to be tested
        int result = cChecker.checkObject(entity, false);

        // Assert the expected results
        assertEquals(999, result); // Assert the expected index value
    }

    public void testCheckMonsterCollision() {
        // CHECK IF MONSTER AND PLAYER INTERSECT
        gp.player.setDefaultValues(JsonPathConfig.pathToFolder + JsonPathConfig.jsonFileNameDefault);
        gp.player.worldX = 0;
        gp.player.worldY = 0;

        gp.monster[1] = new MON_professor(gp);
        gp.monster[1].worldX = 0;
        gp.monster[1].worldY = 0;



        boolean result = cChecker.checkPlayer(gp.monster[1]);
        assertTrue(result);
        assertTrue("Monster and player should intersect", result);
    }

}
