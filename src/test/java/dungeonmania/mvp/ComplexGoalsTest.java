package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ComplexGoalsTest {

    @Test
    @Tag("14-1")
    @DisplayName("Testing a map with 4 conjunction goal")
    public void andAll() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_andAll", "c_complexGoalsTest_andAll");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // kill spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        // pickup treasure
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        // move to exit
        res = dmc.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-2")
    @DisplayName("Testing a map with 4 disjunction goal")
    public void orAll() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_orAll", "c_complexGoalsTest_orAll");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move onto exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-3")
    @DisplayName("Testing that the exit goal must be achieved last in EXIT and TREASURE")
    public void exitAndTreasureOrder() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_complexGoalsTest_exitAndTreasureOrder", "c_complexGoalsTest_exitAndTreasureOrder");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // move player onto exit
        res = dmc.tick(Direction.RIGHT);

        // don't check state of exit goal in string
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // move player to pick up treasure
        res = dmc.tick(Direction.RIGHT);

        // assert treasure goal met, but goal string is not empty
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertNotEquals("", TestUtils.getGoals(res));

        // move player back onto exit
        res = dmc.tick(Direction.LEFT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-4")
    @DisplayName("Testing that the exit goal must be achieved last and EXIT and TREASURE")
    public void exitAndBouldersAndTreasureOrder() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_complexGoalsTest_exitAndBouldersAndTreasureOrder", "c_complexGoalsTest_exitAndBouldersAndTreasureOrder");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move player onto treasure
        res = dmc.tick(Direction.RIGHT);

        // assert treasure goal met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));

        // move player onto exit
        res = dmc.tick(Direction.RIGHT);

        // assert treasure goal remains achieved
        // don't check state of exit goal in string
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));

        // move boulder onto switch, but goal string is not empty
        res = dmc.tick(Direction.RIGHT);
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertNotEquals("", TestUtils.getGoals(res));

        // move back onto exit
        res = dmc.tick(Direction.LEFT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-6")
    @DisplayName("Testing a switch goal can be achieved and then become unachieved")
    public void switchUnachieved() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_switchUnachieved", "c_complexGoalsTest_switchUnachieved");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // assert boulder goal met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder off switch
        res = dmc.tick(Direction.RIGHT);

        // assert boulder goal unmet
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));
    }

    @Test
    @Tag("14-7")
    @DisplayName("Test achieving a complex enemies goal")
    public void spawner() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_activeSpawner", "c_complexGoalsTest_activeSpawner");

        // move player to spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // check that the player survived and 1 spider died
        List<EntityResponse> entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "spider"));

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // check that the spawner exists and has spawned a zombie_toast
        entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "zombie_toast_spawner"));

        // move player to right
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // check that the player survived and the zombie_toast is destroyed
        entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "zombie_toast"));
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));

        // move player to wait two spawn tick (interval is set to 3)
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // check that the spawner no longer spawns zombie_toast
        entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "zombie_toast"));

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-8")
    @DisplayName("Test achieving a complex enemies AND treasure goal")
    public void enemiesAndTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_complexGoalsTest_activeSpawnerAndTreasure", "c_complexGoalsTest_activeSpawnerAndTreasure");

        // move player to spider
        res = dmc.tick(Direction.RIGHT);

        // assert goals not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // check that the player survived and 1 spider died
        List<EntityResponse> entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "spider"));

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // check that the spawner exists and has spawned a zombie_toast
        entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "zombie_toast_spawner"));

        // move player to right
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // check that the player survived and the zombie_toast is destroyed
        entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "zombie_toast"));
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));

        // move player to wait two spawn tick (interval is set to 3)
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // check that the spawner no longer spawns zombie_toast
        entities = res.getEntities();
        assertEquals(1, TestUtils.countEntityOfType(entities, "player"));
        assertEquals(0, TestUtils.countEntityOfType(entities, "zombie_toast"));

        // assert goal not met for treasure
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // pick up treasure
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }
}
