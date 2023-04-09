package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SwampTileTest {

    @Test
    @Tag("15-1")
    @DisplayName("Test Swamp Tile effect on player")
    public void playerInSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_playerInSwamp", "c_swampTileTest_playerInSwamp");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // move player to exit and check that swamp tile does not delay it.
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("15-2")
    @DisplayName("Test Swamp Tile effect on boulder")
    public void boulderInSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_boulderInSwamp", "c_swampTileTest_boulderInSwamp");

        // push boulder onto swamp tile
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // push boulder through swamp tile onto switch
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("15-3")
    @DisplayName("Test Swamp Tile effect on spider")
    public void spiderInSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_spiderInSwamp", "c_swampTileTest_spiderInSwamp");
        Position pos = TestUtils.getEntities(res, "spider").get(0).getPosition();

        List<Position> movementTrajectory = new ArrayList<>();
        int x = pos.getX();
        int y = pos.getY();
        int nextPositionElement = 0;
        // Tick 1 - Move onto swamp tile
        movementTrajectory.add(new Position(x, y - 1));
        // Tick 2 - Stuck on swamp tile
        movementTrajectory.add(new Position(x, y - 1));
        // Tick 3 - Still stuck on swamp tile
        movementTrajectory.add(new Position(x, y - 1));
        // Tick 4 - Move off the swamp tile
        movementTrajectory.add(new Position(x + 1, y - 1));
        // Normal movement
        movementTrajectory.add(new Position(x + 1, y));

        // Assert Circular Movement of Spider
        for (int i = 0; i <= 4; ++i) {
            res = dmc.tick(Direction.NONE);
            assertEquals(movementTrajectory.get(nextPositionElement),
                TestUtils.getEntities(res, "spider").get(0).getPosition());
            nextPositionElement++;
        }
    }

    @Test
    @Tag("15-4")
    @DisplayName("Test Swamp Tile effect on zombie")
    public void zombieInSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombieInSwamp", "c_swampTileTest_zombieInSwamp");

        assertEquals(1, getZombies(res).size());

        // Move zombie onto a swamp tile
        boolean zombieMoved = false;
        Position prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.NONE);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(zombieMoved);

        // Check that given zombie is stuck and cannot not move for a total of 9 ticks (movement_factor of 5)
        zombieMoved = false;
        prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.NONE);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertFalse(zombieMoved);
        // Check that zombie can eventually escape
        for (int i = 0; i < 100; i++) {
            res = dmc.tick(Direction.NONE);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(zombieMoved);
    }

    @Test
    @Tag("15-5")
    @DisplayName("Test Swamp Tile effect on mercenary")
    public void mercenaryInSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_mercenaryInSwamp", "c_swampTileTest_mercenaryInSwamp");

        // Tick 0 - Spawn below swamp
        assertEquals(new Position(0, 2), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        // Tick 1 - Move onto swamp tile
        assertEquals(new Position(0, 1), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        // Tick 2 - Stuck on swamp tile
        assertEquals(new Position(0, 1), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        // Tick 3 - Still stuck on swamp tile
        assertEquals(new Position(0, 1), getMercPos(res));
        res = dmc.tick(Direction.NONE);

        // assert goal met, mercenary moves off swamp and is destroyed by player
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("15-6")
    @DisplayName("Test Swamp Tile effect on ally")
    public void allyInSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_allyInSwamp", "c_swampTileTest_allyInSwamp");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // ally is trapped swamp (not adjacent)
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(1, 3), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(1, 3), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(1, 3), getMercPos(res));

        // move player adjacent to ally
        res = dmc.tick(Direction.DOWN);

        // show that ally is now freed
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 2), getMercPos(res));

        // move the player down through the swamp
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 1), getMercPos(res));
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 2), getMercPos(res));
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 3), getMercPos(res));

        // ally can no longer get stuck in the swamp
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 4), getMercPos(res));
    }

    @Test
    @Tag("15-7")
    @DisplayName("Test ally pathing around swamp tile")
    public void aroundSwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_aroundSwamp", "c_swampTileTest_allyInSwamp");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(new Position(2, 4), getMercPos(res));

        // ally is trapped swamp (not adjacent)
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(2, 3), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(2, 2), getMercPos(res));
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(1, 2), getMercPos(res));
    }

    private List<EntityResponse> getZombies(DungeonResponse res) {
        return TestUtils.getEntities(res, "zombie_toast");
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
