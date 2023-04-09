package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class TimeTravellingPortal extends Entity implements OverlapAction, MovedAwayAction {
    private boolean isActive = false;
    private boolean isBroken = false;

    public TimeTravellingPortal(Position position) {
        super(position);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player && !isBroken) {
            isActive = true;
            isBroken = true;
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            isActive = false;
        }
    }
}
