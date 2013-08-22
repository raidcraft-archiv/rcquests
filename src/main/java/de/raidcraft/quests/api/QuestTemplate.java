package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface QuestTemplate {

    public String getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public Requirement[] getRequirements();

    public Objective[] getObjectives();

    public Trigger[] getTrigger();

    public Action[] getActions();
}
