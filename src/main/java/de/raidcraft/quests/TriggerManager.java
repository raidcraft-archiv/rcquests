package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.quests.quest.trigger.Trigger;

/**
 * @author Silthus
 */
public final class TriggerManager implements Component {

    private final QuestPlugin plugin;

    protected TriggerManager(QuestPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(TriggerManager.class, this);
    }

    public void registerTrigger(Trigger... triggers) {

        for (Trigger trigger : triggers) {
            // also load the counter part that actually trigger this
            Quests.initializeTrigger(trigger, trigger.getConfig());
            RaidCraft.LOGGER.info("[Quest] Registered Trigger: " + trigger.getName());
        }
    }
}
