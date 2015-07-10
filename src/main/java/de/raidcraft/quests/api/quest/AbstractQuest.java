package de.raidcraft.quests.api.quest;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.api.events.ObjectiveCompletedEvent;
import de.raidcraft.quests.api.events.QuestAbortedEvent;
import de.raidcraft.quests.api.events.QuestCompleteEvent;
import de.raidcraft.quests.api.events.QuestCompletedEvent;
import de.raidcraft.quests.api.events.QuestStartedEvent;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.objective.PlayerObjective;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
@Data
@ToString(exclude = {"playerObjectives", "startTrigger", "completionTrigger"})
@EqualsAndHashCode(of = "id")
public abstract class AbstractQuest implements Quest {

    private final int id;
    private final QuestTemplate template;
    private final QuestHolder holder;
    private final List<PlayerObjective> playerObjectives;
    private final Collection<TriggerFactory> startTrigger;
    private final Collection<TriggerFactory> completionTrigger;

    private Phase phase;
    private Timestamp startTime;
    private Timestamp completionTime;
    private Timestamp abortionTime;

    public AbstractQuest(int id, QuestTemplate template, QuestHolder holder) {

        this.id = id;
        this.template = template;
        this.holder = holder;
        this.playerObjectives = loadObjectives();
        this.startTrigger = template.getStartTrigger();
        this.completionTrigger = template.getCompletionTrigger();
    }

    protected abstract List<PlayerObjective> loadObjectives();

    @Override
    public String getListenerId() {

        return getTemplate().getListenerId() + "." + getId();
    }

    @Override
    public boolean processTrigger(Player player) {

        if (!getPlayer().equals(player)) {
            return false;
        }
        if (!isActive()) {
            return false;
        }
        Collection<Requirement<Player>> requirements = getTemplate().getRequirements();
        if (requirements.stream().allMatch(requirement -> requirement.test(player))) {
            unregisterListeners();
            registerListeners();
        }
        return true;

    }

    public void registerListeners() {
        if (isCompleted()) {
            setPhase(Phase.COMPLETE);
            // do not register anything if completed
            unregisterListeners();
            return;
        }
        if (hasCompletedAllObjectives()) {
            setPhase(Phase.OJECTIVES_COMPLETED);
            if (completionTrigger.isEmpty()) {
                // complete the quest
                complete();
                return;
            }
            // register the completion trigger
            completionTrigger.forEach(factory -> factory.registerListener(this));
        } else {
            setPhase(Phase.NOT_STARTED);
            // we need to register the objective trigger
            updateObjectiveListeners();
        }
    }

    public void unregisterListeners() {

        startTrigger.forEach(factory -> factory.unregisterListener(this));
        completionTrigger.forEach(factory -> factory.unregisterListener(this));
        getObjectives().forEach(PlayerObjective::unregisterListeners);
    }

    @Override
    public void updateObjectiveListeners() {

        if (isCompleted()) {
            setPhase(Phase.COMPLETE);
            unregisterListeners();
            return;
        }
        if (!isActive()) {
            setPhase(Phase.NOT_STARTED);
            // do not register objective listeners if the quest is not started
            return;
        }
        if (hasCompletedAllObjectives()) {
            setPhase(Phase.OJECTIVES_COMPLETED);
            unregisterListeners();
            registerListeners();
            return;
        }
        setPhase(Phase.IN_PROGRESS);
        for (PlayerObjective playerObjective : getUncompletedObjectives()) {
            if (!playerObjective.isCompleted()) {
                // lets register the listeners of our objectives
                playerObjective.updateListeners();
            } else {
                playerObjective.unregisterListeners();
            }
            // abort if we are dealing with ordered required objectives
            if (!playerObjective.getObjectiveTemplate().isOptional() && getTemplate().isOrdered()) {
                return;
            }
        }
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
    public boolean isActive() {

        return getStartTime() != null && !isCompleted();
    }

    @Override
    public boolean hasCompletedAllObjectives() {

        List<PlayerObjective> uncompletedObjectives = getUncompletedObjectives();
        boolean completed = uncompletedObjectives.isEmpty()
                || (getTemplate().getRequiredObjectiveAmount() > 0
                && getTemplate().getRequiredObjectiveAmount() <= uncompletedObjectives.size());
        if (!uncompletedObjectives.isEmpty() && !completed) {
            int optionalObjectives = 0;
            for (PlayerObjective objective : uncompletedObjectives) {
                if (objective.getObjectiveTemplate().isOptional()) optionalObjectives++;
            }
            if (optionalObjectives == uncompletedObjectives.size()) {
                completed = true;
            }
        }
        return completed;
    }

    @Override
    public List<PlayerObjective> getObjectives() {

        if (playerObjectives.size() > 1) {
            Collections.sort(playerObjectives);
        }
        return playerObjectives;
    }

    @Override
    public void onObjectCompletion(PlayerObjective objective) {

        save();
        ObjectiveCompletedEvent event = new ObjectiveCompletedEvent(objective);
        RaidCraft.callEvent(event);
        updateObjectiveListeners();
    }

    @Override
    public boolean start() {

        if (!isActive()) {
            setStartTime(new Timestamp(System.currentTimeMillis()));
            getHolder().addQuest(this);
            setPhase(Phase.IN_PROGRESS);
            save();
            QuestStartedEvent event = new QuestStartedEvent(this);
            RaidCraft.callEvent(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete() {

        if (!isActive() || isCompleted()) {
            return false;
        }
        // first unregister all listeners to avoid double completion
        unregisterListeners();

        QuestCompleteEvent event = new QuestCompleteEvent(this);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) return false;

        setCompletionTime(new Timestamp(System.currentTimeMillis()));
        setPhase(Phase.COMPLETE);
        save();
        // give rewards and execute completion actions
        getTemplate().getCompletionActions()
                .forEach(action -> action.accept(getPlayer()));

        getHolder().removeQuest(this);

        QuestCompletedEvent questCompletedEvent = new QuestCompletedEvent(this);
        RaidCraft.callEvent(questCompletedEvent);
        return true;
    }

    @Override
    public boolean abort() {

        // first unregister all listeners (includes complete listeners)
        unregisterListeners();
        getObjectives().forEach(PlayerObjective::abort);
        // we need to remove all requirements tracked by the quest
        getTemplate().getRequirements().forEach(playerRequirement -> playerRequirement.delete(getPlayer()));
        setAbortionTime(Timestamp.from(Instant.now()));
        setPhase(Phase.ABORTED);
        save();
        RaidCraft.callEvent(new QuestAbortedEvent(this));
        return true;
    }
}
