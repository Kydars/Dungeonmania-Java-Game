package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TimeTravelTest {

    @Test
    @Tag("18-1")
    @DisplayName("Test time turner is removed from map")
    public void timeTurnerIsPickedUp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravelTest_mercenaryPosition", "c_timeTravelTest");

        // Pick up Time Turner
        res = dmc.tick(Direction.DOWN);

        // Check that Time Turner does not exist on map and is in player inventory
        assertEquals(0, TestUtils.getEntities(res, "time_turner").size());
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());
    }

    @Test
    @Tag("18-2")
    @DisplayName("Test time travelling one ticks")
    public void timeTravelTimeTurnerOneTick() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravelTest_mercenaryPosition", "c_timeTravelTest");
        Position initialPosition = TestUtils.getPlayerPos(res);
        Position initialMercPosition = getMercPos(res);

        res = dmc.tick(Direction.DOWN);
        res = dmc.rewind(1);

        // Check that player position does not change
        assertNotEquals(initialPosition, TestUtils.getPlayerPos(res));

        // Test that mercenary position reverts back
        assertEquals(initialMercPosition, getMercPos(res));
    }

    @Test
    @Tag("18-3")
    @DisplayName("Test time travelling five ticks")
    public void timeTravelTimeTurnerFiveTicks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse start = dmc.newGame("d_timeTravelTest_mercenaryPosition", "c_timeTravelTest");
        Position initialPosition = TestUtils.getPlayerPos(start);
        Position initialMercPosition = getMercPos(start);

        DungeonResponse res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.rewind(5);

        // Check that player position does not change
        assertNotEquals(initialPosition, TestUtils.getPlayerPos(res));

        // Test that mercenary position reverts back
        assertEquals(initialMercPosition, getMercPos(res));
    }

    @Test
    @Tag("18-4")
    @DisplayName("Test time travel Portal")
    public void timeTravelPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravelTest_mercenaryPosition", "c_timeTravelTest");
        Position initialPosition = TestUtils.getPlayerPos(res);
        Position initialMercPosition = getMercPos(res);

        // Travel to time travel portal
        for (int i = 0; i < 4; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        for (int i = 0; i < 4; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // Check that player position does not change
        assertNotEquals(initialPosition, TestUtils.getPlayerPos(res));

        // Test that mercenary position reverts back
        assertEquals(initialMercPosition, getMercPos(res));
    }

    @Test
    @Tag("18-5")
    @DisplayName("Test time travelling doesn't revert player inventory")
    public void timeTravelInventoryIsSaved() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravelTest_inventoryIsSaved", "c_timeTravelTest");
        int startingEntities = TestUtils.getEntities(res).size();

        // Pick up items
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.rewind(5);

        // Sword remains in Inventory
        assertEquals(TestUtils.getInventory(res, "sword").size(), 1);
        // Sword respawns on Map at correct position
        assertEquals(TestUtils.getEntities(res).size(), startingEntities);
        assertEquals(TestUtils.getEntities(res, "sword").get(0).getPosition(), new Position(2, 1));
    }

    @Test
    @Tag("18-5")
    @DisplayName("Test time travelling changes a boulder back to original position")
    public void timeTravelBoulderIsChangedBack() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravelTest_boulderChangesBack", "c_timeTravelTest");
        Position startingPosition = getBoulderPos(res);

        // Pick up items
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.rewind(5);

        assertEquals(getBoulderPos(res), startingPosition);
    }

    private Position getBoulderPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "boulder").get(0).getPosition();
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
