package chukieka.ctuadventures.object;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

public class OBJ_Coffee extends Entity {

    public OBJ_Coffee(GamePanel gp){
        super(gp);

        name = "Coffee";
        down1 = setup("/objects/coffee", gp.tileSize, gp.tileSize);
    }
}
