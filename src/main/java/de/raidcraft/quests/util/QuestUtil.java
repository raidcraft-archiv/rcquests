package de.raidcraft.quests.util;

import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class QuestUtil {

    public static FancyMessage getQuestTooltip(FancyMessage msg, Quest quest) {

        List<FancyMessage> tooltip = new ArrayList<>();
        tooltip.add(new FancyMessage(quest.getTemplate().getFriendlyName()).color(ChatColor.YELLOW));

        if (!Objects.isNull(quest.getDescription())) {
            String[] lines = quest.getDescription().split("\\|");
            for (String line : lines) {
                tooltip.add(new FancyMessage(line).style(ChatColor.ITALIC).color(ChatColor.GOLD));
            }
        }

        List<PlayerObjective> objectives = quest.getObjectives();
        tooltip.addAll(objectives.stream()
                .filter(objective -> !objective.getObjectiveTemplate().isHidden())
                .map(objective -> new FancyMessage("  * ")
                        .then(objective.getObjectiveTemplate().getFriendlyName())
                        .style(objective.isCompleted() ? ChatColor.STRIKETHROUGH : ChatColor.UNDERLINE)
                        .color(objective.isActive() ? ChatColor.AQUA : ChatColor.GRAY)).collect(Collectors.toList()));

        return msg.then("[").color(ChatColor.DARK_BLUE)
                .then(quest.getTemplate().getFriendlyName()).color(ChatColor.GOLD)
                .formattedTooltip(tooltip.toArray(new FancyMessage[tooltip.size()]))
                .then("]").color(ChatColor.DARK_BLUE);
    }
}
