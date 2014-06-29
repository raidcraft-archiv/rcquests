package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.holder.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.quests.QuestManager;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class CompleteQuestAction implements Action<Player> {

    @Override
    @SneakyThrows
    public void accept(Player player) {

        if (!player.hasPermission("rcquests.quest.complete")) {
            throw new QuestException("Du hast nicht das Recht Quests zu beenden!");
        }
        try {
            QuestManager component = RaidCraft.getComponent(QuestManager.class);
            QuestHolder questHolder = component.getQuestHolder(player);
            Quest quest = questHolder.getQuest(getConfig().getString("quest"));
            quest.complete();
        } catch (Exception e) {
            throw new QuestException(e.getMessage());
        }
    }
}
