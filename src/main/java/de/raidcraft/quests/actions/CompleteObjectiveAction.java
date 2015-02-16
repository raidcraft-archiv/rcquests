package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.PlayerObjective;
import de.raidcraft.quests.api.Quest;
import de.raidcraft.quests.api.QuestException;
import de.raidcraft.quests.api.QuestHolder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author mdoering
 */
public class CompleteObjectiveAction implements Action<Player> {

    @Override
    public void accept(Player player, ConfigurationSection config) {

        try {
            QuestManager component = RaidCraft.getComponent(QuestManager.class);
            QuestHolder questHolder = component.getQuestHolder(player);
            Quest quest = questHolder.getQuest(config.getString("quest"));
            Optional<PlayerObjective> objective = quest.getUncompletedObjectives().stream()
                    .filter(obj -> obj.getId() == config.getInt("objective"))
                    .findFirst();
            if (objective.isPresent()) {
                objective.get().complete();
            } else {
                player.sendMessage(ChatColor.RED + "Invalid objective given for action in quest: " + quest.getFullName());
            }
        } catch (QuestException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}
