package de.raidcraft.quests.trigger;

import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.quests.events.QuestHostInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Silthus
 */
public class HostTrigger extends Trigger implements Listener {

    public HostTrigger() {

        super("host", "interact");
    }

    @Information(
            value = "host.interact",
            desc = "Is triggered when the player interacts with the given host.",
            conf = {
                    "host: <id>"
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onQuestHostInteract(QuestHostInteractEvent event) {

        informListeners("interact", event.getPlayer(),
                config -> event.getQuestHost().getId().equalsIgnoreCase(config.getString("host")));
    }
}
