package de.raidcraft.quests.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.quests.QuestHostInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Silthus
 */
public class HostTrigger extends Trigger implements Listener {

    public HostTrigger() {

        super("host", "interact");
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestHostInteract(QuestHostInteractEvent event) {

        RaidCraft.LOGGER.info("Triggered host.interact on " + event.getHost().getId() + " from " + event.getPlayer().getName());
        informListeners("interact", event.getPlayer(), config -> event.getHost().getId().equalsIgnoreCase(config.getString("host")));
    }
}
