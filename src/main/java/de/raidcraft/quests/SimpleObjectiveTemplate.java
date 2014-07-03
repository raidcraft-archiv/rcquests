package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementException;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.quests.objective.AbstractObjectiveTemplate;
import de.raidcraft.api.quests.quest.QuestTemplate;
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

        try {
            return RequirementFactory.getInstance().createRequirements(data, Player.class);
        } catch (RequirementException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in " + getQuestTemplate().getId());
        }
        return new ArrayList<>();
    }

    @Override
    protected Collection<TriggerFactory> loadTrigger(ConfigurationSection data) {

        return TriggerManager.getInstance().createTriggerFactories(data);
    }

    @Override
    protected Collection<Action<Player>> loadActions(ConfigurationSection data) {

        try {
            return ActionFactory.getInstance().createActions(data, Player.class);
        } catch (ActionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in " + getQuestTemplate().getId());
        }
        return new ArrayList<>();
    }
}
