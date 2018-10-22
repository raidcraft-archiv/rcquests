package de.raidcraft.quests.requirements;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.holder.QuestHolder;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Data
public class QuestCompletedRequirement implements Requirement<Player> {

    private final QuestManager questManager;

    @Information(
            value = "quest.completed",
            aliases = {"quest.complete"},
            desc = "Checks if the given quest was completed.",
            conf = {
                    "quest: <id>"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {
        QuestHolder holder = getQuestManager().getQuestHolder(player);
        return holder != null && holder.hasCompletedQuest(config.getString("quest"));
    }
}
