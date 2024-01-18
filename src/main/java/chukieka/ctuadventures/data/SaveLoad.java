package chukieka.ctuadventures.data;

import chukieka.ctuadventures.GamePanel;

import java.io.*;

public class SaveLoad {
    GamePanel gp;

    public SaveLoad(GamePanel gp){
            this.gp = gp;
    }

    public void save(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));

            DataStorage ds = new DataStorage();

            ds.level = gp.player.level; //TODO #50 14m (level in /chukieka.ctuadventures.entity/Player is incorrect!)

            // Write the DataStorage object
            oos.writeObject(ds);
        } catch (IOException e) {
            System.out.println("Save Exception!");
        }
    }

    public void load(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));

            // Read the DataStorage object
            DataStorage ds = (DataStorage)ois.readObject();

            gp.player.level = ds.level;

        } catch (IOException e) {
            System.out.println("Load Exception!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
