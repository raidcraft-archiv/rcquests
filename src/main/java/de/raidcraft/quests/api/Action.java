package de.raidcraft.quests.api;

/**
 * @author Silthus
 */
public interface Action {

    public int getId();

    public String getType();

    public void execute(Quest quest);
}
