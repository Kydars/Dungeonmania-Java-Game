package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;

public class AndGoal extends Goal {
    public AndGoal(Goal goal1, Goal goal2) {
        super(goal1, goal2);
    }

    @Override
    public boolean achieved(Game game, GameMap map, Player player) {
        if (player == null) return false;
        return getGoal1().achieved(game, map, player) && getGoal2().achieved(game, map, player);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game, game.getMap(), game.getPlayer())) return "";
        return "(" + getGoal1().toString(game) + " AND " + getGoal2().toString(game) + ")";
    }
}
