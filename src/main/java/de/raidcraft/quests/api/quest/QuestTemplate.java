package de.raidcraft.quests.api.quest;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface QuestTemplate extends TriggerListener<Player> {

    @Override
    public default Class<Player> getTriggerEntityType() {

        return Player.class;
    }

    public String getId();

    public String getBasePath();

    public String getName();

    public String getFriendlyName();

    public String getAuthor();

    public String getDescription();

    public int getRequiredObjectiveAmount();

    public boolean isRepeatable();

    public long getCooldown();

    public boolean isOrdered();

    public boolean isLocked();

    public Collection<ObjectiveTemplate> getObjectiveTemplates();

    public Collection<Requirement<Player>> getRequirements();

    public Collection<TriggerFactory> getStartTrigger();

    public Collection<TriggerFactory> getCompletionTrigger();

    public Collection<Action<Player>> getCompletionActions();
}
