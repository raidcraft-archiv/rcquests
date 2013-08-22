package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface Objective {

    public int getId();

    public String getFriendlyName();

    public String getDescription();

    public Requirement[] getRequirements();

    public Trigger[] getTrigger();

    public Action[] getActions();
}
