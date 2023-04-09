package dungeonmania.entities.collectables;

import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Collectable extends Entity implements InventoryItem {

    public Collectable(Position position) {
        super(position);
    }

    public boolean isCollectable() {
        return true;
    }

    public void onPickUp(GameMap map) {
        map.removeNode(this);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
