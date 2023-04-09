package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;
    public static final int DEFAULT_MIND_CONTROL_DURATION = 3;


    private int mindControlDuration = DEFAULT_MIND_CONTROL_DURATION;
    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private int endOfControl = -1;
    private boolean allied = false;
    private Movement alliedMovement = new AlliedMovement();
    private Movement hostileMovement = new HostileMovement();

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            int mindControlDuration) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.mindControlDuration = mindControlDuration;
    }

    public boolean isAllied() {
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied) return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    protected boolean canBeBribed(Player player) {
        return (isWithinRadius(player.getPosition(), getPosition())
            && player.countEntityOfType(Treasure.class) >= bribeAmount)
            || playerHasSceptre(player);
    }

    private boolean isWithinRadius(Position position1, Position position2) {
        int xDist = Math.abs(position1.getX() - position2.getX());
        int yDist = Math.abs(position1.getY() - position2.getY());
        return (bribeRadius >= Math.max(xDist, yDist));
    }

    /**
     * bribe the merc
     */
    protected void bribe(Player player, int tickCount) {
        if (playerHasSceptre(player)) {
            endOfControl = tickCount  + mindControlDuration;
            onTick(tickCount);
        } else {
            for (int i = 0; i < bribeAmount; i++) {
                player.use(Treasure.class);
            }
        }
    }

    protected boolean playerHasSceptre(Player player) {
        return player.countEntityOfType(Sceptre.class) >= 1;
    }

    public void onTick(int tickCount) {
        if (tickCount == endOfControl) {
            allied = false;
        }
    }

    @Override
    public void interact(Player player, Game game) {
        allied = true;
        bribe(player, game.getTick());
    }

    @Override
    public boolean move(Game game, GameMap map) {
        if (!super.move(game, map) && !(allied && map.cardinallyAdjacentToPlayer(super.getPosition()))) {
            return false;
        }
        if (allied) {
            alliedMovement.move(map, this);
        } else {
            hostileMovement.move(map, this);
        }
        super.setInitialTick(game.getTick());
        return true;
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && canBeBribed(player);
    }
}
