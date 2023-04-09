package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SunStoneTest {

    @Test
    @Tag("17-1")
    @DisplayName("Test picking up a sun stone")
    public void pickUp() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_pickUp", "c_sunStoneTest_pickUp");
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("17-2")
    @DisplayName("Test unlocking door with sun stone")
    public void unlockDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_unlockDoor", "c_sunStoneTest_unlockDoor");
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Unlock first door with key 1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Unlock first door with key 2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test crafting shield with sun stone")
    public void craftShield() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_craftShield", "c_sunStoneTest_craftShield");

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // Build shield
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());
    }

    @Test
    @Tag("17-4")
    @DisplayName("Test treasure goal completion with sun stones")
    public void treasureGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_treasureGoal", "c_sunStoneTest_treasureGoal");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }
}
