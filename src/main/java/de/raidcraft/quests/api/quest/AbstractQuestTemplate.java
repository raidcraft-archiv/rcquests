package de.raidcraft.quests.api.quest;

import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.objective.Objective;
import de.raidcraft.quests.api.quest.requirement.Requirement;
import de.raidcraft.quests.api.quest.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractQuestTemplate implements QuestTemplate {

    private final String id;
    private final String name;
    private final String basePath;
    private final String friendlyName;
    private final String description;
    private final int requiredObjectiveAmount;
    private final boolean ordered;
    private List<Action<QuestTemplate>> actions = new ArrayList<>();
    protected Requirement[] requirements = new Requirement[0];
    protected Objective[] objectives = new Objective[0];
    protected Trigger[] triggers = new Trigger[0];

    public AbstractQuestTemplate(String id, ConfigurationSection data) {

        this.id = id;
        String[] split = id.split("\\.");
        this.name = split[split.length - 1];
        this.basePath = id.replace("." + name, "");
        this.friendlyName = data.getString("name", name);
        this.description = data.getString("description");
        this.requiredObjectiveAmount = data.getInt("required", 0);
        this.ordered = data.getBoolean("ordered", false);
        loadRequirements(data.getConfigurationSection("requirements"));
        loadObjectives(data.getConfigurationSection("objectives"));
        loadTriggers(data.getConfigurationSection("trigger"));
        loadActions(data.getConfigurationSection("complete.actions"));
    }

    protected abstract void loadRequirements(ConfigurationSection data);

    protected abstract void loadObjectives(ConfigurationSection data);

    protected abstract void loadTriggers(ConfigurationSection data);

    protected abstract void loadActions(ConfigurationSection data);

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getBasePath() {

        return basePath;
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
    public int getRequiredObjectiveAmount() {

        return requiredObjectiveAmount;
    }

    @Override
    public boolean isOrdered() {

        return ordered;
    }

    @Override
    public Requirement[] getRequirements() {

        return requirements;
    }

    @Override
    public Objective[] getObjectives() {

        return objectives;
    }

    @Override
    public Trigger[] getTrigger() {

        return triggers;
    }

    @Override
    public List<Action<QuestTemplate>> getActions() {

        return actions;
    }

    protected void setActions(List<Action<QuestTemplate>> actions) {

        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractQuestTemplate)) return false;

        AbstractQuestTemplate that = (AbstractQuestTemplate) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {

        return id.hashCode();
    }

    @Override
    public String toString() {

        return id;
    }
}
