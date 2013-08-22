package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener {

    public int getId();

    public Quest getQuest();

    public Objective getObjective();

    public Player getPlayer();

    public Timestamp getCompletionTime();

    public boolean isCompleted();

    public void save();
}
