package chukieka.ctuadventures.object;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

public class OBJ_Weapon extends Entity {

    public OBJ_Weapon(GamePanel gp) {
        super(gp);

        name = "Pen";
        down1 = setup("/objects/pen", gp.tileSize, gp.tileSize);
        attackValue = 1;
    }
}
