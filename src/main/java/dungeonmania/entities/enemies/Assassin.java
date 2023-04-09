package dungeonmania.entities.enemies;

import java.util.SplittableRandom;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final double DEFAULT_ATTACK = 10.0;
    public static final double DEFAULT_HEALTH = 10.0;
    public static final double DEFAULT_BRIBE_FAIL_RATE = 0.3;

    private double bribeFailRate = Assassin.DEFAULT_BRIBE_FAIL_RATE;

    public Assassin(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            double bribeFailRate, int mindControlDuration) {
        super(position, health, attack, bribeAmount, bribeRadius, mindControlDuration);
        this.bribeFailRate = bribeFailRate;
    }

    @Override
    public void interact(Player player, Game game) {
        SplittableRandom random = new SplittableRandom();
        if (playerHasSceptre(player) || random.nextDouble(0, 1) >= bribeFailRate) {
            super.interact(player, game);
        } else {
            bribe(player, game.getTick());
        }
    }
}
