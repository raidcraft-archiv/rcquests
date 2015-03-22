package de.raidcraft.quests.api.objective;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.language.Translator;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.events.ObjectiveCompleteEvent;
import de.raidcraft.quests.api.events.ObjectiveStartedEvent;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
@Data
@EqualsAndHashCode(of = {"id", "quest", "objectiveTemplate"})
public abstract class AbstractPlayerObjective implements PlayerObjective {

    private final int id;
    private final Quest quest;
    private final ObjectiveTemplate objectiveTemplate;
    private boolean active = false;
    private Timestamp completionTime;

    public AbstractPlayerObjective(int id, Quest quest, ObjectiveTemplate objectiveTemplate) {

        this.id = id;
        this.quest = quest;
        this.objectiveTemplate = objectiveTemplate;
    }

    @Override
    public String getListenerId() {

        return getQuest().getListenerId() + "." + getId();
    }

    @Override
    public boolean processTrigger(Player player) {

        if (!player.equals(getQuest().getHolder().getPlayer())) {
            return false;
        }
        if (getObjectiveTemplate().isAutoCompleting() &&
                getObjectiveTemplate().getRequirements().stream()
                .allMatch(requirement -> requirement.test(player))) {
            complete();
        }
        return true;
    }

    public void updateListeners() {

        if (!isCompleted()) {
            if (!isActive()) {
                // register our start trigger
                getObjectiveTemplate().getTrigger().forEach(factory -> factory.registerListener(this));
                setActive(true);
                Translator.msg(QuestPlugin.class, getQuestHolder().getPlayer(),
                        "objective.start", "%s: %s!",
                        getQuest().getFriendlyName(),
                        getObjectiveTemplate().getFriendlyName());
                ObjectiveStartedEvent event = new ObjectiveStartedEvent(this);
                RaidCraft.callEvent(event);
            }
        } else {
            unregisterListeners();
        }
    }

    public void unregisterListeners() {

        getObjectiveTemplate().getTrigger().forEach(factory -> factory.unregisterListener(this));
        setActive(false);
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
    public void complete() {

        if (isCompleted()) return;
        ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(this);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) return;
        unregisterListeners();
        this.completionTime = new Timestamp(System.currentTimeMillis());
        getQuest().onObjectCompletion(this);
    }

    @Override
    public int compareTo(@NonNull PlayerObjective o) {

        return this.getObjectiveTemplate().compareTo(o.getObjectiveTemplate());
    }
}
