package dungeonmania;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import dungeonmania.response.models.DungeonResponse;

public class Persistence {
    public static void save(String name, Game game, DungeonResponse dungeonResponse) {
        ObjectOutputStream objectOutputStream = null;
        try {
            File file = new File("src/main/java/dungeonmania/savedGames/" + name);
            file.createNewFile();
            FileOutputStream fileStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileStream);
            objectOutputStream.writeObject(game);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File("src/main/java/dungeonmania/savedDungeonResponses/" + name);
            file.createNewFile();
            FileOutputStream fileStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileStream);
            objectOutputStream.writeObject(dungeonResponse);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Game loadGame(String name)
     throws IllegalArgumentException {
        Game game = null;
        try {
            FileInputStream fileStream = new FileInputStream("src/main/java/dungeonmania/savedGames/" + name);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
            game = (Game) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        return game;
    }

    public static DungeonResponse loadDungeonResponse(String name)
     throws IllegalArgumentException {
        DungeonResponse dungeonResponse = null;
        try {
            FileInputStream fileStream = new FileInputStream("src/main/java/dungeonmania/savedDungeonResponses/"
                + name);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
            dungeonResponse = (DungeonResponse) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        return dungeonResponse;
    }
}
