package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.PlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.AbstractQuest;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import org.bukkit.ChatColor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
public class SimpleQuest extends AbstractQuest {

    private final List<PlayerObjective> playerObjectives = new ArrayList<>();
    private final List<PlayerObjective> uncompletedObjectives = new ArrayList<>();

    protected SimpleQuest(TPlayerQuest quest, QuestTemplate template, QuestHolder holder) {

        super(quest.getId(), template, holder);

        setStartTime(quest.getStartTime());
        setCompletionTime(quest.getCompletionTime());

        for (Objective objective : template.getObjectives()) {
            EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
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
            playerObjectives.add(new SimplePlayerObjective(entry.getId(), this, objective));
        }
        for (PlayerObjective objective : playerObjectives) {
            if (!objective.isCompleted()) {
                uncompletedObjectives.add(objective);
            }
        }
        // we also need to sort the uncompleted objectives if the quest is ordered
        if (getTemplate().isOrdered()) {
            Collections.sort(uncompletedObjectives);
        }
    }

    @Override
    public boolean hasCompletedAllObjectives() {

        return uncompletedObjectives.isEmpty()
                || (getTemplate().getRequiredObjectiveAmount() > 0
                && getTemplate().getRequiredObjectiveAmount() <= getUncompletedObjectives().size());
    }


    @Override
    public List<PlayerObjective> getPlayerObjectives() {

        return playerObjectives;
    }

    @Override
    public List<PlayerObjective> getUncompletedObjectives() {

        return uncompletedObjectives;
    }

    @Override
    public void completeObjective(PlayerObjective objective) {

        uncompletedObjectives.remove(objective);
    }

    @Override
    public void trigger(QuestHolder questHolder) {

        if (!getPlayer().equals(questHolder) || !isActive() || isCompleted()) {
            return;
        }
        boolean meetsRequirements = false;
        if (getTemplate().getRequirements().length > 0) {
            try {
                for (Requirement requirement : getTemplate().getRequirements()) {
                    if (!requirement.isMet(questHolder.getPlayer())) {
                        meetsRequirements = false;
                        break;
                    }
                }
            } catch (QuestException e) {
                meetsRequirements = false;
                getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            meetsRequirements = true;
        }
        // dont trigger objectives when no requirements are met
        if (meetsRequirements) {
            for (PlayerObjective playerObjective : getUncompletedObjectives()) {
                playerObjective.trigger(questHolder);
                // abort the loop if we are dealing with ordered required objectives
                if (!playerObjective.getObjective().isOptional() && getTemplate().isOrdered()) {
                    break;
                }
            }
        }
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

        if (!isActive() || !hasCompletedAllObjectives()) {
            return;
        }
        // complete the quest and trigger the complete actions
        setCompletionTime(new Timestamp(System.currentTimeMillis()));
        // give rewards and execute completion actions
        for (Action<QuestTemplate> action : getTemplate().getCompleteActions()) {
            try {
                action.execute(getHolder(), getTemplate());
            } catch (QuestException e) {
                getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }

    @Override
    public void abort() {

        setStartTime(null);
        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest quest = database.find(TPlayerQuest.class, getId());
        database.delete(quest);
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
