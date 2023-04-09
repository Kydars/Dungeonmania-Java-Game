package dungeonmania;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.Goal;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game implements Serializable {
    private String id;
    private String name;
    private Goal goals;
    private GameMap map;
    private Player player;
    private BattleFacade battleFacade;
    private int initialTreasureCount;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    private int enemiesDestroyed = 0;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int PLAYER_MOVEMENT_CALLBACK = 1;
    public static final int AI_MOVEMENT = 2;
    public static final int AI_MOVEMENT_CALLBACK = 3;

    private int tickCount = 0;
    private SortedSet<ComparableCallback> sub = new TreeSet<>();
    private SortedSet<ComparableCallback> addingSub = new TreeSet<>();

    public Game(String dungeonName) {
        this.name = dungeonName;
        this.map = new GameMap();
        this.battleFacade = new BattleFacade();
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        map.init();
        this.tickCount = 0;
        player = map.getPlayer();
        register((Runnable & Serializable) () -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
        register((Runnable & Serializable) () -> map.getEntities(Mercenary.class).forEach(m -> m.onTick(tickCount)),
            PLAYER_MOVEMENT_CALLBACK, "mindControlCheck");
        initialTreasureCount = map.getTreasureSize();
    }

    public Game tick(Direction movementDirection) {
        registerOnce(
            (Runnable & Serializable) () -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT,
                "playerMoves");
        tick();
        return this;
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));

        registerOnce((Runnable & Serializable) () -> {
            if (item instanceof Bomb)
                player.use((Bomb) item, map);
            if (item instanceof Potion)
                player.use((Potion) item, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");
        tick();
        return this;
    }

    /**
     * Battle between player and enemy
     * @param player
     * @param enemy
     */
    public void battle(Player player, Enemy enemy) {
        battleFacade.battle(this, player, enemy);
        if (player.getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getHealth() <= 0) {
            map.destroyEntity(enemy);
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        List<String> buildables = getBuildables();
        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        registerOnce((Runnable & Serializable) () -> player.build(buildable, entityFactory), PLAYER_MOVEMENT,
            "playerBuildsItem");
        tick();
        return this;
    }

    public List<String> getBuildables() {
        return player.getBuildables(countEntities(ZombieToast.class) > 0);
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!((Interactable) e).isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce((Runnable & Serializable) () -> ((Interactable) e).interact(player, this), PLAYER_MOVEMENT,
            "playerBuildsItem");
        tick();
        return this;
    }

    public <T extends Entity> long countEntities(Class<T> type) {
        return map.countEntities(type);
    }

    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id));
        else
            sub.add(new ComparableCallback(r, priority, id));
    }

    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id, true));
        else
            sub.add(new ComparableCallback(r, priority, id, true));
    }

    public void unsubscribe(String id) {
        for (ComparableCallback c : sub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : addingSub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        isInTick = true;
        sub.forEach(s -> s.run());
        isInTick = false;
        sub.addAll(addingSub);
        addingSub = new TreeSet<>();
        sub.removeIf(s -> !s.isValid());
        tickCount++;
        // update the weapons/potions duration
        return tickCount;
    }

    public int getTick() {
        return this.tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }

    public void setBattleFacade(BattleFacade battleFacade) {
        this.battleFacade = battleFacade;
    }

    public int getInitialTreasureCount() {
        return initialTreasureCount;
    }

    public void setInitialTreasureCount(int initialTreasureCount) {
        this.initialTreasureCount = initialTreasureCount;
    }

    public int getEnemiesDestroyed() {
        return enemiesDestroyed;
    }

    public void setEnemiesDestroyed(int enemiesDestroyed) {
        this.enemiesDestroyed = enemiesDestroyed;
    }

    public void addEnemiesDestroyed() {
        enemiesDestroyed += 1;
    }

    public void setSub(SortedSet<ComparableCallback> sub) {
        this.sub = sub;
    }

    public void setAddingSub(SortedSet<ComparableCallback> addingSub) {
        this.addingSub = addingSub;
    }

    public boolean playerOnTimeTravellingPortal() {
        return map.playerOnTimeTravellingPortal();
    }

    public Inventory getPlayerInventory() {
        return player.getInventory();
    }

    public void useInventoryItem(InventoryItem item) {
        player.remove(item);
    }

    public void destroySpawner(ZombieToastSpawner spawner) {
        map.destroyEntity(spawner);
    }

    public void spawnSpider(Game game) {
        getEntityFactory().spawnSpider(game, game.getMap());
    }

    public int getRewindTick(int ticks) {
        int currentTick = getTick();
        if (ticks <= 0 || (ticks != 30 && currentTick - ticks < 0)) {
            throw new IllegalArgumentException();
        }
        if (currentTick - ticks < 0) {
            currentTick = 0;
        } else {
            currentTick -= ticks;
        }
        return currentTick;
    }

    public void update(GameMap oldMap, GameMap newMap, Inventory inventory) {
        try {
            oldMap.removeNode(player);
            player.setPosition((Position) DeepCopy.copy(newMap.getPlayerPosition()));
            player.setId(UUID.randomUUID().toString());
            oldMap.addEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Inventory newInventory = new Inventory();
        for (Entity item : inventory.getEntities()) {
            try {
                Entity newItem = (Entity) DeepCopy.copy(item);
                newItem.setId(UUID.randomUUID().toString());
                newInventory.add((InventoryItem) newItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        player.setInventory(newInventory);
    }
}
