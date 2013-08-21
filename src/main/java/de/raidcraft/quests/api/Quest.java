package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public interface Quest {

    public QuestTemplate getTemplate();

    public QuestHolder getHolder();

    public Player getPlayer();

    public boolean isCompleted();

    public boolean isActive();

    public Timestamp getStartTime();

    public Timestamp getCompletionTime();
}
