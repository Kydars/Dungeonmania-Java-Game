package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface OverlapAction {
    public void onOverlap(GameMap map, Entity entity);
}
