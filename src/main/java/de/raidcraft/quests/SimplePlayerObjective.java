package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractPlayerObjective;
import de.raidcraft.quests.api.Objective;
import de.raidcraft.quests.api.Quest;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimplePlayerObjective extends AbstractPlayerObjective {

    public SimplePlayerObjective(Quest quest, Objective objective) {

        super(quest, objective);
    }

    @Override
    public void trigger(Player player) {

        if (!getPlayer().equals(player)) {
            return;
        }
        // TODO: check requirements
    }
}
