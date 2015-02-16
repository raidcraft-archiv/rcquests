package de.raidcraft.quests.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.QuestManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class HasCompletedQuestRequirement implements Requirement<Player> {

    @Override
    public boolean test(Player player, ConfigurationSection config) {

        return RaidCraft.getComponent(QuestManager.class).getQuestHolder(player).hasCompletedQuest(config.getString("quest"));
    }
}
