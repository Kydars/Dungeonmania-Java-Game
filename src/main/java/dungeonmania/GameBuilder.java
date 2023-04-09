package dungeonmania;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalFactory;
import dungeonmania.map.GameMap;
import dungeonmania.map.GraphNode;
import dungeonmania.map.GraphNodeFactory;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

/**
 * GameBuilder -- A builder to build up the whole game
 * @author      Webster Zhang
 * @author      Tina Ji
 */
public class GameBuilder implements Serializable {
    private String configName;
    private String dungeonName;

    private JSONObject config;
    private JSONObject dungeon;

    public GameBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public GameBuilder setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
        return this;
    }

    public Game buildGame() {
        loadConfig();
        loadDungeon();
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }

        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        game.init();
        return game;
    }

    public Game buildRandomGame(int xStart, int yStart, int xEnd, int yEnd) {
        loadConfig();
        dungeon = generateRandomDungeon(xStart, yStart, xEnd, yEnd);
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }

        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        game.init();
        return game;
    }

    private void addNeighbours(Position position, List<Position> neighbours, int height, int width,
     boolean[][] maze, boolean isEmpty) {
        int x = position.getX();
        int y = position.getY();
        if ((x - 2 > 0) && (x - 2 < width - 1)) {
            if ((!isEmpty && !maze[x - 2][y]) || (isEmpty && maze[x - 2][y])) {
                neighbours.add(new Position(x - 2, y));
            }
        }
        if ((x + 2 < width - 1) && (x + 2 > 0)) {
            if ((!isEmpty && !maze[x + 2][y]) || (isEmpty && maze[x + 2][y])) {
                neighbours.add(new Position(x + 2, y));
            }
        }
        if ((y - 2 > 0) && (y - 2 < height - 1)) {
            if ((!isEmpty && !maze[x][y - 2]) || (isEmpty && maze[x][y - 2])) {
                neighbours.add(new Position(x, y - 2));
            }
        }
        if ((y + 2 < height - 1) && (y + 2 > 0)) {
            if ((!isEmpty && !maze[x][y + 2]) || (isEmpty && maze[x][y + 2])) {
                neighbours.add(new Position(x, y + 2));
            }
        }
     }

    private void addNeighbours(Position position, List<Position> neighbours, int height, int width,
     boolean[][] maze) {
        int x = position.getX();
        int y = position.getY();
        if (x - 1 > 0) {
            neighbours.add(new Position(x - 1, y));
        }
        if (x + 1 < width - 1) {
            neighbours.add(new Position(x + 1, y));
        }
        if (y - 1 > 0) {
            neighbours.add(new Position(x, y - 1));
        }
        if (y + 1 < height - 1) {
            neighbours.add(new Position(x, y + 1));
        }
    }

    private boolean noEmptyNeighbours(boolean[][] maze, List<Position> neighbours) {
        for (Position position : neighbours) {
            if (maze[position.getX()][position.getY()]) {
                return false;
            }
        }
        return true;
    }

    private JSONObject dungeonJsonObject(int width, int height, boolean[][] maze, Position start, Position end) {
        JSONArray entities = new JSONArray();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!maze[i][j]) {
                    JSONObject entity = new JSONObject();
                    entity.put("type", "wall");
                    entity.put("x", i + start.getX() - 1);
                    entity.put("y", j + start.getY() - 1);
                    entities.put(entity);
                }
            }
        }
        JSONObject player = new JSONObject();
        player.put("type", "player");
        player.put("x", start.getX());
        player.put("y", start.getY());
        entities.put(player);
        JSONObject exit = new JSONObject();
        exit.put("type", "exit");
        exit.put("x", end.getX());
        exit.put("y", end.getY());
        entities.put(exit);
        JSONObject goal = new JSONObject();
        goal.put("goal", "exit");
        dungeon = new JSONObject();
        dungeon.put("entities", entities);
        dungeon.put("goal-condition", goal);
        return dungeon;
    }

    private void performPrimsAlgorithm(boolean[][] maze, Position start, Position end, int height, int width) {
        maze[1][1] = true;
        List<Position> options = new ArrayList<Position>();
        addNeighbours(start, options, height, width, maze, false);
        Random random = new Random();
        while (!options.isEmpty()) {
            Position next = options.get(random.nextInt(options.size()));
            options.remove(next);
            List<Position> neighbours = new ArrayList<Position>();
            addNeighbours(next, neighbours, height, width, maze, true);
            if (!neighbours.isEmpty()) {
                Position neighbour = neighbours.get(random.nextInt(neighbours.size()));
                maze[next.getX()][next.getY()] = true;
                maze[(next.getX() + neighbour.getX()) / 2][(next.getY() + neighbour.getY()) / 2] = true;
                maze[neighbour.getX()][neighbour.getY()] = true;
            }
            addNeighbours(next, options, height, width, maze, false);
        }
        if (!maze[end.getX()][end.getY()]) {
            maze[end.getX()][end.getY()] = true;
            List<Position> neighbours = new ArrayList<Position>();
            addNeighbours(end, neighbours, height, width, maze);
            if (noEmptyNeighbours(maze, neighbours)) {
                Position neighbour = neighbours.get(random.nextInt(neighbours.size()));
                maze[neighbour.getX()][neighbour.getY()] = true;
            }
        }
    }

    private JSONObject generateRandomDungeon(int xStart, int yStart, int xEnd, int yEnd) {
        int width = xEnd - xStart + 3;
        int height = yEnd - yStart + 3;
        Position start = new Position(1, 1);
        Position end = new Position(width - 2, height - 2);
        boolean[][] maze = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                maze[i][j] = false;
            }
        }
        performPrimsAlgorithm(maze, start, end, height, width);
        return dungeonJsonObject(width, height, maze, new Position(xStart, yStart), new Position(xEnd, yEnd));
    }

    private void loadConfig() {
        String configFile = String.format("/configs/%s.json", configName);
        try {
            config = new JSONObject(FileLoader.loadResourceFile(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }

    private void loadDungeon() {
        String dungeonFile = String.format("/dungeons/%s.json", dungeonName);
        try {
            dungeon = new JSONObject(FileLoader.loadResourceFile(dungeonFile));

        } catch (IOException e) {
            dungeon = null;
        }
    }

    private void buildMap(Game game) {
        GameMap map = new GameMap();
        map.setGame(game);
        dungeon.getJSONArray("entities").forEach(e -> {
            JSONObject jsonEntity = (JSONObject) e;
            GraphNode newNode = GraphNodeFactory.createEntity(jsonEntity, game.getEntityFactory());
            Entity entity = newNode.getEntities().get(0);

            if (newNode != null)
                map.addNode(newNode);

            if (entity instanceof Player)
                map.setPlayer((Player) entity);
        });
        game.setMap(map);
    }

    public void buildGoals(Game game) {
        if (!dungeon.isNull("goal-condition")) {
            Goal goal = GoalFactory.createGoal(dungeon.getJSONObject("goal-condition"), config);
            game.setGoals(goal);
        }
    }
}
