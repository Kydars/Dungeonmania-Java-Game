package dungeonmania.entities.logical;

import java.util.List;

import dungeonmania.Game;

public interface LogicalRule {
    public boolean isActivated(Game game, List<Conductor> conductors);
}
