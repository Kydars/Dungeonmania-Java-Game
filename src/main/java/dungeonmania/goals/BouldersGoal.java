package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;

public class BouldersGoal extends Goal {
    public BouldersGoal() {
        super();
    }

    @Override
    public boolean achieved(Game game, GameMap map, Player player) {
        if (player == null) return false;
        return map.allSwitchesActivated();
    }

    @Override
    public String toString(Game game) {
        if (achieved(game, game.getMap(), game.getPlayer())) return "";
        return ":boulders";
    }
}
