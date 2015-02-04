package de.raidcraft.quests.api.objective;

import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.holder.QuestHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private Timestamp completionTime;

    public AbstractPlayerObjective(int id, Quest quest, ObjectiveTemplate objectiveTemplate) {

        this.id = id;
        this.quest = quest;
        this.objectiveTemplate = objectiveTemplate;
        registerListeners();
    }

    @Override
    public boolean processTrigger(Player player) {

        if (!player.equals(getQuest().getHolder().getPlayer())) {
            return false;
        }
        if (getObjectiveTemplate().getRequirements().stream()
                .allMatch(requirement -> requirement.test(player))) {
            complete();
        }
        return true;
    }

    public void registerListeners() {

        if (!isCompleted()) {
            // register our start trigger
            getObjectiveTemplate().getTrigger().forEach(factory -> factory.registerListener(this));
        } else {
            unregisterListeners();
            // pass back the listener registration to the quest
            getQuest().updateObjectiveListeners();
        }
    }

    public void unregisterListeners() {

        getObjectiveTemplate().getTrigger().forEach(factory -> factory.unregisterListener(this));
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

        this.completionTime = new Timestamp(System.currentTimeMillis());
        unregisterListeners();
        getQuest().onObjectCompletion(this);
    }

    @Override
    public int compareTo(PlayerObjective o) {

        return this.getObjectiveTemplate().compareTo(o.getObjectiveTemplate());
    }
}
