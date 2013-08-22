package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
public interface Quest extends TriggerListener {

    public String getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public List<PlayerObjective> getPlayerObjectives();

    public List<PlayerObjective> getUncompletedObjectives();

    public QuestTemplate getTemplate();

    public QuestHolder getHolder();

    public Player getPlayer();

    public boolean isCompleted();

    public boolean isActive();

    public Timestamp getStartTime();

    public Timestamp getCompletionTime();

    public void start();
}
