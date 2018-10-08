package de.raidcraft.quests.api.quest;

import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.api.conversations.conversation.DefaultConversation;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public interface QuestTemplate extends TriggerListener<Player> {

    String getId();

    String getBasePath();

    String getName();

    String getFriendlyName();

    List<String> getAuthors();

    String getDescription();

    int getRequiredObjectiveAmount();

    boolean isRepeatable();

    boolean isSilent();

    long getCooldown();

    boolean isOrdered();

    boolean isLocked();

    boolean isAutoCompleting();

    Collection<ObjectiveTemplate> getObjectiveTemplates();

    Collection<Requirement<Player>> getStartRequirements();

    Collection<TriggerFactory> getStartTrigger();

    Collection<Action<Player>> getStartActions();

    Collection<TriggerFactory> getActiveTrigger();

    Collection<TriggerFactory> getCompletionTrigger();

    Collection<Action<Player>> getCompletionActions();

    Map<Quest.Phase, Collection<DefaultConversation>> getDefaultConversations();
}
