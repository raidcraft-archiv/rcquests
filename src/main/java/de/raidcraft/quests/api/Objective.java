package de.raidcraft.quests.api;

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
