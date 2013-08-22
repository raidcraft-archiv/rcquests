package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractObjective implements Objective {

    private final int id;
    private final String friendlyName;
    private final String description;
    protected Requirement[] requirements = new Requirement[0];
    protected Trigger[] triggers = new Trigger[0];
    protected Action[] actions = new Action[0];

    public AbstractObjective(int id, ConfigurationSection data) {

        this.id = id;
        this.friendlyName = data.getString("name");
        this.description = data.getString("description");
        loadRequirements(data.getConfigurationSection("requirements"));
        loadTriggers(data.getConfigurationSection("triggers"));
        loadActions(data.getConfigurationSection("actions"));
    }

    protected abstract void loadRequirements(ConfigurationSection data);

    protected abstract void loadTriggers(ConfigurationSection data);

    protected abstract void loadActions(ConfigurationSection data);

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public Requirement[] getRequirements() {

        return requirements;
    }

    @Override
    public Trigger[] getTrigger() {

        return triggers;
    }

    @Override
    public Action[] getActions() {

        return actions;
    }
}
