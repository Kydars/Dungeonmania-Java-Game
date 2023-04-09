package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.DestroyAction;
import dungeonmania.entities.Entity;
import dungeonmania.entities.OverlapAction;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable, DestroyAction, OverlapAction {
    private BattleStatistics battleStatistics;
    private int initialMovementTick = -1;

    public Enemy(Position position, double health, double attack) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
    }

    public void setInitialTick(int initialTick) {
        this.initialMovementTick = initialTick;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            map.battle(player, this);
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
        g.addEnemiesDestroyed();
    }

    public boolean move(Game game, GameMap map) {
        if (game.getTick() >= initialMovementTick + 2 * getMovementFactor() - 1) {
            initialMovementTick = game.getTick();
            return true;
        }
        return false;
    }

    @Override
    public double getHealth() {
        return battleStatistics.getHealth();
    }

    @Override
    public boolean isEnabled() {
        return battleStatistics.isEnabled();
    }
}
