package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.logical.Conductor;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Conductor implements OverlapAction, MovedAwayAction {
    private List<Bomb> bombs = new ArrayList<>();

    public Switch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (isActivated()) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActivated(true);
            bombs.stream().forEach(b -> b.notify(map));
            notifyObservers(map.getGame());
            super.addSource(map.getGame(), getId());
        }
    }

    @Override
    public void addSource(Game game, String newSource) {
        return;
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActivated(false);
            notifyObservers(map.getGame());
            removeSource(map.getGame(), getId());
        }
    }

    @Override
    public boolean isConductive() {
        return isActivated();
    }
}
