package de.raidcraft.quests.api.objective;

import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

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

    ObjectiveTemplate getObjectiveTemplate();

    QuestHolder getQuestHolder();

    Timestamp getCompletionTime();

    Timestamp getAbortionTime();

    boolean isActive();

    boolean isCompleted();

    boolean isAborted();

    void complete();

    void abort();

    void updateListeners();

    void unregisterListeners();

    void save();
}
