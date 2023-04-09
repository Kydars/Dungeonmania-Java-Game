package dungeonmania.map;

import java.io.Serializable;

import org.json.JSONObject;

import dungeonmania.entities.EntityFactory;

public class GraphNodeFactory implements Serializable {
    public static GraphNode createEntity(JSONObject jsonEntity, EntityFactory factory) {
        return constructEntity(jsonEntity, factory);
    }

    private static GraphNode constructEntity(JSONObject jsonEntity, EntityFactory factory) {
        switch (jsonEntity.getString("type")) {
        case "player":
        case "zombie_toast":
        case "zombie_toast_spawner":
        case "mercenary":
        case "assassin":
        case "wall":
        case "boulder":
        case "switch":
        case "exit":
        case "treasure":
        case "wood":
        case "arrow":
        case "bomb":
        case "sun_stone":
        case "invisibility_potion":
        case "invincibility_potion":
        case "portal":
        case "sword":
        case "spider":
        case "door":
        case "key":
        case "time_turner":
        case "time_travelling_portal":
        case "wire":
        case "light_bulb_off":
        case "switch_door":
            return new GraphNode(factory.createEntity(jsonEntity));
        case "swamp_tile":
            return new GraphNode(factory.createEntity(jsonEntity), jsonEntity.getInt("movement_factor"));
        default:
            return null;
        }
    }
}
