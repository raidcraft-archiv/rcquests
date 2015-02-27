package de.raidcraft.quests.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.quests.api.events.QuestCompleteEvent;
import de.raidcraft.quests.api.events.QuestCompletedEvent;
import de.raidcraft.quests.api.events.QuestStartedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class QuestTrigger extends Trigger implements Listener {

    public QuestTrigger() {

        super("quest", "complete", "completed", "started");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestStart(QuestStartedEvent event) {

        informListeners("started", event.getQuest().getPlayer(), config -> event.getQuest().getFullName().equals(config.getString("quest")));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestComplete(QuestCompleteEvent event) {

        informListeners("complete", event.getQuest().getPlayer(), config -> event.getQuest().getFullName().equals(config.getString("quest")));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestCompleted(QuestCompletedEvent event) {

        informListeners("completed", event.getQuest().getPlayer(), config -> event.getQuest().getFullName().equals(config.getString("quest")));
    }
}
