package dungeonmania.entities.logical;

import java.util.List;

import dungeonmania.Game;

public class XOrRule implements LogicalRule {

    @Override
    public boolean isActivated(Game game, List<Conductor> conductors) {
        return conductors.stream().filter(c -> c.isActivated()).count() == 1;
    }
}
