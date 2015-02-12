package de.raidcraft.quests.api.impl;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.quests.api.PlayerObjective;
import de.raidcraft.quests.api.Quest;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
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

    public AbstractQuest(int id, QuestTemplate template, QuestHolder holder) {

        this.id = id;
        this.template = template;
        this.holder = holder;
        this.playerObjectives = loadObjectives();
        this.startTrigger = template.getStartTrigger();
        this.completionTrigger = template.getCompletionTrigger();
        registerListeners();
    }

    protected abstract List<PlayerObjective> loadObjectives();

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
            // do not register anything if completed
            return;
        }
        if (hasCompletedAllObjectives()) {
            if (completionTrigger.isEmpty()) {
                // complete the quest
                complete();
                return;
            }
            // register the completion trigger
            completionTrigger.forEach(factory -> factory.registerListener(this));
        } else {
            // we need to register the objective trigger
            updateObjectiveListeners();
        }
    }

    public void unregisterListeners() {

        startTrigger.forEach(factory -> factory.unregisterListener(this));
        completionTrigger.forEach(factory -> factory.unregisterListener(this));
    }

    @Override
    public void updateObjectiveListeners() {

        if (hasCompletedAllObjectives()) {
            unregisterListeners();
            registerListeners();
            return;
        }
        for (PlayerObjective playerObjective : getUncompletedObjectives()) {
            if (!playerObjective.isCompleted()) {
                // lets register the listeners of our objectives
                playerObjective.registerListeners();
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
    public List<PlayerObjective> getPlayerObjectives() {

        Collections.sort(playerObjectives);
        return playerObjectives;
    }

    @Override
    public void onObjectCompletion(PlayerObjective objective) {

        updateObjectiveListeners();
        getHolder().getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + getTemplate().getFriendlyName() + ": " + ChatColor.RESET
                ChatColor.AQUA + "Aufgabe " + ChatColor.GREEN + ChatColor.ITALIC + objective.getObjectiveTemplate().getFriendlyName() 
                + ChatColor.RESET + ChatColor.AQUA + " abgeschlossen.");
    }

    @Override
    public void start() {

        if (!isActive()) {
            setStartTime(new Timestamp(System.currentTimeMillis()));
            save();
        }
        getHolder().getPlayer().sendMessage(ChatColor.YELLOW + "Quest angenommen: " + ChatColor.GREEN + getFriendlyName());
    }

    @Override
    public void complete() {

        if (!isActive() || !hasCompletedAllObjectives() || isCompleted()) {
            return;
        }
        // first unregister all listeners to avoid double completion
        unregisterListeners();
        
        // complete the quest and trigger the complete actions
        setCompletionTime(new Timestamp(System.currentTimeMillis()));
        // give rewards and execute completion actions
        getTemplate().getCompletionActions()
                .forEach(action -> action.accept(getPlayer()));
                
        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + getHolder().getPlayer().getName() + " hat die Quest '" +
                ChatColor.GOLD + getFriendlyName() + ChatColor.DARK_GREEN + "' abgeschlossen!");
    }

    @Override
    public void abort() {

        setStartTime(null);
        // first unregister all listeners (includes complete listeners)
        unregisterListeners();
        // and then we reregister our listeners because the player should be able to reaccept the quest
        registerListeners();
    }
}
