package dungeonmania.entities.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;

public class Inventory implements Serializable {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        if (item instanceof Key && count(Key.class) > 0) {
            return false;
        }
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables(boolean zombiesExist) {

        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int swords = count(Sword.class);
        int sunstones = count(SunStone.class);
        List<String> result = new ArrayList<>();

        if (wood >= 1 && arrows >= 3) {
            result.add("bow");
        }
        if (wood >= 2 && (sunstones >= 1 || treasure >= 1 || keys >= 1)) {
            result.add("shield");
        }
        if ((wood >= 1 || arrows >= 2) && ((sunstones == 1 && (keys >= 1 || treasure >= 1)) || sunstones > 1)) {
            result.add("sceptre");
        }
        if (sunstones >= 1 && swords >= 1 && !zombiesExist) {
            result.add("midnight_armour");
        }
        return result;
    }

    public InventoryItem checkBuildCriteria(Player p, boolean remove, String buildable, EntityFactory factory) {

        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Sword> swords = getEntities(Sword.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);
        List<SunStone> sunStones = getEntities(SunStone.class);

        switch (buildable) {
            case "bow":
                items.remove(wood.get(0));
                items.remove(arrows.get(0));
                items.remove(arrows.get(1));
                items.remove(arrows.get(2));
                return factory.buildBow();
            case "shield":
                items.remove(wood.get(0));
                items.remove(wood.get(1));
                if (sunStones.isEmpty() && !treasure.isEmpty()) {
                    items.remove(treasure.get(0));
                } else if (sunStones.isEmpty()) {
                    items.remove(keys.get(0));
                }
                return factory.buildShield();
            case "sceptre":
                if (wood.isEmpty()) {
                    items.remove(arrows.get(0));
                    items.remove(arrows.get(1));
                } else {
                    items.remove(wood.get(0));
                }
                if (sunStones.size() < 2) {
                    if (keys.isEmpty()) {
                        items.remove(treasure.get(0));
                    } else {
                        items.remove(keys.get(0));
                    }
                }
                items.remove(sunStones.get(0));
                return factory.buildSceptre();
            case "midnight_armour":
                items.remove(swords.get(0));
                items.remove(sunStones.get(0));
                return factory.buildMidnightArmour();
            default:
                return null;
        }
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId)) return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public void useWeapon(Game game) {
        getWeapon().use(game);
    }

}
