package de.raidcraft.quests.api.quest;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerListener;
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
import java.util.Optional;

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
    private final Collection<TriggerFactory> activeTrigger;
    private final TriggerListener<Player> activeTriggerListener;
    private final Collection<TriggerFactory> completionTrigger;
    private final TriggerListener<Player> completionTriggerListener;

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
        this.activeTrigger = template.getActiveTrigger();
        this.activeTriggerListener = new TriggerListener<Player>() {
            @Override
            public Class<Player> getTriggerEntityType() {

                return Player.class;
            }

            @Override
            public boolean processTrigger(Player entity) {

                return isActive();
            }
        };
        this.completionTrigger = template.getCompletionTrigger();
        this.completionTriggerListener = new TriggerListener<Player>() {
            @Override
            public Class<Player> getTriggerEntityType() {

                return Player.class;
            }

            @Override
            public boolean processTrigger(Player entity) {

                return hasCompletedAllObjectives() && !isCompleted() && isActive();
            }
        };
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
            completionTrigger.forEach(factory -> factory.registerListener(completionTriggerListener));
        } else {
            setPhase(Phase.NOT_STARTED);
            // we need to register the objective trigger
            updateObjectiveListeners();
        }
    }

    public void unregisterListeners() {

        startTrigger.forEach(factory -> factory.unregisterListener(this));
        activeTrigger.forEach(factory -> factory.unregisterListener(activeTriggerListener));
        completionTrigger.forEach(factory -> factory.unregisterListener(completionTriggerListener));
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

        getActiveTrigger().forEach(trigger -> trigger.registerListener(activeTriggerListener));

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
    public boolean isObjectiveCompleted(int id) {

        Optional<PlayerObjective> objective = getObjectives().stream()
                .filter(playerObjective -> playerObjective.getObjectiveTemplate().getId() == id)
                .findAny();
        return objective.isPresent() && objective.get().isCompleted();
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
            getTemplate().getStartActions().forEach(playerAction -> playerAction.accept(getPlayer()));
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
