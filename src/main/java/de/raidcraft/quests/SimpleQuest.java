package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.player.PlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.AbstractQuest;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SimpleQuest extends AbstractQuest {

    protected SimpleQuest(TPlayerQuest quest, QuestTemplate template, QuestHolder holder) {

        super(quest.getId(), template, holder);

        setPhase(quest.getPhase());
        setStartTime(quest.getStartTime());
        setCompletionTime(quest.getCompletionTime());
    }

    @Override
    protected List<PlayerObjective> loadObjectives() {

        List<PlayerObjective> objectives = new ArrayList<>();
        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        for (Objective objective : getTemplate().getObjectives()) {
            TPlayerObjective entry = database.find(TPlayerObjective.class).where()
                    .eq("quest_id", getId())
                    .eq("objective_id", objective.getId()).findUnique();
            // create a new db entry if none exists
            if (entry == null) {
                entry = new TPlayerObjective();
                entry.setObjectiveId(objective.getId());
                entry.setQuest(database.find(TPlayerQuest.class, getId()));
                database.save(entry);
            }
            objectives.add(new SimplePlayerObjective(entry, this, objective));
        }
        return objectives;
    }

    @Override
    public void delete() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest quest = database.find(TPlayerQuest.class, getId());
        if (quest != null) {
            database.delete(quest);
            if(isCompleted()) {
                TPlayer tPlayer = database.find(TPlayer.class).where().eq("player", getPlayer().getName()).findUnique();
                if(tPlayer != null && tPlayer.getCompletedQuests() > 0) {
                    tPlayer.setCompletedQuests(tPlayer.getCompletedQuests() - 1);
                    database.save(tPlayer);
                }
            }
        }
    }

    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest quest = database.find(TPlayerQuest.class, getId());
        if (quest == null) return;
        quest.setStartTime(getStartTime());
        quest.setCompletionTime(getCompletionTime());
        database.save(quest);
        // also save all quest objectives
        for (PlayerObjective objective : getPlayerObjectives()) {
            objective.save();
        }
        // and actions
        for (Action<QuestTemplate> action : getTemplate().getCompleteActions()) {
            action.save();
        }
    }
}
