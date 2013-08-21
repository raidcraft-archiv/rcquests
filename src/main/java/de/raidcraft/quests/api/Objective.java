package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface Objective {

    public String getId();

    public String getFriendlyName();

    public String getDescription();

    public Requirement[] getRequirements();

    public Trigger[] getTrigger();

    public Action[] getCompletionActions();
}
