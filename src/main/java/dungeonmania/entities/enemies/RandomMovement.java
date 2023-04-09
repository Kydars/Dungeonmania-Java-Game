package dungeonmania.entities.enemies;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class RandomMovement implements Movement, Serializable {
    @Override
    public void move(GameMap map, Enemy enemy) {
        Position nextPos;
        Random randGen = new Random();
        List<Position> pos = enemy.getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> map.canMoveTo(enemy, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            nextPos = enemy.getPosition();
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
        }
        map.moveTo(enemy, nextPos);
    }
}
