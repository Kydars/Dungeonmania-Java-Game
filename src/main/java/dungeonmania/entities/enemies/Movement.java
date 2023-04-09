package dungeonmania.entities.enemies;

import dungeonmania.map.GameMap;

public interface Movement {
    public void move(GameMap map, Enemy enemy);
}
