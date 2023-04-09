package dungeonmania.entities.enemies;

import java.io.Serializable;

import dungeonmania.map.GameMap;

public class AlliedMovement implements Movement, Serializable {
    @Override
    public void move(GameMap map, Enemy enemy) {
        if (enemy.getCardinallyAdjacentPositions().contains(map.getPlayerPreviousPosition())
         || enemy.getCardinallyAdjacentPositions().contains(map.getPlayerPosition())) {
            if (map.getPlayerPreviousDistinctPosition() != null) {
                map.moveTo(enemy, map.getPlayerPreviousDistinctPosition());
            }
        } else {
            map.moveTo(enemy, map.dijkstraPathFind(enemy.getPosition(), map.getPlayerPosition(), enemy));
        }
    }
}
