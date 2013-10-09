package de.raidcraft.quests.api.quest;

import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.objective.Objective;
import de.raidcraft.quests.api.quest.requirement.Requirement;
import de.raidcraft.quests.api.quest.trigger.Trigger;

import java.util.List;

/**
 * @author Silthus
 */
public interface QuestTemplate {

    public String getId();

    public String getBasePath();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public int getRequiredObjectiveAmount();

    public boolean isOrdered();

    public Requirement[] getRequirements();

    public Objective[] getObjectives();

    public Trigger[] getTrigger();

    public Trigger[] getCompleteTrigger();

    public List<Action<QuestTemplate>> getCompleteActions();
}
