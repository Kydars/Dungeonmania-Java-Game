package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class PersistenceTest {
    @Test
    @Tag("17-1")
    @DisplayName("Test that the movement of player is saved and loaded")
    public void testMovementIsSaved() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // move player downward

        DungeonResponse res1 = dmc.tick(Direction.DOWN);

        res1 = dmc.saveGame("save");

        DungeonManiaController dmcNew = new DungeonManiaController();

        DungeonResponse res2 = dmcNew.loadGame("save");

        TestUtils.dungeonResponseEqual(res1, res2);

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(),
        new Position(1, 2), false);

        EntityResponse actualPlayer = TestUtils.getPlayer(res1).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @Tag("17-2")
    @DisplayName("Test that the movement of player is reverted to the given save state")
    public void testMovementIsReverted() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungeonRes = dmc.newGame(
            "d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        assertEquals(TestUtils.getPlayerPos(initDungeonRes), new Position(1, 1));

        // save the current position, move the player and then overwrite the new position with the saved one
        DungeonResponse save = dmc.saveGame("save");

        // move the player downwards
        dmc.tick(Direction.DOWN);

        DungeonManiaController dmcNew = new DungeonManiaController();

        DungeonResponse load = dmcNew.loadGame("save");

        TestUtils.dungeonResponseEqual(save, load);

        // assert after movement
        assertEquals(TestUtils.getPlayerPos(load), new Position(1, 1));
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test that the interactions of player is saved and loaded")
    public void testInteractIsSaved() {
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

        // save and load the current game in a new controller
        DungeonResponse save = dmc.saveGame("save");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

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
    @Tag("17-4")
    @DisplayName("Test that movement of Mercenary is saved and loaded")
    public void testMercenaryMovementIsSaved() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_simpleMovement", "c_mercenaryTest_simpleMovement");

        assertEquals(new Position(8, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);

        // save the current game and load it into a new controller
        DungeonResponse save = dmc.saveGame("save");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        assertEquals(new Position(7, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
    }

    @Test
    @Tag("17-5")
    @DisplayName("Test basic movement of spiders")
    public void testSpiderMovementIsSaved() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_spiderTest_basicMovement", "c_spiderTest_basicMovement");
        Position pos = TestUtils.getEntities(res, "spider").get(0).getPosition();

        List<Position> movementTrajectory = new ArrayList<>();
        int x = pos.getX();
        int y = pos.getY();
        int nextPositionElement = 0;
        movementTrajectory.add(new Position(x, y - 1));
        movementTrajectory.add(new Position(x + 1, y - 1));
        movementTrajectory.add(new Position(x + 1, y));
        movementTrajectory.add(new Position(x + 1, y + 1));
        movementTrajectory.add(new Position(x, y + 1));
        movementTrajectory.add(new Position(x - 1, y + 1));
        movementTrajectory.add(new Position(x - 1, y));
        movementTrajectory.add(new Position(x - 1, y - 1));

        // save prior to spider movement
        DungeonResponse save = dmc.saveGame("save");

        // assert circular movement of spider
        for (int i = 0; i <= 20; ++i) {
            res = dmc.tick(Direction.UP);
            assertEquals(movementTrajectory.get(nextPositionElement),
            TestUtils.getEntities(res, "spider").get(0).getPosition());
            nextPositionElement++;
            if (nextPositionElement == 8) {
                nextPositionElement = 0;
            }
        }

        // load the game in a new controller
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        // assert that spider has returned to initial position
        assertEquals(new Position(x, y), TestUtils.getEntities(load, "spider").get(0).getPosition());
    }

    @Test
    @Tag("17-6")
    @DisplayName("Testing zombies movement")
    public void testZombieMovementIsSaved() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieTest_movement", "c_zombieTest_movement");
        assertEquals(1, getZombies(res).size());

        // save prior to zombie movement
        DungeonResponse save = dmc.saveGame("save");

        // Teams may assume that random movement includes choosing to stay still, so we should just
        // check that they do move at least once in a few turns
        boolean zombieMoved = false;
        Position prevPosition = getZombies(res).get(0).getPosition();
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.UP);
            if (!prevPosition.equals(getZombies(res).get(0).getPosition())) {
                zombieMoved = true;
                break;
            }
        }
        assertTrue(zombieMoved);

        // load after zombie movement
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        // assert that the zombie has returned to its initial position
        assertTrue(prevPosition.equals(getZombies(load).get(0).getPosition()));
    }

    @Test
    @Tag("17-7")
    @DisplayName("Test allied Mercenary remains allied and still follows player")
    public void alliedMovementIsSaved() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMovementAdjacent", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 1), getMercPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        assertEquals(new Position(1, 1), getMercPos(res));
        res = dmc.tick(Direction.NONE);

        // save the current game and load it into a new controller
        DungeonResponse save = dmc.saveGame("save");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        assertEquals(new Position(1, 1), getMercPos(load));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getMercPos(res));
    }

    @Test
    @Tag("17-8")
    @DisplayName("Test pushing a boulder is saved")
    public void pushedBoulderPositionIsSaved() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_boulderTest_pushBoulder", "c_boulderTest_pushBoulder");
        assertTrue(boulderAt(res, 1, 0));

        // Player moves boulder
        res = dmc.tick(Direction.RIGHT);

        // save the current game and load it into a new controller
        DungeonResponse save = dmc.saveGame("save");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        assertTrue(boulderAt(load, 2, 0));
        assertEquals(new Position(1, 0), TestUtils.getPlayer(load).get().getPosition());
    }

    @Test
    @Tag("17-9")
    @DisplayName("Test picking up a bomb removes the bomb from the map and adds the bomb to the inventory is saved")
    public void pickUpBombIsSaved() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_pickUp", "c_bombTest_pickUp");
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);

        // save the current game and load it into a new controller
        DungeonResponse save = dmc.saveGame("save");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        assertEquals(0, TestUtils.getEntities(load, "bomb").size());
        assertEquals(1, TestUtils.getInventory(load, "bomb").size());
    }

    @Test
    @Tag("17-10")
    @DisplayName(
        "Test that placing a bomb on the map at the character's location is saved")
    public void placingBombIsSaved() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_place", "c_bombTest_place");

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // save the current game and load it into a new controller
        DungeonResponse save = dmc.saveGame("save1");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save1");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        // Remove bomb from inventory
        res = dmc.tick(TestUtils.getInventory(load, "bomb").get(0).getId());

        // save the current game and load it into a new controller
        save = dmc.saveGame("save2");
        dmcNew = new DungeonManiaController();
        load = dmcNew.loadGame("save2");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        // Bomb not in inventory
        assertEquals(0, TestUtils.getInventory(load, "bomb").size());

        // Bomb in the position the character was previously
        assertEquals(1, TestUtils.getEntities(load, "bomb").size());
        assertEquals(pos, TestUtils.getEntities(load, "bomb").get(0).getPosition());

        //Bomb can not be re-picked up
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);

        // save the current game and load it into a new controller
        save = dmc.saveGame("save3");
        dmcNew = new DungeonManiaController();
        load = dmcNew.loadGame("save3");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        assertEquals(0, TestUtils.getInventory(load, "bomb").size());
        assertEquals(1, TestUtils.getEntities(load, "bomb").size());
    }

    @Test
    @Tag("17-11")
    @DisplayName(
        "Test that an bomb's affect on surrounding non-player entities is saved"
    )
    public void placeActiveBombIsSaved() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeCardinallyActive", "c_bombTest_placeCardinallyActive");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // save the current game and load it into a new controller
        DungeonResponse save = dmc.saveGame("save");
        DungeonManiaController dmcNew = new DungeonManiaController();
        DungeonResponse load = dmcNew.loadGame("save");

        // Check that the responses are the same
        TestUtils.dungeonResponseEqual(save, load);

        // Check Bomb exploded
        assertEquals(0, TestUtils.getEntities(load, "bomb").size());
        assertEquals(0, TestUtils.getEntities(load, "boulder").size());
        assertEquals(0, TestUtils.getEntities(load, "switch").size());
        assertEquals(0, TestUtils.getEntities(load, "wall").size());
        assertEquals(0, TestUtils.getEntities(load, "treasure").size());
        assertEquals(1, TestUtils.getEntities(load, "player").size());
    }

    @Test
    @Tag("17-12")
    @DisplayName("Test that the effects of a potion are maintained between save and load")
    public void invincibilityDurationIsSaved() throws InvalidActionException {
        //   S1_2   S1_3       P_1
        //   S1_1   S1_4/P_4   P_2/POT/P_3
        //          P_5        S2_2         S2_3
        //          P_6        S2_1         S2_4
        //          P_7/S2_7   S2_6         S2_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invincibilityDuration", "c_potionsTest_invincibilityDuration");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(2, TestUtils.getEntities(res, "spider").size());

        // pick up invincibility_potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // consume invincibility_potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));

        // check that invincibility potion persists

        DungeonResponse save = dmc.saveGame("save");

        DungeonManiaController dmcNew = new DungeonManiaController();

        DungeonResponse load = dmcNew.loadGame("save");

        TestUtils.dungeonResponseEqual(save, load);

        // meet first spider, battle won immediately using invincibility_potion
        // we need to check that the effects exist before they are worn off,
        // otherwise teams which don't implement potions will pass
        res = dmcNew.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertEquals(1, res.getBattles().get(0).getRounds().size());

        // check that invincibility potion wears off correctly
        dmcNew.saveGame("save");
        dmcNew.loadGame("save");

        // meet second spider and battle without effects of potion
        res = dmcNew.tick(Direction.DOWN);
        res = dmcNew.tick(Direction.DOWN);
        res = dmcNew.tick(Direction.DOWN);

        assertEquals(0, TestUtils.getEntities(res, "spider").size());
        assertEquals(2, res.getBattles().size());
        assertTrue(res.getBattles().get(1).getRounds().size() >= 1);
        assertEquals(0, res.getBattles().get(1).getBattleItems().size());
    }

    // Test when the effects of a 2nd potion are 'queued'
    // and will take place the tick following the previous potion wearing off
    @Test
    @Tag("17-13")
    @DisplayName(
        "Test that the effect of second potion is queued between save and load"
    )
    public void potionQueuingIsSaved() throws InvalidActionException {
        //  Wall   P_1/2/3    P_4   P_5/6/7/S_9/P_9     S_2     S_3
        //                          S_8/P_8             S_1     S_4
        //                          S_7                 S_6     S_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_potionQueuing", "c_potionsTest_potionQueuing");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getEntities(res, "spider").size());

        // buffer
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // pick up invisibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // consume invisibility potion (invisibility has duration 3)
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());

        // check that invincibility potion duration is saved
        DungeonResponse save = dmc.saveGame("save");

        DungeonManiaController dmcNew = new DungeonManiaController();

        DungeonResponse load = dmcNew.loadGame("save");

        TestUtils.dungeonResponseEqual(save, load);

        // consume invincibility potion (invisibility has duration 2)
        res = dmcNew.tick(TestUtils.getFirstItemId(load, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        // check that potion queue is saved
        dmcNew.saveGame("save");
        dmcNew.loadGame("save");

        // meet spider, but not battle occurs (invisibility has duration 1)
        res = dmcNew.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(0, res.getBattles().size());
    }

    private List<EntityResponse> getZombies(DungeonResponse res) {
        return TestUtils.getEntities(res, "zombie_toast");
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private boolean boulderAt(DungeonResponse res, int x, int y) {
        Position pos = new Position(x, y);
        return TestUtils.getEntitiesStream(res, "boulder").anyMatch(
            it -> it.getPosition().equals(pos)
        );
    }
}
