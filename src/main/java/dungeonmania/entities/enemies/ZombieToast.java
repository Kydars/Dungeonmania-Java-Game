package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;
    private Movement randomMovement = new RandomMovement();

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    @Override
    public boolean move(Game game, GameMap map) {
        if (!super.move(game, map)) {
            return false;
        }
        randomMovement.move(map, this);
        return true;
    }
}
