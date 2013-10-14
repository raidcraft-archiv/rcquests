package de.raidcraft.quests.api.quest.trigger;

import de.raidcraft.quests.api.player.QuestHolder;

/**
 * @author Silthus
 */
public interface TriggerListener {

    public void trigger(QuestHolder player);
}
