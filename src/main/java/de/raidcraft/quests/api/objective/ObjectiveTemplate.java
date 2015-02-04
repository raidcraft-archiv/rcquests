package de.raidcraft.quests.api.objective;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.quests.api.quest.QuestTemplate;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface ObjectiveTemplate extends Comparable<ObjectiveTemplate> {

    public int getId();

    public String getFriendlyName();

    public String getDescription();

    public boolean isOptional();

    public QuestTemplate getQuestTemplate();

    public Collection<Requirement<Player>> getRequirements();

    public Collection<TriggerFactory> getTrigger();

    public Collection<Action<Player>> getActions();
}
