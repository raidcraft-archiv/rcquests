package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.AbstractQuest;
import de.raidcraft.quests.api.Action;
import de.raidcraft.quests.api.Objective;
import de.raidcraft.quests.api.PlayerObjective;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.quests.api.Requirement;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SimpleQuest extends AbstractQuest {

    private final List<PlayerObjective> playerObjectives = new ArrayList<>();
    private final List<PlayerObjective> uncompletedObjectives = new ArrayList<>();

    protected SimpleQuest(int id, QuestTemplate template, QuestHolder holder) {

        super(id, template, holder);

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
        if (uncompletedObjectives.isEmpty()) {
            // complete the quest and trigger the complete actions
            setCompletionTime(new Timestamp(System.currentTimeMillis()));
            // give rewards and execute completion actions
            for (Action<QuestTemplate> action : getTemplate().getActions()) {
                try {
                    action.execute(getPlayer(), getTemplate());
                } catch (QuestException e) {
                    getPlayer().sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
    }

    @Override
    public void trigger(Player player) {

        if (!getPlayer().equals(player) || !isActive() || isCompleted()) {
            return;
        }
        boolean meetsRequirements = false;
        if (getTemplate().getRequirements().length > 0) {
            try {
                for (Requirement requirement : getTemplate().getRequirements()) {
                    if (!requirement.isMet(player)) {
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
                playerObjective.trigger(player);
            }
        }
    }

    @Override
    public void start() {

        setStartTime(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void abort() {

        setStartTime(null);
    }

    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest quest = database.find(TPlayerQuest.class, getId());
        quest.setStartTime(getStartTime());
        quest.setCompletionTime(getCompletionTime());
        database.save(quest);
        // also save all quest objectives
        for (PlayerObjective objective : getPlayerObjectives()) {
            objective.save();
        }
    }
}
