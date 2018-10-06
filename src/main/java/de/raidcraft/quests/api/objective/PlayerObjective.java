package de.raidcraft.quests.api.objective;

import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener<Player>, Comparable<PlayerObjective> {

    @Override
    default Class<Player> getTriggerEntityType() {

        return Player.class;
    }

    int getId();

    Quest getQuest();

    List<PlayerTask> getTasks();

    void onTaskComplete(PlayerTask task);

    ObjectiveTemplate getObjectiveTemplate();

    QuestHolder getQuestHolder();

    Timestamp getStartTime();

    Timestamp getCompletionTime();

    Timestamp getAbortionTime();

    boolean isActive();

    boolean isStarted();

    boolean isCompleted();

    boolean isAborted();

    void complete();

    void abort();

    void updateListeners();

    void unregisterListeners();

    void save();
}
