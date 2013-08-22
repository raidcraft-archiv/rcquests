package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractObjective implements Objective {

    private final int id;
    private final String friendlyName;
    private final String description;
    private final QuestTemplate questTemplate;
    protected Requirement[] requirements = new Requirement[0];
    protected Trigger[] triggers = new Trigger[0];
    protected List<Action<Objective>> actions = new ArrayList<>();

    public AbstractObjective(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        this.id = id;
        this.friendlyName = data.getString("name");
        this.description = data.getString("description");
        this.questTemplate = questTemplate;
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
    public QuestTemplate getQuestTemplate() {

        return questTemplate;
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
    public List<Action<Objective>> getActions() {

        return actions;
    }

    protected void setActions(List<Action<Objective>> actions) {

        this.actions = actions;
    }
}
