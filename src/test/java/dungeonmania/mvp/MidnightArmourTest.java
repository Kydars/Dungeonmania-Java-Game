package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MidnightArmourTest {
    @Test
    @Tag("19-1")
    @DisplayName("Test crafting midnight armour")
    public void craft() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_craft", "c_midnightArmourTest_craft");

        // Pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Pick up sun_stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Build midnight armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // Check materials were depleted
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
    }

    @Test
    @Tag("19-2")
    @DisplayName("Test crafting midnight armour")
    public void failCraft() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_failCraft", "c_midnightArmourTest_failCraft");

        // Pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Pick up sun_stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Check building throws an error since there are zombies
        assertThrows(InvalidActionException.class, () ->
            dmc.build("midnight_armour")
        );
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());

        // Check materials were not depleted
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
    }

    @Test
    @Tag("19-3")
    @DisplayName("Test druability of midnight armour")
    public void durability() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_durability", "c_midnightArmourTest_durability");

        // Pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Pick up sun_stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Build midnight armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // Pick up key and unlock door
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Fight ten mercenaries
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Check midnight armour was used in last battle
        BattleResponse lastBattle = res.getBattles().get(res.getBattles().size() - 1);
        assertEquals(1, lastBattle.getBattleItems().size());
    }

    @Test
    @Tag("19-4")
    @DisplayName("Test using midnight armour")
    public void battle() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        String config = "c_midnightArmourTest_battle";
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_battle", config);

        // Pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Pick up sun_stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Build midnight armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // Pick up key and unlock door
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Battle assassin
        res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        double enemyAttack = Integer.parseInt(TestUtils.getValueFromConfigFile("assassin_attack", config));
        double defenceEffect = Integer.parseInt(TestUtils.getValueFromConfigFile("midnight_armour_defence", config));
        double playerBaseAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", config));
        double attackEffect = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_attack", config));
        double expectedDamage = (enemyAttack - defenceEffect) / 10;
        // Delta health is negative so take negative here
        assertEquals(expectedDamage, -firstRound.getDeltaCharacterHealth(), 0.001);
        assertEquals((playerBaseAttack + attackEffect) / 5, -firstRound.getDeltaEnemyHealth(), 0.001);
    }
}
