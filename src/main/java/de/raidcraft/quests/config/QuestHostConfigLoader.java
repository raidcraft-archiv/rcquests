package de.raidcraft.quests.config;

import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.QuestConfigLoader;
import de.raidcraft.quests.QuestPlugin;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public class QuestHostConfigLoader extends QuestConfigLoader {

    private final QuestPlugin plugin;

    public QuestHostConfigLoader(QuestPlugin plugin) {

        super("host");
        this.plugin = plugin;
    }

    @Override
    public void loadConfig(String id, ConfigurationSection config) {

        String hostType = config.getString("type");
        if (plugin.getQuestManager().isQuestHostType(hostType)) {
            plugin.getQuestManager().createQuestHost(hostType, id, config);
        } else {
            plugin.getLogger().warning("Failed to load quest host \"" + id + "\"! Invalid host type: " + hostType);
        }
    }

    @Override
    public String replaceReference(String key) {

        try {
            return plugin.getQuestManager().getQuestHost(key).getFriendlyName();
        } catch (InvalidQuestHostException e) {
            return key;
        }
    }
}
