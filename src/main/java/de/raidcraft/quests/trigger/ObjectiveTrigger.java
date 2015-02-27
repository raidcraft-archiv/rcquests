package de.raidcraft.quests.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.quests.api.events.ObjectiveCompleteEvent;
import de.raidcraft.quests.api.events.ObjectiveCompletedEvent;
import de.raidcraft.quests.api.events.ObjectiveStartedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class ObjectiveTrigger extends Trigger implements Listener {

    public ObjectiveTrigger() {

        super("objective", "complete", "completed", "started");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onObjectiveStart(ObjectiveStartedEvent event) {

        informListeners("started", event.getObjective().getQuest().getPlayer(),
                config -> event.getObjective().getId() == config.getInt("objective")
                && event.getObjective().getQuest().getFullName().equals(config.getString("quest"))
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onObjectiveComplete(ObjectiveCompleteEvent event) {

        informListeners("complete", event.getObjective().getQuest().getPlayer(),
                config -> event.getObjective().getId() == config.getInt("objective")
                        && event.getObjective().getQuest().getFullName().equals(config.getString("quest"))
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onObjectiveCompleted(ObjectiveCompletedEvent event) {

        informListeners("completed", event.getObjective().getQuest().getPlayer(),
                config -> event.getObjective().getId() == config.getInt("objective")
                        && event.getObjective().getQuest().getFullName().equals(config.getString("quest"))
        );
    }
}
