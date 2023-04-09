package dungeonmania;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONException;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

public class DungeonManiaController {
    private Game game = null;
    private DungeonResponse dungeonResponse = null;
    private List<Game> gameSnapshots = new ArrayList<>();
    private List<DungeonResponse> dungeonResponseSnapshots = new ArrayList<>();

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    public Game getGame() {
        return game;
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try {
            GameBuilder builder = new GameBuilder();
            game = builder.setConfigName(configName).setDungeonName(dungeonName).buildGame();
            dungeonResponse = ResponseBuilder.getDungeonResponse(game);
            gameSnapshots.clear();
            dungeonResponseSnapshots.clear();
            return dungeonResponse;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return dungeonResponse;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        saveSnapshot();
        dungeonResponse = ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
        return dungeonResponse;
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        saveSnapshot();
        dungeonResponse = ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
        if (game.playerOnTimeTravellingPortal()) {
            dungeonResponse = rewind(30);
        }
        return dungeonResponse;
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        saveSnapshot();
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }
        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        saveSnapshot();
        dungeonResponse = ResponseBuilder.getDungeonResponse(game.interact(entityId));
        return dungeonResponse;
    }

    public DungeonResponse saveGame(String name) {
        Persistence.save(name, game, dungeonResponse);
        return dungeonResponse;
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        game = Persistence.loadGame(name);
        dungeonResponse = Persistence.loadDungeonResponse(name);
        return dungeonResponse;
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return Stream.of(new File("src/main/java/dungeonmania/savedGames/")
            .listFiles())
            .map(File::getName)
            .collect(Collectors.toList());
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName)
     throws IllegalArgumentException {
        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }
        try {
            GameBuilder builder = new GameBuilder();
            game = builder.setConfigName(configName).setDungeonName("Random Dunegon").
            buildRandomGame(xStart, yStart, xEnd, yEnd);
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    private void saveSnapshot() {
        try {
            gameSnapshots.add((Game) DeepCopy.copy(game));
            dungeonResponseSnapshots.add((DungeonResponse) DeepCopy.copy(dungeonResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        Game oldGame = gameSnapshots.get(game.getRewindTick(ticks));
        oldGame.update(oldGame.getMap(), game.getMap(), game.getPlayerInventory());
        game = oldGame;
        return ResponseBuilder.getDungeonResponse(game);
    }
}
