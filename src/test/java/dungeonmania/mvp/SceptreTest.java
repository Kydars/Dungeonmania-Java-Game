package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SceptreTest {

    @Test
    @Tag("18-1")
    @DisplayName("Test crafting sceptre with treasure and wood")
    public void craftTreasureWood() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_craftTreasureWood", "c_sceptreTest_craftTreasureWood");

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Check materials were depleted
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("18-2")
    @DisplayName("Test crafting sceptre with arrows and a key")
    public void craftArrowsKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_craftArrowsKey", "c_sceptreTest_craftArrowsKey");

        // Pick up arrow
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "arrow").size());

        // Pick up arrow
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Pick up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Check materials were depleted
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
    }

    @Test
    @Tag("18-3")
    @DisplayName("Test crafting sceptre with arrows and a key")
    public void craftSunStoneWood() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_craftSunStoneWood", "c_sceptreTest_craftSunStoneWood");

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Check sun stone used to replace treasure/key is retained after use
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
    }

    @Test
    @Tag("18-4")
    @DisplayName("Test attempt to craft with single sun stone and wood")
    public void failCraft() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_failCraft", "c_sceptreTest_failCraft");

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Build sceptre
        assertThrows(InvalidActionException.class, () ->
            dmc.build("sceptre")
        );
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());

        // Check materials were depleted
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
    }

    @Test
    @Tag("18-5")
    @DisplayName("Test mind control outside of bribe radius")
    public void mindControlRadius() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_mindControlRadius", "c_sceptreTest_mindControlRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);

        // Pick up 2 sun stones
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("18-6")
    @DisplayName("Test mind control outside of bribe radius")
    public void mindControlDuration() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_mindControlRadius", "c_sceptreTest_mindControlRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);

        // Pick up 2 sun stones
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());

        // walk away for two more ticks, wait for 3 tick mind control to end
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // walk into mercenary, a battle occurs
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @Tag("18-7")
    @DisplayName("Test mind control with assassin with guaranteed bribe failure")
    public void mindControlAssassin() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_mindControlAssassin", "c_sceptreTest_mindControlAssassin");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);

        // Pick up 2 sun stones
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(assId));

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());
    }
}
