package dungeonmania.entities.enemies;

import java.io.Serializable;

import dungeonmania.map.GameMap;

public class HostileMovement implements Movement, Serializable {
    @Override
    public void move(GameMap map, Enemy enemy) {
        map.moveTo(enemy, map.dijkstraPathFind(enemy.getPosition(), map.getPlayerPosition(), enemy));
    }
}
