package dungeonmania.entities.logical;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends Logical {

    public SwitchDoor(Position position, LogicalRule rule) {
        super(position, rule);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return isActivated();
    }
}
