package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementException;
import de.raidcraft.api.action.RequirementFactory;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.TriggerManager;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import de.raidcraft.quests.api.quest.AbstractQuestTemplate;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleQuestTemplate extends AbstractQuestTemplate {

    protected SimpleQuestTemplate(String id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    public boolean processTrigger(Player player) {

        QuestHolder questHolder = RaidCraft.getComponent(QuestManager.class).getQuestHolder(player);
        Optional<Quest> quest = questHolder.getQuest(this);
        if (!quest.isPresent()) return true;
        // lets check if we already have a quest that is started
        // and do not execute actions if the quest is started
        if (quest.get().isActive()) {
            return false;
        }
        // if holder has completed the quest also pass the trigger
        if (quest.get().isCompleted()) {
            // lets check if the quest is repeatable and allow the trigger if the cooldown is over
            if (isRepeatable()
                    && quest.get().getCompletionTime().toInstant().plusSeconds(getCooldown()).isBefore(Instant.now())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Collection<ObjectiveTemplate> loadObjectives(ConfigurationSection data) {

        List<ObjectiveTemplate> objectiveTemplates = new ArrayList<>();
        if (data == null) return objectiveTemplates;
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    objectiveTemplates.add(new SimpleObjectiveTemplate(Integer.parseInt(key), this, data.getConfigurationSection(key)));
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning(getId() + ": " + "Wrong objective id in " + getId() + ": " + key);
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
    protected Collection<Requirement<Player>> loadRequirements(ConfigurationSection data) {

        try {
            return RequirementFactory.getInstance().createRequirements(getListenerId(), data, Player.class);
        } catch (RequirementException e) {
            RaidCraft.LOGGER.warning(getId() + ": " + data.getRoot().getName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    protected Collection<Action<Player>> loadActions(ConfigurationSection data) {

        try {
            return ActionFactory.getInstance().createActions(data, Player.class);
        } catch (ActionException e) {
            RaidCraft.LOGGER.warning(getId() + ": " + data.getRoot().getName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
