package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public abstract class AbstractQuest implements Quest {

    private final QuestTemplate template;
    private final QuestHolder holder;

    private Timestamp startTime;
    private Timestamp completionTime;

    public AbstractQuest(QuestTemplate template, QuestHolder holder) {

        this.template = template;
        this.holder = holder;
    }

    @Override
    public String getId() {

        return getTemplate().getId();
    }

    @Override
    public String getName() {

        return getTemplate().getName();
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
}
