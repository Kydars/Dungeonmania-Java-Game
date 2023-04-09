package dungeonmania.entities.logical;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public abstract class Activatable extends Entity {
    private boolean isActivated;
    private List<Conductor> conductors;

    public Activatable(Position position) {
        super(position);
        isActivated = false;
    }

    public boolean isActivated() {
        return isActivated;
    }

    protected void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    protected List<Conductor> getConductors() {
        return conductors;
    }

    public void setConductors(List<Conductor> conductors) {
        this.conductors = conductors.stream()
                                    .filter(c -> Position.isAdjacent(c.getPosition(), getPosition()))
                                    .collect(Collectors.toList());
    }
}
