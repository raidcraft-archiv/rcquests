package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractQuest;
import de.raidcraft.quests.api.Objective;
import de.raidcraft.quests.api.PlayerObjective;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;
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
    public void start() {

        setStartTime(new Timestamp(System.currentTimeMillis()));
        // TODO
    }

    @Override
    public void trigger(Player player) {

        if (!getPlayer().equals(player)) {
            return;
        }
        for (PlayerObjective playerObjective : getUncompletedObjectives()) {
            playerObjective.trigger(player);
        }
        // TODO: check requirements
    }
}
