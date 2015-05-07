package de.raidcraft.quests;

import de.raidcraft.api.random.GenericRDSTable;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public class QuestPool extends GenericRDSTable {

    private final String name;

    public QuestPool(String name, ConfigurationSection config) {

        this.name = name;
        ConfigurationSection section = config.getConfigurationSection("quests");
        if (section != null) {
            for (String key : section.getKeys(false)) {

            }
        }
    }
}
