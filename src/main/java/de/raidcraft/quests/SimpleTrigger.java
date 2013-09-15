package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
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

    public SimpleTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        super(id, questTemplate, data);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestUtil.loadActions((Trigger) this, data, getQuestTemplate().getBasePath()));
    }

    @Override
    public void trigger(final Player player) {

        if (getDelay() > 0) {
            Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(QuestPlugin.class), new Runnable() {
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
