package de.raidcraft.quests.api.quest;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface QuestTemplate extends TriggerListener<Player> {

    String getId();

    String getBasePath();

    String getName();

    String getFriendlyName();

    String getAuthor();

    String getDescription();

    int getRequiredObjectiveAmount();

    boolean isRepeatable();

    long getCooldown();

    boolean isOrdered();

    boolean isLocked();

    Collection<ObjectiveTemplate> getObjectiveTemplates();

    Collection<Requirement<Player>> getRequirements();

    Collection<TriggerFactory> getStartTrigger();

    Collection<TriggerFactory> getCompletionTrigger();

    Collection<Action<Player>> getCompletionActions();
}
