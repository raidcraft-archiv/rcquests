package de.raidcraft.quests.api;

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

    public Requirement[] getRequirements();

    public Objective[] getObjectives();

    public Trigger[] getTrigger();

    public List<Action<QuestTemplate>> getActions();
}
