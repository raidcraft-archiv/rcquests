package de.raidcraft.quests.api.quest;

import de.raidcraft.quests.api.player.PlayerObjective;
import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.trigger.TriggerListener;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
public interface Quest extends TriggerListener {

    public int getId();

    public String getName();

    public String getFullName();

    public String getFriendlyName();

    public String getDescription();

    public List<PlayerObjective> getPlayerObjectives();

    public List<PlayerObjective> getUncompletedObjectives();

    public QuestTemplate getTemplate();

    public QuestHolder getHolder();

    public Player getPlayer();

    public boolean isCompleted();

    public boolean hasCompletedAllObjectives();

    public void completeObjective(PlayerObjective objective);

    public boolean isActive();

    public Timestamp getStartTime();

    public Timestamp getCompletionTime();

    public void start();

    public void complete();

    public void abort();

    public void save();
}
