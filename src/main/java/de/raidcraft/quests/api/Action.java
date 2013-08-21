package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface Action {

    public int getId();

    public String getName();

    public void execute(Quest quest);
}
