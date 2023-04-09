package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface MovedAwayAction {
    public void onMovedAway(GameMap map, Entity entity);
}
