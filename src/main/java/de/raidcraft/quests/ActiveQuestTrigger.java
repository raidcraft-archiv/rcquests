package de.raidcraft.quests;

import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class ActiveQuestTrigger extends SimpleTrigger {

    private final List<Action<Trigger>> successActions = new ArrayList<>();
    private final List<Action<Trigger>> failActions = new ArrayList<>();

    public ActiveQuestTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data, Type type) {

        super(id, questTemplate, data, type);
        successActions.addAll(QuestManager.loadActions((Trigger) this, data.getConfigurationSection("success-actions"), getQuestTemplate().getBasePath()));
        failActions.addAll(QuestManager.loadActions((Trigger) this, data.getConfigurationSection("fail-actions"), getQuestTemplate().getBasePath()));
    }

    @Override
    protected void execute(QuestHolder holder) {

        Quest quest = holder.getQuest(getQuestTemplate());
        // execute success actions if all objectives are complete but the quest isnt
        if (!quest.isCompleted() && quest.hasCompletedAllObjectives()) {
            for (Action<Trigger> action : successActions) {
                try {
                    action.execute(holder, this);
                } catch (QuestException e) {
                    holder.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
        // execute our fail actions if not all objectives are complete
        if (!quest.isCompleted() && quest.isActive() && !quest.hasCompletedAllObjectives()) {
            for (Action<Trigger> action : failActions) {
                try {
                    action.execute(holder, this);
                } catch (QuestException e) {
                    holder.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
        super.execute(holder);
    }
}
