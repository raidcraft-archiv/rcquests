package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public abstract class AbstractPlayerObjective implements PlayerObjective {

    private final Quest quest;
    private final Objective objective;
    private boolean completed = false;

    public AbstractPlayerObjective(Quest quest, Objective objective) {

        this.quest = quest;
        this.objective = objective;
    }

    @Override
    public Quest getQuest() {

        return quest;
    }

    @Override
    public Objective getObjective() {

        return objective;
    }

    @Override
    public Player getPlayer() {

        return quest.getPlayer();
    }

    @Override
    public boolean isCompleted() {

        return completed;
    }

    protected void setCompleted(boolean completed) {

        this.completed = completed;
    }
}
