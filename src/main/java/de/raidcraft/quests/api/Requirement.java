package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface Requirement {

    public int getId();

    public String getName();

    public boolean isMet(Quest quest);
}
