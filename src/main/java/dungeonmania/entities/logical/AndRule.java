package dungeonmania.entities.logical;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;

public class AndRule implements LogicalRule {

    @Override
    public boolean isActivated(Game game, List<Conductor> conductors) {
        List<Conductor> activated = conductors.stream()
                                              .filter(c -> c.isActivated())
                                              .collect(Collectors.toList());
        return (activated.size() == conductors.size()) && (activated.size() >= 2);
    }

}
