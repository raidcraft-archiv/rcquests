package de.raidcraft.quests.api.quest;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
@Data()
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"completionActions", "requirements", "objectiveTemplates", "startTrigger", "completionTrigger"})
public abstract class AbstractQuestTemplate implements QuestTemplate {

    private final String id;
    private final String name;
    private final String author;
    private final String basePath;
    private final String friendlyName;
    private final String description;
    private final int requiredObjectiveAmount;
    private final boolean ordered;
    private final boolean locked;
    private final boolean repeatable;
    private final long cooldown;
    private final Collection<Action<Player>> completionActions;
    private final Collection<Requirement<Player>> requirements;
    private final Collection<ObjectiveTemplate> objectiveTemplates;
    private final Collection<TriggerFactory> startTrigger;
    private final Collection<TriggerFactory> completionTrigger;

    public AbstractQuestTemplate(String id, ConfigurationSection data) {

        this.id = id;
        String[] split = id.split("\\.");
        this.name = split[split.length - 1];
        this.basePath = id.replace("." + name, "");
        this.friendlyName = data.getString("name", name);
        this.author = data.getString("author", "Raid-Craft Team");
        this.description = data.getString("desc");
        this.requiredObjectiveAmount = data.getInt("required", 0);
        this.ordered = data.getBoolean("ordered", false);
        this.locked = data.getBoolean("locked", true);
        this.cooldown = data.getLong("cooldown", 0);
        this.repeatable = cooldown > 0 || data.getBoolean("repeatable", false);
        this.requirements = loadRequirements(data.getConfigurationSection("requirements"));
        this.objectiveTemplates = loadObjectives(data.getConfigurationSection("objectives"));
        this.startTrigger = loadStartTrigger(data.getConfigurationSection("trigger"));
        this.completionTrigger = loadCompletionTrigger(data.getConfigurationSection("complete-trigger"));
        this.completionActions = loadActions(data.getConfigurationSection("complete-actions"));
    }

    @Override
    public String getListenerId() {

        return getId();
    }

    public void registerListeners() {

        startTrigger.forEach(trigger -> trigger.registerListener(this));
    }

    protected abstract Collection<Requirement<Player>> loadRequirements(ConfigurationSection data);

    protected abstract Collection<ObjectiveTemplate> loadObjectives(ConfigurationSection data);

    protected abstract Collection<TriggerFactory> loadStartTrigger(ConfigurationSection data);

    protected abstract Collection<TriggerFactory> loadCompletionTrigger(ConfigurationSection data);

    protected abstract Collection<Action<Player>> loadActions(ConfigurationSection data);
}
