package chukieka.ctuadventures.object;

import chukieka.ctuadventures.entity.Entity;
import chukieka.ctuadventures.GamePanel;

public class OBJ_Boots extends Entity {

    public OBJ_Boots(GamePanel gp){
        super(gp);

        name = "Boots";
        down1 = setup("/objects/boots", gp.tileSize, gp.tileSize);
    }
}
