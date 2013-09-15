package de.raidcraft.quests.api.quest.objective;

import de.raidcraft.quests.api.quest.requirement.Requirement;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.trigger.Trigger;

import java.util.List;

/**
 * @author Silthus
 */
public interface Objective {

    public int getId();

    public String getFriendlyName();

    public String getDescription();

    public QuestTemplate getQuestTemplate();

    public Requirement[] getRequirements();

    public Trigger[] getTrigger();

    public List<Action<Objective>> getActions();
}
