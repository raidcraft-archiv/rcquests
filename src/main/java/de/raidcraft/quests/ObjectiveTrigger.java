package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.player.PlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.objective.Objective;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class ObjectiveTrigger extends SimpleTrigger {

    Objective objective;

    public ObjectiveTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data, Type type) {

        super(id, questTemplate, data, type);
    }

    public void setObjective(Objective objective) {

        this.objective = objective;
    }

    @Override
    protected void execute(QuestHolder holder) {

        if(!holder.hasActiveQuest(getQuestTemplate().getId())) return;

        boolean found = false;
        RaidCraft.LOGGER.info("DEBUG -1: " + objective.getId());
        for(PlayerObjective playerObjective : holder.getQuest(getQuestTemplate()).getUncompletedObjectives()) {
            int objId = playerObjective.getObjective().getId();
            RaidCraft.LOGGER.info("DEBUG 0: " + playerObjective.getObjective().getFriendlyName() + " -> " + objId);
            if(objId == objective.getId()) {
                found = true;
                RaidCraft.LOGGER.info("DEBUG 1: " + objId);
            }
            // abort if we are dealing with ordered required objectives
            if (!playerObjective.getObjective().isOptional() && getQuestTemplate().isOrdered()) {
                RaidCraft.LOGGER.info("DEBUG 2: " + playerObjective.getObjective().getFriendlyName());
                break;
            }
        }
        if(!found) return;

        super.execute(holder);
    }
}
