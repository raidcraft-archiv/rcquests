package de.raidcraft.quests.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestTrigger;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Silthus
 */
@QuestTrigger.Name("location")
public class LocationTrigger extends QuestTrigger implements Listener {

    private Location location;
    private int radius;

    @Override
    protected void load(ConfigurationSection data) {

        location = new Location(Bukkit.getWorld(data.getString("world", "world")), data.getInt("x"), data.getInt("y"), data.getInt("z"));
        radius = data.getInt("radius", 0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {

        Location to = event.getTo();
        if (RaidCraft.hasMoved(event.getPlayer(), to)) {
            if (radius > 0 && LocationUtil.isWithinRadius(to, location, radius)) {
                inform("move", event.getPlayer());
            } else if (to.getBlockX() == location.getBlockX()
                    && to.getBlockY() == location.getBlockY()
                    && to.getBlockZ() == location.getBlockZ()) {
                inform("move", event.getPlayer());
            }
        }
    }
}
