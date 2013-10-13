package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.trigger.AbstractTrigger;
import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.trigger.Trigger;
import de.raidcraft.quests.api.quest.trigger.TriggerListener;
import de.raidcraft.quests.util.QuestUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimpleTrigger extends AbstractTrigger {

    public SimpleTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data, Type type) {

        super(id, questTemplate, data, type);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestUtil.loadActions((Trigger) this, data, getQuestTemplate().getBasePath()));
    }

    @Override
    public void trigger(final Player player) {

        QuestPlugin plugin = RaidCraft.getComponent(QuestPlugin.class);
        QuestHolder questHolder = plugin.getQuestManager().getPlayer(player);
        if (getType() == Type.QUEST_START && questHolder.hasQuest(getQuestTemplate().getId())) {
            return;
        }
        if (getType() == Type.QUEST_ACCEPTED && !questHolder.hasActiveQuest(getQuestTemplate().getId())) {
            return;
        }
        if (getDelay() > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {

                    execute(player);
                }
            }, getDelay());
        } else {
            // we have a match now lets execute our actions
            execute(player);
        }
    }

    private void execute(Player player) {

        // we have a match now lets execute our actions
        for (Action<Trigger> action : getActions()) {
            try {
                action.execute(player, this);
            } catch (QuestException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        }
        // and inform our listeners
        for (TriggerListener listener : getListeners()) {
            listener.trigger(player);
        }
    }
}
