package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface Requirement {

    public int getId();

    public String getType();

    public boolean isMet(Quest quest);
}
