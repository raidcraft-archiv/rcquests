package de.raidcraft.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public final class TriggerManager {

    private final QuestPlugin plugin;

    protected TriggerManager(QuestPlugin plugin) {

        this.plugin = plugin;
    }

    protected void callTrigger(String name, Player player, ConfigurationSection data) {


    }
}
