package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;

public class TreasureGoal extends Goal {
    public TreasureGoal(int target) {
        super(target);
    }

    @Override
    public boolean achieved(Game game, GameMap map, Player player) {
        if (player == null) return false;
        return game.getInitialTreasureCount() - map.getTreasureSize() >= getTarget();
    }

    @Override
    public String toString(Game game) {
        if (achieved(game, game.getMap(), game.getPlayer())) return "";
        return ":treasure";
    }
}
