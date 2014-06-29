package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.quests.QuestManager;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class StartQuestAction implements Action<Player> {

    @Override
    @SneakyThrows
    public void accept(Player player) {

        if (!player.hasPermission("rcquests.quest.start")) {
            throw new QuestException("Du hast nicht das Recht Quests zu starten!");
        }
        QuestManager component = RaidCraft.getComponent(QuestManager.class);
        QuestTemplate quest = component.getQuestTemplate(getConfig().getString("quest"));
        if (quest == null) {
            throw new QuestException("Wrong config! Unknown quest given: " + getConfig().getString("quest"));
        }
        component.getQuestHolder(player).startQuest(quest);
    }
}
