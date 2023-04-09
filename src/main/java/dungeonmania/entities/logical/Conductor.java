package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.util.Position;

public  abstract class Conductor extends Activatable {
    private int activatedTick;
    private Set<String> sources = new TreeSet<>();
    private List<Logical> observers = new ArrayList<>();

    public Conductor(Position position) {
        super(position);
    }

    public void setObservers(List<Logical> observers) {
        this.observers = observers.stream()
                                  .filter(o -> Position.isAdjacent(o.getPosition(), getPosition()))
                                  .collect(Collectors.toList());
    }

    public void notifyObservers(Game game) {
        observers.forEach(o -> o.update(game));
    }

    public int getActivatedTick() {
        return activatedTick;
    }

    public boolean contains(String source) {
        return sources.contains(source);
    }

    public void addSource(Game game, String newSource) {
        sources.add(newSource);
        if (sources.size() == 1) {
            setActivated(true);
            notifyObservers(game);
        }
        activatedTick = game.getTick();
        getConductors().stream()
                       .filter(c -> !c.contains(newSource) && c.isConductive())
                       .forEach(c -> c.addSource(game, newSource));
    }

    public void removeSource(Game game, String source) {
        sources.remove(source);
        if (sources.size() == 0) {
            setActivated(false);
            notifyObservers(game);
        }
        notifyObservers(game);
        getConductors().stream()
                       .filter(c -> c.contains(source))
                       .forEach(c -> c.removeSource(game, source));
    }

    public boolean isConductive() {
        return true;
    }
}
