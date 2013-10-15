package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.trigger.AbstractTrigger;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import de.raidcraft.api.quests.quest.trigger.TriggerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class SimpleTrigger extends AbstractTrigger {

    public SimpleTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data, Type type) {

        super(id, questTemplate, data, type);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestManager.loadActions((Trigger) this, data, getQuestTemplate().getBasePath()));
    }

    @Override
    public void trigger(final QuestHolder holder) {

        QuestPlugin plugin = RaidCraft.getComponent(QuestPlugin.class);
        if (getType() == Type.QUEST_START && holder.hasQuest(getQuestTemplate().getId())) {
            return;
        }
        if (getType() == Type.QUEST_ACCEPTED && !holder.hasActiveQuest(getQuestTemplate().getId())) {
            return;
        }
        if (getDelay() > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {

                    execute(holder);
                }
            }, getDelay());
        } else {
            // we have a match now lets execute our actions
            execute(holder);
        }
    }

    protected void execute(QuestHolder holder) {

        // we have a match now lets execute our actions
        for (Action<Trigger> action : getActions()) {
            try {
                action.execute(holder, this);
            } catch (QuestException e) {
                holder.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            }
        }
        // and inform our listeners
        for (TriggerListener listener : getListeners()) {
            listener.trigger(holder);
        }
    }
}
