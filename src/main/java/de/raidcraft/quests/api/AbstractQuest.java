package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public abstract class AbstractQuest implements Quest {

    private final int id;
    private final QuestTemplate template;
    private final QuestHolder holder;

    private Timestamp startTime;
    private Timestamp completionTime;

    public AbstractQuest(int id, QuestTemplate template, QuestHolder holder) {

        this.id = id;
        this.template = template;
        this.holder = holder;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return getTemplate().getName();
    }

    @Override
    public String getFullName() {

        return getTemplate().getId();
    }

    @Override
    public String getFriendlyName() {

        return getTemplate().getFriendlyName();
    }

    @Override
    public String getDescription() {

        return getTemplate().getDescription();
    }

    @Override
    public QuestTemplate getTemplate() {

        return template;
    }

    @Override
    public QuestHolder getHolder() {

        return holder;
    }

    @Override
    public Player getPlayer() {

        return getHolder().getPlayer();
    }

    @Override
    public boolean isCompleted() {

        return completionTime != null;
    }

    @Override
    public boolean isActive() {

        return startTime != null && !isCompleted();
    }

    @Override
    public Timestamp getStartTime() {

        return startTime;
    }

    protected void setStartTime(Timestamp startTime) {

        this.startTime = startTime;
    }

    @Override
    public Timestamp getCompletionTime() {

        return completionTime;
    }

    protected void setCompletionTime(Timestamp completionTime) {

        this.completionTime = completionTime;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractQuest)) return false;

        AbstractQuest that = (AbstractQuest) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }

    @Override
    public String toString() {

        return getTemplate().toString() + "." + getHolder().toString();
    }
}
