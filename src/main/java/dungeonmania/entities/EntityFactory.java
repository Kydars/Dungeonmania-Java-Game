package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.*;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.enemies.*;
import dungeonmania.entities.logical.AndRule;
import dungeonmania.entities.logical.CoAndRule;
import dungeonmania.entities.logical.LightBulb;
import dungeonmania.entities.logical.LogicalRule;
import dungeonmania.entities.logical.OrRule;
import dungeonmania.entities.logical.SwitchDoor;
import dungeonmania.entities.logical.Wire;
import dungeonmania.entities.logical.XOrRule;
import dungeonmania.map.GameMap;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.util.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class EntityFactory implements Serializable {
    private Map<String, Number> config = new HashMap<>();
    private Random ranGen = new Random();

    public EntityFactory(JSONObject config) {
        for (String name : config.keySet()) {
            this.config.put(name, (Number) config.getNumber(name));
        }
    }

    public Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity, config);
    }

    public void spawnSpider(Game game, GameMap map) {
        int tick = game.getTick();
        int rate = config.getOrDefault("spider_spawn_interval", 0).intValue();
        if (rate == 0 || (tick + 1) % rate != 0) return;
        int radius = 20;
        Position player = map.getPlayerPosition();

        Spider dummySpider = buildSpider(new Position(0, 0)); // for checking possible positions

        List<Position> availablePos = new ArrayList<>();
        for (int i = player.getX() - radius; i < player.getX() + radius; i++) {
            for (int j = player.getY() - radius; j < player.getY() + radius; j++) {
                if (Position.calculatePositionBetween(player, new Position(i, j)).magnitude() > radius) continue;
                Position np = new Position(i, j);
                if (!map.canMoveTo(dummySpider, np)) continue;
                availablePos.add(np);
            }
        }
        Position initPosition = availablePos.get(ranGen.nextInt(availablePos.size()));
        Spider spider = buildSpider(initPosition);
        map.addEntity(spider);
        game.register((Runnable & Serializable) () -> spider.move(game, map), Game.AI_MOVEMENT, spider.getId());
    }

    public void spawnZombie(Game game, GameMap map, ZombieToastSpawner spawner) {
        int tick = game.getTick();
        Random randGen = new Random();
        int spawnInterval = config.getOrDefault("zombie_spawn_interval",
            ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL).intValue();
        if (spawnInterval == 0 || (tick + 1) % spawnInterval != 0) return;
        List<Position> pos = spawner.getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> !map.getEntities(p).stream().anyMatch(e -> (e instanceof Wall)))
            .collect(Collectors.toList());
        if (pos.size() == 0) return;
        ZombieToast zt = buildZombieToast(pos.get(randGen.nextInt(pos.size())));
        map.addEntity(zt);
        game.register((Runnable & Serializable) () -> zt.move(game, map), Game.AI_MOVEMENT, zt.getId());
    }

    public Spider buildSpider(Position pos) {
        double spiderHealth = config.getOrDefault("spider_health", Spider.DEFAULT_HEALTH).doubleValue();
        double spiderAttack = config.getOrDefault("spider_attack", Spider.DEFAULT_ATTACK).doubleValue();
        return new Spider(pos, spiderHealth, spiderAttack);
    }

    public Player buildPlayer(Position pos) {
        double playerHealth = config.getOrDefault("player_health", Player.DEFAULT_HEALTH).doubleValue();
        double playerAttack = config.getOrDefault("player_attack", Player.DEFAULT_ATTACK).doubleValue();
        return new Player(pos, playerHealth, playerAttack);
    }

    public ZombieToast buildZombieToast(Position pos) {
        double zombieHealth = config.getOrDefault("zombie_health", ZombieToast.DEFAULT_HEALTH).doubleValue();
        double zombieAttack = config.getOrDefault("zombie_attack", ZombieToast.DEFAULT_ATTACK).doubleValue();
        return new ZombieToast(pos, zombieHealth, zombieAttack);
    }

    public ZombieToastSpawner buildZombieToastSpawner(Position pos) {
        int zombieSpawnRate = config.getOrDefault("zombie_spawn_interval",
            ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL).intValue();
        return new ZombieToastSpawner(pos, zombieSpawnRate);
    }

    public Mercenary buildMercenary(Position pos) {
        double mercenaryHealth = config.getOrDefault("mercenary_health", Mercenary.DEFAULT_HEALTH).doubleValue();
        double mercenaryAttack = config.getOrDefault("mercenary_attack", Mercenary.DEFAULT_ATTACK).doubleValue();
        int mercenaryBribeAmount = config.getOrDefault("bribe_amount", Mercenary.DEFAULT_BRIBE_AMOUNT).intValue();
        int mercenaryBribeRadius = config.getOrDefault("bribe_radius", Mercenary.DEFAULT_BRIBE_RADIUS).intValue();
        int mindControlDuration = config.getOrDefault("mind_control_duration",
            Mercenary.DEFAULT_MIND_CONTROL_DURATION).intValue();
        return new Mercenary(pos, mercenaryHealth, mercenaryAttack, mercenaryBribeAmount, mercenaryBribeRadius,
                mindControlDuration);
    }

    public Assassin buildAssassin(Position pos) {
        double assassinHealth = config.getOrDefault("assassin_health", Assassin.DEFAULT_HEALTH).doubleValue();
        double assassinAttack = config.getOrDefault("assassin_attack", Assassin.DEFAULT_ATTACK).doubleValue();
        int assassinBribeAmount = config.getOrDefault("assassin_bribe_amount",
            Assassin.DEFAULT_BRIBE_AMOUNT).intValue();
        int assassinBribeRadius = config.getOrDefault("bribe_radius", Mercenary.DEFAULT_BRIBE_RADIUS).intValue();
        double assassinFailRate = config.getOrDefault("assassin_bribe_fail_rate",
            Assassin.DEFAULT_BRIBE_FAIL_RATE).doubleValue();
        int mindControlDuration = config.getOrDefault("mind_control_duration",
            Mercenary.DEFAULT_MIND_CONTROL_DURATION).intValue();
        return new Assassin(pos, assassinHealth, assassinAttack, assassinBribeAmount, assassinBribeRadius,
            assassinFailRate, mindControlDuration);
    }

    public Bow buildBow() {
        int bowDurability = config.get("bow_durability").intValue();
        return new Bow(bowDurability);
    }

    public Shield buildShield() {
        int shieldDurability = config.get("shield_durability").intValue();
        double shieldDefence = config.get("shield_defence").intValue();
        return new Shield(shieldDurability, shieldDefence);
    }

    public Sceptre buildSceptre() {
        return new Sceptre();
    }

    public MidnightArmour buildMidnightArmour() {
        double midnightAttack = config.getOrDefault("midnight_armour_attack",
            MidnightArmour.DEFAULT_MIDNIGHT_ATTACK).doubleValue();
        double midnightDefence = config.getOrDefault("midnight_armour_defence",
            MidnightArmour.DEFAULT_MIDNIGHT_DEFENCE).doubleValue();
        return new MidnightArmour(midnightAttack, midnightDefence);
    }

    public LogicalRule buildLogicalRule(String type) {
        switch (type) {
            case "and":
                return new AndRule();
            case "or":
                return new OrRule();
            case "xor":
                return new XOrRule();
            case "co_and":
                return new CoAndRule();
            default:
                return null;
        }
    }

    private Entity constructEntity(JSONObject jsonEntity, Map<String, Number> config) {
        Position pos = new Position(jsonEntity.getInt("x"), jsonEntity.getInt("y"));

        switch (jsonEntity.getString("type")) {
        case "player":
            return buildPlayer(pos);
        case "zombie_toast":
            return buildZombieToast(pos);
        case "zombie_toast_spawner":
            return buildZombieToastSpawner(pos);
        case "mercenary":
            return buildMercenary(pos);
        case "assassin":
            return buildAssassin(pos);
        case "wall":
            return new Wall(pos);
        case "boulder":
            return new Boulder(pos);
        case "switch":
            return new Switch(pos);
        case "exit":
            return new Exit(pos);
        case "treasure":
            return new Treasure(pos);
        case "sun_stone":
            return new SunStone(pos);
        case "wood":
            return new Wood(pos);
        case "arrow":
            return new Arrow(pos);
        case "bomb":
            int bombRadius = config.getOrDefault("bomb_radius", Bomb.DEFAULT_RADIUS).intValue();
            return new Bomb(pos, bombRadius);
        case "invisibility_potion":
            int invisibilityPotionDuration = config.getOrDefault(
                "invisibility_potion_duration",
                InvisibilityPotion.DEFAULT_DURATION).intValue();
            return new InvisibilityPotion(pos, invisibilityPotionDuration);
        case "invincibility_potion":
            int invincibilityPotionDuration = config.getOrDefault("invincibility_potion_duration",
            InvincibilityPotion.DEFAULT_DURATION).intValue();
            return new InvincibilityPotion(pos, invincibilityPotionDuration);
        case "portal":
            return new Portal(pos, ColorCodedType.valueOf(jsonEntity.getString("colour")));
        case "sword":
            double swordAttack = config.getOrDefault("sword_attack", Sword.DEFAULT_ATTACK).doubleValue();
            int swordDurability = config.getOrDefault("sword_durability", Sword.DEFAULT_DURABILITY).intValue();
            return new Sword(pos, swordAttack, swordDurability);
        case "spider":
            return buildSpider(pos);
        case "door":
            return new Door(pos, jsonEntity.getInt("key"));
        case "key":
            return new Key(pos, jsonEntity.getInt("key"));
        case "swamp_tile":
            return new SwampTile(pos, jsonEntity.getInt("movement_factor"));
        case "time_turner":
            return new TimeTurner(pos);
        case "time_travelling_portal":
            return new TimeTravellingPortal(pos);
        case "wire":
            return new Wire(pos);
        case "light_bulb_off":
            return new LightBulb(pos, buildLogicalRule(jsonEntity.getString("logic")));
        case "switch_door":
            return new SwitchDoor(pos, buildLogicalRule(jsonEntity.getString("logic")));
        default:
            return null;
        }
    }
}
