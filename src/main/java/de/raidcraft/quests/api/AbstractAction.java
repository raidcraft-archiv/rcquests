package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractAction implements Action {

    private final int id;
    private final String type;

    public AbstractAction(int id, ConfigurationSection data) {

        this.id = id;
        this.type = data.getString("type");
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getType() {

        return type;
    }
}
