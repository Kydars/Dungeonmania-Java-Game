package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public abstract class Entity implements Serializable {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private Position position;
    private Position previousPosition;
    private Position previousDistinctPosition;
    private String entityId;
    private int movementFactor = 1;

    public Entity(Position position) {
        this.position = position;
        this.previousPosition = position;
        this.previousDistinctPosition = null;
        this.entityId = UUID.randomUUID().toString();
    }

    public boolean canMoveOnto(GameMap map, Entity entity) {
        return false;
    }

    public Position getPosition() {
        return position;
    }

    public List<Position> getCardinallyAdjacentPositions() {
        return getPosition().getCardinallyAdjacentPositions();
    }

    public Position getPreviousPosition() {
        return previousPosition;
    }

    public Position getPreviousDistinctPosition() {
        return previousDistinctPosition;
    }

    public String getId() {
        return entityId;
    }

    public void setId(String id) {
        this.entityId = id;
    }

    public void setPosition(Position position) {
        previousPosition = this.position;
        this.position = position;
        if (!previousPosition.equals(this.position)) {
            previousDistinctPosition = previousPosition;
        }
    }

    public int getMovementFactor() {
        return movementFactor;
    }

    public void setMovementFactor(int movementFactor) {
        this.movementFactor = movementFactor;
    }
}
