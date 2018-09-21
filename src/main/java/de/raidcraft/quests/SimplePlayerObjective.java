package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.objective.AbstractPlayerObjective;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.tables.TPlayerObjective;
import io.ebean.EbeanServer;

/**
 * @author Silthus
 */
public class SimplePlayerObjective extends AbstractPlayerObjective {

    public SimplePlayerObjective(TPlayerObjective tableEntry, Quest quest, ObjectiveTemplate objectiveTemplate) {

        super(tableEntry.getId(), quest, objectiveTemplate);
        setCompletionTime(tableEntry.getCompletionTime());
        setAbortionTime(tableEntry.getAbortionTime());
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerObjective objective = database.find(TPlayerObjective.class, getId());
        objective.setCompletionTime(getCompletionTime());
        objective.setAbortionTime(getAbortionTime());
        database.save(objective);
    }
}
