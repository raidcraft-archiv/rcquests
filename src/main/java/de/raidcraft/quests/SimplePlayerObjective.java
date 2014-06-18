package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.AbstractPlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerRequirementCount;
import org.bukkit.ChatColor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class SimplePlayerObjective extends AbstractPlayerObjective {

    private Map<Integer, Integer> playerCount = new HashMap<>();

    public SimplePlayerObjective(TPlayerObjective tableEntry, Quest quest, Objective objective) {

        super(tableEntry.getId(), quest, objective);
        setCompleted(tableEntry.getCompletionTime());
        // lets load up all requirement counts
        for (TPlayerRequirementCount count : tableEntry.getRequirementCounts()) {
            playerCount.put(count.getRequirementId(), count.getCount());
        }
    }

    @Override
    public void trigger(QuestHolder questHolder) {

        if (!getQuestHolder().equals(questHolder) || isCompleted()) {
            return;
        }
        boolean complete = true;
        try {
            for (Requirement requirement : getObjective().getRequirements()) {
                if (requirement.getRequiredCount() > 0) {
                    if (!playerCount.containsKey(requirement.getId())) {
                        playerCount.put(requirement.getId(), 0);
                    }
                    int currentCount = playerCount.get(requirement.getId()) + 1;
                    playerCount.put(requirement.getId(), currentCount);
                    if (currentCount < requirement.getRequiredCount()) {
                        String countText = requirement.getCountText(currentCount);
                        if (countText != null && !countText.equals("")) {
                            questHolder.getPlayer().sendMessage(countText);
                        }
                        complete = false;
                        break;
                    }
                } else {
                    RaidCraft.LOGGER.info("[Quest] Check Requirement '" + requirement.getType() + "'");
                    if (!requirement.isMet(questHolder.getPlayer())) {
                        RaidCraft.LOGGER.info("[Quest] Requirement isn't met!");
                        complete = false;
                        break;
                    }
                }
            }
        } catch (QuestException e) {
            questHolder.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
        if (complete) {
            RaidCraft.LOGGER.info("[Quest] Set objective as completed: '" + getObjective().getFriendlyName() + "'");
            setCompleted(new Timestamp(System.currentTimeMillis()));
            getQuest().onObjectCompletion(this);
        }
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerObjective objective = database.find(TPlayerObjective.class, getId());
        objective.setCompletionTime(getCompletionTime());
        database.save(objective);
        // save the counted requirements
        for (Requirement requirement : getObjective().getRequirements()) {
            if (requirement.getRequiredCount() > 0 && playerCount.containsKey(requirement.getId())) {
                TPlayerRequirementCount count = database.find(TPlayerRequirementCount.class).where()
                        .eq("requirement_id", requirement.getId())
                        .eq("objective_id", getId()).findUnique();
                if (count == null) {
                    count = new TPlayerRequirementCount();
                    count.setObjective(objective);
                    count.setRequirementId(requirement.getId());
                }
                count.setCount(playerCount.get(requirement.getId()));
                database.save(count);
            }
        }
        // save actions
        for (Action<Objective> action : getObjective().getActions()) {
            action.save();
        }
    }
}
