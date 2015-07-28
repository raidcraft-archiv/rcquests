package de.raidcraft.quests.api.objective;

import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.api.quest.QuestTemplate;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface ObjectiveTemplate extends Comparable<ObjectiveTemplate> {

    int getId();

    String getFriendlyName();

    String getDescription();

    boolean isOptional();

    boolean isHidden();

    boolean isSilent();

    boolean isAutoCompleting();

    QuestTemplate getQuestTemplate();

    Collection<Requirement<Player>> getRequirements();

    Collection<TriggerFactory> getTrigger();

    Collection<Action<Player>> getActions();
}
