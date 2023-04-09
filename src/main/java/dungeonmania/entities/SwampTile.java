package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends Entity implements OverlapAction, MovedAwayAction {
    private int movementFactor;
    public SwampTile(Position position, int movementFactor) {
        super(position);
        this.movementFactor = movementFactor;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        entity.setMovementFactor(movementFactor);
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        entity.setMovementFactor(1);
    }
}
