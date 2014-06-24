package de.raidcraft.quests.builder;

import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.quests.QuestPlugin;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class QuestBuilder extends ConfigBuilder<QuestPlugin> {

    protected QuestBuilder(QuestPlugin plugin, Player player, String basePath) {

        super(plugin, player, basePath);
    }
}
