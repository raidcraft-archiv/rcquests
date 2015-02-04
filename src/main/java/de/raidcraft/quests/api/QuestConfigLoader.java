package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Quest plugin loads all yml files in the quest folder.
 * For each type, e.g. host, conv, quest their is a different loader.
 * @author Silthus
 */
public abstract class QuestConfigLoader {

    private final String suffix;

    public QuestConfigLoader(String suffix) {

        this.suffix = ("." + suffix + ".yml").toLowerCase();
    }

    public String getSuffix() {

        return suffix;
    }

    public abstract void loadConfig(String id, ConfigurationSection config);

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof QuestConfigLoader)) return false;

        QuestConfigLoader loader = (QuestConfigLoader) o;

        return suffix.equals(loader.suffix);
    }

    @Override
    public int hashCode() {

        return suffix.hashCode();
    }
}
