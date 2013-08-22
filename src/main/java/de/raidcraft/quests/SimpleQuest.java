package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractQuest;
import de.raidcraft.quests.api.Action;
import de.raidcraft.quests.api.Objective;
import de.raidcraft.quests.api.PlayerObjective;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.quests.api.Requirement;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SimpleQuest extends AbstractQuest {

    private final List<PlayerObjective> playerObjectives = new ArrayList<>();
    private final List<PlayerObjective> uncompletedObjectives;
    private boolean meetsRequirements = false;

    protected SimpleQuest(QuestTemplate template, QuestHolder holder) {

        super(template, holder);
        for (Objective objective : template.getObjectives()) {
            playerObjectives.add(new SimplePlayerObjective(this, objective));
        }
        // TODO: filter out completed objectives that are stored in the database
        uncompletedObjectives = playerObjectives;
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
                action.execute(getPlayer(), getTemplate());
            }
        }
    }

    @Override
    public void start() {

        setStartTime(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void trigger(Player player) {

        if (!getPlayer().equals(player) || !isActive()) {
            return;
        }
        if (!meetsRequirements && getTemplate().getRequirements().length > 0) {
            meetsRequirements = true;
            for (Requirement requirement : getTemplate().getRequirements()) {
                if (!requirement.isMet(player)) {
                    meetsRequirements = false;
                    break;
                }
            }
        }
        // dont trigger objectives when no requirements are met
        if (meetsRequirements) {
            for (PlayerObjective playerObjective : getUncompletedObjectives()) {
                playerObjective.trigger(player);
            }
        }
    }
}
