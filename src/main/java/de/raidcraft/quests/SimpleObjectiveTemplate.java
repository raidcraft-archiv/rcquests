package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.api.objective.AbstractObjectiveTemplate;
import de.raidcraft.quests.api.objective.TaskTemplate;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Silthus
 */
public class SimpleObjectiveTemplate extends AbstractObjectiveTemplate {

    protected SimpleObjectiveTemplate(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        super(id, questTemplate, data);
    }

    @Override
    protected Collection<Requirement<Player>> loadRequirements(ConfigurationSection data) {

        return ActionAPI.createRequirements(getQuestTemplate().getListenerId() + "." + getId(), data, Player.class);
    }

    @Override
    protected Collection<TriggerFactory> loadTrigger(ConfigurationSection data) {

        return ActionAPI.createTrigger(data);
    }

    @Override
    protected Collection<Action<Player>> loadActions(ConfigurationSection data) {

        return ActionAPI.createActions(data, Player.class);
    }

    @Override
    protected Collection<TaskTemplate> loadTasks(ConfigurationSection data) {

        ArrayList<TaskTemplate> tasks = new ArrayList<>();
        if (data == null) return tasks;

        for (String key : data.getKeys(false)) {
            try {
                tasks.add(new SimpleTaskTemplate(Integer.parseInt(key), this, data.getConfigurationSection(key)));
            } catch (NumberFormatException e) {
                RaidCraft.LOGGER.warning("Invalid task id " + key + " inside " + ConfigUtil.getFileName(data));
            }
        }

        return tasks;
    }
}
