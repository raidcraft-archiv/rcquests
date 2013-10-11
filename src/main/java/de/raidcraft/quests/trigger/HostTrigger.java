package de.raidcraft.quests.trigger;

import de.raidcraft.api.quests.QuestHostInteractEvent;
import de.raidcraft.api.quests.QuestTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Silthus
 */
@QuestTrigger.Name("host")
public class HostTrigger extends QuestTrigger implements Listener {

    private String hostId;

    @Override
    protected void load(ConfigurationSection data) {

        this.hostId = data.getString("host");
    }

    @Method("interact")
    @EventHandler(ignoreCancelled = true)
    public void onInteract(QuestHostInteractEvent event) {

        if (event.getHost().getId().equalsIgnoreCase(hostId)) {
            inform("interact", event.getPlayer());
        }
    }
}