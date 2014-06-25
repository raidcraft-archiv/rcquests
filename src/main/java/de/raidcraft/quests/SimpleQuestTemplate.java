package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.*;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.quests.holder.QuestHolder;
import de.raidcraft.api.quests.quest.AbstractQuestTemplate;
import de.raidcraft.api.quests.objective.ObjectiveTemplate;
import de.raidcraft.api.quests.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleQuestTemplate extends AbstractQuestTemplate {

    protected SimpleQuestTemplate(String id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    protected Collection<ObjectiveTemplate> loadObjectives(ConfigurationSection data) {

        List<ObjectiveTemplate> objectiveTemplates = new ArrayList<>();
        if (data == null) return objectiveTemplates;
        Set <String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    objectiveTemplates.add(new SimpleObjectiveTemplate(Integer.parseInt(key), this, data.getConfigurationSection(key)));
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning("Wrong objective id in " + getId() + ": " + key);
                }
            }
        }
        return objectiveTemplates;
    }

    @Override
    protected Collection<TriggerFactory> loadStartTrigger(ConfigurationSection data) {

        return TriggerManager.getInstance().createTriggerFactories(data);
    }

    @Override
    protected Collection<TriggerFactory> loadCompletionTrigger(ConfigurationSection data) {

        return TriggerManager.getInstance().createTriggerFactories(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<Requirement<Player>> loadRequirements(ConfigurationSection data) {

        return RequirementFactory.getInstance().createRequirements(data, Player.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<Action<Player>> loadActions(ConfigurationSection data) {

        return ActionFactory.getInstance().createActions(data, Player.class);
    }
}
