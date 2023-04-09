package dungeonmania.goals;

import java.io.Serializable;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;

public abstract class Goal implements Serializable {
    private int target;
    private Goal goal1;
    private Goal goal2;

    public Goal() { }

    public Goal(int target) {
        this.target = target;
    }

    public Goal(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public int getTarget() {
        return target;
    }

    public Goal getGoal1() {
        return goal1;
    }

    public Goal getGoal2() {
        return goal2;
    }

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public abstract boolean achieved(Game game, GameMap map, Player player);
    public abstract String toString(Game game);
}
