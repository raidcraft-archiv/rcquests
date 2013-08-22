package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractObjective;
import de.raidcraft.quests.api.Action;
import de.raidcraft.quests.api.Requirement;
import de.raidcraft.quests.api.Trigger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleObjective extends AbstractObjective {

    public SimpleObjective(int id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    protected void loadRequirements(ConfigurationSection data) {

        if (data == null) {
            return;
        }
        List<Requirement> requirements = new ArrayList<>();
        Set <String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                requirements.add(new SimpleRequirement(Integer.parseInt(key), data.getConfigurationSection(key)));
            }
        }
        this.requirements = requirements.toArray(new Requirement[requirements.size()]);
    }

    @Override
    protected void loadTriggers(ConfigurationSection data) {

        if (data == null) {
            return;
        }
        List<Trigger> triggers = new ArrayList<>();
        Set <String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                triggers.add(new SimpleTrigger(Integer.parseInt(key), data.getConfigurationSection(key)));
            }
        }
        this.triggers = triggers.toArray(new Trigger[triggers.size()]);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        if (data == null) {
            return;
        }
        List<Action> actions = new ArrayList<>();
        Set <String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                actions.add(new SimpleAction(Integer.parseInt(key), data.getConfigurationSection(key)));
            }
        }
        this.actions = actions.toArray(new Action[actions.size()]);
    }
}
