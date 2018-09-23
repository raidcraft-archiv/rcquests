package de.raidcraft.quests.requirements;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.holder.QuestHolder;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Data
public class QuestActiveRequirement implements Requirement<Player> {

    private final QuestManager questManager;

    @Information(
            value = "quest.active",
            desc = "Checks if the given quest is currently active.",
            conf = {
                    "quest: <id>"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {
        QuestHolder holder = getQuestManager().getQuestHolder(player);
        return holder != null && holder.hasActiveQuest(config.getString("quest"));
    }
}
