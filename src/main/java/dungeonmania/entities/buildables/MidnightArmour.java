package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;

public class MidnightArmour extends Buildable implements BattleItem {
    public static final double DEFAULT_MIDNIGHT_DEFENCE = 2;
    public static final double DEFAULT_MIDNIGHT_ATTACK = 2;

    private double defence;
    private double attack;
    private int durability = 1;

    public MidnightArmour(double attack, double defence) {
        this.defence = defence;
        this.attack = attack;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            attack,
            defence,
            1,
            1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public void use(Game game) {
        return;
    }
}
