package dungeonmania.goals;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ExitGoal extends Goal {
    public ExitGoal() {
        super();
    }

    @Override
    public boolean achieved(Game game, GameMap map, Player player) {
        if (player == null) return false;
        Position pos = player.getPosition();
        List<Exit> es = map.getEntities(Exit.class);
        if (es == null || es.size() == 0) return false;
        return es
            .stream()
            .map(Entity::getPosition)
            .anyMatch(pos::equals);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game, game.getMap(), game.getPlayer())) return "";
        return ":exit";
    }
}
