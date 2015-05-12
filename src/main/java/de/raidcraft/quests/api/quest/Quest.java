package de.raidcraft.quests.api.quest;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.objective.PlayerObjective;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public interface Quest extends TriggerListener<Player> {

    public enum Phase {

        @EnumValue("NOT_STARTED")
        NOT_STARTED,
        @EnumValue("IN_PROGRESS")
        IN_PROGRESS,
        @EnumValue("OBJECTIVES_COMPLETED")
        OJECTIVES_COMPLETED,
        @EnumValue("COMPLETE")
        COMPLETE
    }

    @Override
    public default Class<Player> getTriggerEntityType() {

        return Player.class;
    }

    public int getId();

    public default String getName() {

        return getTemplate().getName();
    }

    public default String getFullName() {

        return getTemplate().getId();
    }

    public default String getFriendlyName() {

        return getTemplate().getFriendlyName();
    }

    public default String getAuthor() {

        return getTemplate().getAuthor();
    }

    public default String getDescription() {

        return getTemplate().getDescription();
    }

    public default Player getPlayer() {

        return getHolder().getPlayer();
    }

    public default List<PlayerObjective> getUncompletedObjectives() {

        return getObjectives().stream()
                .filter(playerObjective -> !playerObjective.isCompleted())
                .sorted()
                .collect(Collectors.toList());
    }

    public List<PlayerObjective> getObjectives();

    public QuestTemplate getTemplate();

    public QuestHolder getHolder();

    public Phase getPhase();

    public boolean isCompleted();

    public boolean hasCompletedAllObjectives();

    public void onObjectCompletion(PlayerObjective objective);

    public boolean isActive();

    public Timestamp getStartTime();

    public Timestamp getCompletionTime();

    public void updateObjectiveListeners();

    public boolean start();

    public boolean complete();

    public boolean abort();

    public void delete();

    public void save();
}
