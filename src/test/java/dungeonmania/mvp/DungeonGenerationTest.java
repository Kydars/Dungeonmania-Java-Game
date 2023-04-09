package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DungeonGenerationTest {
    @Test
    @Tag("16-1")
    @DisplayName("Test that player can move around in randomized dunegon")
    public void move() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 3, 3, "c_basicGoalsTest_exit");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // Can move player without issue
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
    }

    @Test
    @Tag("16-2")
    @DisplayName("Test player starts in requested position")
    public void checkStartingPosition() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(2, 5, 9, 15, "c_basicGoalsTest_exit");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        assertTrue(TestUtils.getPlayerPos(res).equals(new Position(2, 5)));
    }
}
