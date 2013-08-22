package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractTrigger implements Trigger {

    private final int id;
    private final String type;
    protected Action[] actions = new Action[0];

    public AbstractTrigger(int id, ConfigurationSection data) {

        this.id = id;
        this.type = data.getString("type");
        loadActions(data.getConfigurationSection("action"));
    }

    protected abstract void loadActions(ConfigurationSection data);

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public Action[] getActions() {

        return actions;
    }
}
