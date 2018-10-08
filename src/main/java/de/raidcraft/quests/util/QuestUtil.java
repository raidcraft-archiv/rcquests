package de.raidcraft.quests.util;

import de.raidcraft.api.conversations.conversation.DefaultConversation;
import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class QuestUtil {

    public static FancyMessage getQuestTooltip(FancyMessage msg, Quest quest) {

        FancyMessage tooltip = new FancyMessage();

        tooltip.then(quest.getTemplate().getFriendlyName()).color(ChatColor.GOLD)
                .newLine();

        if (!Objects.isNull(quest.getDescription())) {
            tooltip.then(quest.getDescription()).style(ChatColor.ITALIC).color(ChatColor.GOLD).newLine();
        }

        tooltip.newLine();

        for (PlayerObjective objective : quest.getObjectives()) {
            if (objective.getObjectiveTemplate().isHidden()) continue;
            tooltip.then("  * ").color(ChatColor.GOLD).style(ChatColor.RESET)
                    .then(objective.getObjectiveTemplate().getFriendlyName())
                    .style(objective.isCompleted() ? ChatColor.STRIKETHROUGH : ChatColor.RESET)
                    .color(objective.isActive() ? ChatColor.AQUA : ChatColor.GRAY)
                    .newLine();
        }

        return msg.then("[").color(ChatColor.DARK_BLUE)
                .then(quest.getTemplate().getFriendlyName()).color(ChatColor.GOLD)
                .formattedTooltip(tooltip)
                .then("]").color(ChatColor.DARK_BLUE);
    }

    public static Map<Quest.Phase, Collection<DefaultConversation>> loadDefaultConversations(ConfigurationSection data) {
        HashMap<Quest.Phase, Collection<DefaultConversation>> conversations = new HashMap<>();
        Arrays.stream(Quest.Phase.values()).forEach(phase -> conversations.put(phase, new ArrayList<>()));
        if (data == null) return conversations;

        for (Quest.Phase phase : Quest.Phase.values()) {
            conversations.get(phase).addAll(DefaultConversation.fromConfig(data.getConfigurationSection(phase.getConfigName())));
        }

        return conversations;
    }

    public static Map<Quest.Phase, Boolean> loadDefaultConversationsClearingMap(ConfigurationSection section) {
        HashMap<Quest.Phase, Boolean> clearingMap = new HashMap<>();
        Arrays.stream(Quest.Phase.values()).forEach(phase -> clearingMap.put(phase, true));
        if (section == null) return clearingMap;

        for (Quest.Phase phase : Quest.Phase.values()) {
            ConfigurationSection phaseSection = section.getConfigurationSection(phase.getConfigName());
            if (phaseSection != null) {
                clearingMap.put(phase, phaseSection.getBoolean("clear", true));
            }
        }

        return clearingMap;
    }
}
