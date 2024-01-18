package chukieka.ctuadventures.object;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

public class OBJ_markA extends Entity {

    public OBJ_markA(GamePanel gp){
        super(gp);

        name = "A";
        down1 = setup("/objects/a", gp.tileSize, gp.tileSize);
    }
}
