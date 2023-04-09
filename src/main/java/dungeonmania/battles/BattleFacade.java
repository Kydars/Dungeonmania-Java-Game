package dungeonmania.battles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade implements Serializable {
    private List<BattleResponse> battleResponses = new ArrayList<>();

    public void battle(Game game, Player player, Enemy enemy) {
        // 0. init
        double initialPlayerHealth = player.getHealth();
        double initialEnemyHealth = enemy.getHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);

        player.buff();

        if (!player.isEnabled() || !enemy.isEnabled()) {
            return;
        }
        List<BattleRound> rounds = BattleStatistics.battle(player.getBattleStatistics(), enemy.getBattleStatistics());

        player.useItems(game);

        // 5. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(enemyString, rounds.stream().map(ResponseBuilder::getRoundResponse).
        collect(Collectors.toList()), player.getBattleItems().stream().map(Entity.class::cast).
        map(ResponseBuilder::getItemResponse).collect(Collectors.toList()), initialPlayerHealth, initialEnemyHealth));
    }

    public List<BattleResponse> getBattleResponses() {
        return battleResponses;
    }
}
