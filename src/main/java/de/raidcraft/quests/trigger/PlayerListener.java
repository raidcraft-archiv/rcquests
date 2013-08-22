package de.raidcraft.quests.trigger;

import de.raidcraft.api.quests.QuestTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Silthus
 */
public class PlayerListener extends QuestTrigger implements Listener {

    @Override
    protected void load(ConfigurationSection data) {


    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {


    }
}
