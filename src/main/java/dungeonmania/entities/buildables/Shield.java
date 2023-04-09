package dungeonmania.entities.buildables;


import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;

public class Shield extends Buildable implements BattleItem {
    private double defence;
    private int durability;

    public Shield(int durability, double defence) {
        this.defence = defence;
        this.durability = durability;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
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
        durability--;
        if (durability <= 0) {
            game.useInventoryItem(this);
        }
    }
}
