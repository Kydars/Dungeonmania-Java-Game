package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class LogicalEntitiesTest {

    @Test
    @Tag("23-1")
    @DisplayName("Test turn light bulb on with switch - OR condition")
    public void lightBulbOR() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_lightBulbOR", "c_logicalEntitiesTest");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Deactivate switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("23-2")
    @DisplayName("Test turn light bulb on with switch and wire - OR condition")
    public void lightBulbWireOR() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_lightBulbWireOR", "c_logicalEntitiesTest");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("23-3")
    @DisplayName("Test turn light bulb on with switch and wire - AND condition")
    public void lightBulbWireAND() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_lightBulbWireAND", "c_logicalEntitiesTest");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate first switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate second switch
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Deactivate first switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("23-4")
    @DisplayName("Test turn light bulb on with switch and wire - XOR condition")
    public void lightBulbWireXOR() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_lightBulbWireXOR", "c_logicalEntitiesTest");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate first switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate second switch
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Deactivate first switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("23-5")
    @DisplayName("Test turn light bulb on with switch and wire - CO_AND condition")
    public void lightBulbWireCOAND() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_lightBulbWireCOAND", "c_logicalEntitiesTest");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate first switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate second switch
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Deactivate first switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("23-6")
    @DisplayName("Test open switch door with switch and wire - OR condition")
    public void switchDoorWireOR() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_switchDoorWireOR", "c_logicalEntitiesTest");

        // Activate first switch
        res = dmc.tick(Direction.RIGHT);

        // Walk through switch door
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 2), TestUtils.getPlayerPos(res));

        // Walk through wire
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 3), TestUtils.getPlayerPos(res));
    }

    @Test
    @Tag("23-6")
    @DisplayName("Test light bulb with multiple activation sources")
    public void multipleSources() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicalEntitiesTest_multipleSources", "c_logicalEntitiesTest");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Activate first switch
        res = dmc.tick(Direction.RIGHT);

        // Activate second switch
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Deactivate first switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }
}
