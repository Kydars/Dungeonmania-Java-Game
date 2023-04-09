package dungeonmania.entities.logical;

import java.util.List;

import dungeonmania.Game;

public class OrRule implements LogicalRule {

    @Override
    public boolean isActivated(Game game, List<Conductor> conductors) {
        return conductors.stream().anyMatch(c -> c.isActivated());
    }

}
