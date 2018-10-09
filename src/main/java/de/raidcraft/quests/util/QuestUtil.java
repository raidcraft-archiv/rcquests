package de.raidcraft.quests.util;

import de.raidcraft.api.conversations.conversation.DefaultConversation;
import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.util.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class QuestUtil {

    /**
     * Creates a new quest tooltip and appends it to the given message.
     *
     * @param msg to append tooltip to
     * @param quest to create tooltip from
     * @return quest tooltip with name
     */
    public static FancyMessage getQuestTooltip(FancyMessage msg, Quest quest) {

        FancyMessage tooltip = getQuestTooltip(quest);

        return msg.then("[").color(ChatColor.DARK_BLUE)
                .then(quest.getTemplate().getFriendlyName()).color(ChatColor.GOLD)
                .formattedTooltip(tooltip)
                .then("]").color(ChatColor.DARK_BLUE);
    }

    /**
     * Gets a quest tooltip not wrapped in another text. Just the tooltip.
     *
     * @param quest to get tooltip for
     * @return quest tooltip
     */
    public static FancyMessage getQuestTooltip(Quest quest) {
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

        return tooltip;
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
