package chukieka.ctuadventures.object;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

public class OBJ_Key extends Entity {

    public OBJ_Key(GamePanel gp){
        super(gp);

        name = "Key";
        down1 = setup("/objects/key", gp.tileSize, gp.tileSize);
    }
}
