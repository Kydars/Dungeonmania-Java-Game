package dungeonmania.entities.logical;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;

public class CoAndRule implements LogicalRule {

    @Override
    public boolean isActivated(Game game, List<Conductor> conductors) {
        List<Conductor> activated = conductors.stream()
                                              .filter(c -> c.isActivated())
                                              .collect(Collectors.toList());
        int activatedTick = (activated.size() == 0) ? 1 : activated.get(0).getActivatedTick();
        return activated.size() >= 2 && activated.stream().allMatch(c -> c.getActivatedTick() == activatedTick);
    }

}
