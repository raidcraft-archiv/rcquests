package de.raidcraft.quests.api.script.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.QuestException;
import de.raidcraft.quests.api.QuestTemplate;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class StartQuestAction implements Action<Player> {

    @Override
    @SneakyThrows
    public void accept(Player player, ConfigurationSection config) {

        RaidCraft.LOGGER.info("Start Quest Action was triggered for " + player.getName() + " : " + config.getString("quest"));
        if (!player.hasPermission("rcquests.quest.start")) {
            throw new QuestException("Du hast nicht das Recht Quests zu starten!");
        }
        QuestManager component = RaidCraft.getComponent(QuestManager.class);
        QuestTemplate quest = component.getQuestTemplate(config.getString("quest"));
        if (quest == null) {
            throw new QuestException("Wrong config! Unknown quest given: " + config.getString("quest"));
        }
        component.getQuestHolder(player).startQuest(quest);
    }
}
