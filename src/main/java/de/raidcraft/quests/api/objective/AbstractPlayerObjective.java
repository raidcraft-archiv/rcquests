package de.raidcraft.quests.api.objective;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.events.ObjectiveCompleteEvent;
import de.raidcraft.quests.api.events.ObjectiveStartedEvent;
import de.raidcraft.quests.api.events.TaskCompletedEvent;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@Data
@EqualsAndHashCode(of = {"id", "quest", "objectiveTemplate"})
public abstract class AbstractPlayerObjective implements PlayerObjective {

    private final int id;
    private final Quest quest;
    private final ObjectiveTemplate objectiveTemplate;
    private final List<PlayerTask> tasks;
    private boolean active = false;
    private Timestamp completionTime;
    private Timestamp abortionTime;

    public AbstractPlayerObjective(int id, Quest quest, ObjectiveTemplate objectiveTemplate) {

        this.id = id;
        this.quest = quest;
        this.objectiveTemplate = objectiveTemplate;
        this.tasks = loadTasks();
    }

    protected abstract List<PlayerTask> loadTasks();

    @Override
    public String getListenerId() {

        return getQuest().getListenerId() + "." + getId();
    }

    @Override
    public boolean processTrigger(Player player) {

        if (!player.equals(getQuest().getHolder().getPlayer())) {
            return false;
        }
        if (hasCompletedAllTasks() && getObjectiveTemplate().getRequirements().stream()
                .allMatch(requirement -> requirement.test(player))) {
            if (getObjectiveTemplate().isAutoCompleting()) {
                complete();
            }
        }
        return true;
    }

    public void updateListeners() {

        if (!isCompleted() && !isAborted()) {
            if (!isActive()) {
                // execute our objective start actions
                getObjectiveTemplate().getStartActions().forEach(action -> action.accept(getQuestHolder().getPlayer()));
                // register our start trigger
                getObjectiveTemplate().getTrigger().forEach(factory -> factory.registerListener(this));
                getUncompletedTasks().forEach(PlayerTask::updateListeners);
                setActive(true);
                ObjectiveStartedEvent event = new ObjectiveStartedEvent(this);
                RaidCraft.callEvent(event);
            }
        } else {
            unregisterListeners();
        }
    }

    public void unregisterListeners() {

        getObjectiveTemplate().getTrigger().forEach(factory -> factory.unregisterListener(this));
        unregisterTaskListeners();
        setActive(false);
    }

    private void updateTaskListeners() {
        if (isCompleted()) {
            unregisterListeners();
            return;
        }
        if (!isActive()) {
            // do not register task listeners if the objective is not started
            return;
        }
        if (hasCompletedAllTasks()) {
            unregisterTaskListeners();
            return;
        }

        for (PlayerTask task : getTasks()) {
            if (!task.isCompleted()) {
                // lets register the listeners of our task
                task.updateListeners();
            } else {
                task.unregisterListeners();
            }
        }
    }

    private boolean hasCompletedAllTasks() {
        if (getTasks().isEmpty()) return true;
        List<PlayerTask> uncompletedTasks = getUncompletedTasks();
        boolean completed = uncompletedTasks.isEmpty()
                || (getObjectiveTemplate().getRequiredTaskCount() > 0
                && getObjectiveTemplate().getRequiredTaskCount() <= uncompletedTasks.size());
        if (!uncompletedTasks.isEmpty() && !completed) {
            int optionalObjectives = 0;
            for (PlayerTask task : uncompletedTasks) {
                if (task.getTaskTemplate().isOptional()) optionalObjectives++;
            }
            if (optionalObjectives == uncompletedTasks.size()) {
                completed = true;
            }
        }
        return completed;
    }

    private List<PlayerTask> getUncompletedTasks() {
        return getTasks().stream().filter(task -> !task.isCompleted()).collect(Collectors.toList());
    }

    private void unregisterTaskListeners() {
        getTasks().forEach(PlayerTask::unregisterListeners);
    }

    @Override
    public QuestHolder getQuestHolder() {

        return quest.getHolder();
    }

    @Override
    public boolean isCompleted() {

        return completionTime != null;
    }

    @Override
    public boolean isAborted() {

        return abortionTime != null;
    }

    @Override
    public void onTaskComplete(PlayerTask task) {
        save();
        TaskCompletedEvent event = new TaskCompletedEvent(task);
        RaidCraft.callEvent(event);
        updateTaskListeners();
    }

    @Override
    public void complete() {

        if (isCompleted()) return;
        ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(this);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) return;
        unregisterListeners();
        this.completionTime = new Timestamp(System.currentTimeMillis());
        save();
        // lets execute all objective actions
        getObjectiveTemplate().getActions().forEach(action -> action.accept(getQuestHolder().getPlayer()));
        getQuest().onObjectCompletion(this);
    }

    @Override
    public void abort() {

        unregisterListeners();
        setCompletionTime(null);
        setAbortionTime(Timestamp.from(Instant.now()));
        save();
    }

    @Override
    public int compareTo(@NonNull PlayerObjective o) {

        return this.getObjectiveTemplate().compareTo(o.getObjectiveTemplate());
    }
}
