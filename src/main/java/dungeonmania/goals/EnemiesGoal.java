package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;

public class EnemiesGoal extends Goal {
    public EnemiesGoal(int target) {
        super(target);
    }

    @Override
    public boolean achieved(Game game, GameMap map, Player player) {
        if (player == null) return false;
        return game.getEnemiesDestroyed() >= getTarget() && map.allSpawnersDestroyed();
    }

    @Override
    public String toString(Game game) {
        if (achieved(game, game.getMap(), game.getPlayer())) return "";
        return ":enemies";
    }
}
