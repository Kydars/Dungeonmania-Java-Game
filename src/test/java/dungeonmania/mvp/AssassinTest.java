package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssassinTest {

    @Test
    @Tag("16-1")
    @DisplayName("Test assassin in line with Player moves towards them")
    public void simpleMovement() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      A4      A3      A2      A1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_simpleMovement", "c_assassinTest_simpleMovement");

        assertEquals(new Position(8, 1), getAssPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getAssPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getAssPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getAssPos(res));
    }

    @Test
    @Tag("16-2")
    @DisplayName("Test assassin stops if they cannot move any closer to the player")
    public void stopMovement() {
        //                  Wall     Wall    Wall
        // P1       P2      Wall      A1     Wall
        //                  Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_stopMovement", "c_assassinTest_simpleMovement");

        Position startingPos = getAssPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getAssPos(res));
    }

    @Test
    @Tag("16-3")
    @DisplayName("Test assassins can not move through closed doors")
    public void doorMovement() {
        //                  Wall   Door   Wall    Wall
        // P1       P2      Wall           A1     Wall
        // Key              Wall   Wall   Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_doorMovement", "c_assassinTest_simpleMovement");

        Position startingPos = getAssPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getAssPos(res));
    }

    @Test
    @Tag("16-4")
    @DisplayName("Test assassin moves around a wall to get to the player")
    public void evadeWall() {
        //                  Wall      M2
        // P1       P2      Wall      A1
        //                  Wall      M2
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_evadeWall", "c_assassinTest_simpleMovement");

        res = dmc.tick(Direction.RIGHT);
        assertTrue(new Position(8, 5).equals(getAssPos(res))
            || new Position(8, 4).equals(getAssPos(res)));
    }

    @Test
    @Tag("16-5")
    @DisplayName("Testing an assassin can be bribed with a certain amount")
    public void bribeAmount() {
        //                                                          Wall     Wall     Wall    Wall    Wall
        // P1       P2/Treasure      P3/Treasure    P4/Treasure      A4       A3       A2     A1      Wall
        //                                                          Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_bribeAmount", "c_assassinTest_bribeAmount");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getAssPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getAssPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up third treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getAssPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("16-6")
    @DisplayName("Testing an assassin can be bribed within a radius")
    public void bribeRadius() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1       P2/Treasure      P3    P4      A4       A3       A2     A1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_bribeRadius", "c_assassinTest_bribeRadius");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getAssPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
            dmc.interact(assId)
        );
    }

    @Test
    @Tag("16-7")
    @DisplayName("Testing an allied assassin does not battle the player")
    public void allyBattle() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      A2      A1      Wall
        //                                  Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_allyBattle", "c_assassinTest_allyBattle");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("16-8")
    @DisplayName("Testing guaranteed bribe failure")
    public void bribeFailure() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      A2      A1      Wall
        //                                  Wall    Wall    Wall
        for (int i = 0; i < 10; i++) {
            DungeonManiaController dmc = new DungeonManiaController();
            DungeonResponse res = dmc.newGame("d_assassinTest_allyBattle", "c_assassinTest_bribeFailure");

            String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

            // pick up treasure
            res = dmc.tick(Direction.RIGHT);
            assertEquals(1, TestUtils.getInventory(res, "treasure").size());

            // achieve bribe
            res = assertDoesNotThrow(() -> dmc.interact(assId));
            assertEquals(0, TestUtils.getInventory(res, "treasure").size());

            // walk into assassin, a battle occurs
            res = dmc.tick(Direction.RIGHT);
            assertEquals(1, res.getBattles().size());
        }
    }

    @Test
    @Tag("16-8")
    @DisplayName("Testing guaranteed bribe success")
    public void bribeSuccess() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      A2      A1      Wall
        //                                  Wall    Wall    Wall
        for (int i = 0; i < 10; i++) {
            DungeonManiaController dmc = new DungeonManiaController();
            DungeonResponse res = dmc.newGame("d_assassinTest_allyBattle", "c_assassinTest_bribeSuccess");

            String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

            // pick up treasure
            res = dmc.tick(Direction.RIGHT);
            assertEquals(1, TestUtils.getInventory(res, "treasure").size());

            // achieve bribe
            res = assertDoesNotThrow(() -> dmc.interact(assId));
            assertEquals(0, TestUtils.getInventory(res, "treasure").size());

            // walk into assassin, a battle occurs
            res = dmc.tick(Direction.RIGHT);
            assertEquals(0, res.getBattles().size());
        }
    }

    private Position getAssPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "assassin").get(0).getPosition();
    }

}
