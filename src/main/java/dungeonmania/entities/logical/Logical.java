package dungeonmania.entities.logical;

import dungeonmania.Game;
import dungeonmania.util.Position;

public abstract class Logical extends Activatable {
    private LogicalRule rule;

    public Logical(Position position, LogicalRule rule) {
        super(position);
        this.rule = rule;
    }

    public void update(Game game) {
        if (rule.isActivated(game, getConductors())) {
            setActivated(true);
        } else {
            setActivated(false);
        }
    }
}
