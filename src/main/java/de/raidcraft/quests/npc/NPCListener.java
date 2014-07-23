package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.QuestHost;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.rcconversations.api.conversation.RCConservationFindNPCConversationHost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 16.03.13 - 02:19
 */
public class NPCListener implements Listener {

    @EventHandler
    private void npcConservations(RCConservationFindNPCConversationHost event) {

        if (!event.getNPC().hasTrait(QuestTrait.class)) {
            return;
        }
        try {
            QuestHost questHost = Quests.getQuestHost(event.getNPC().getTrait(QuestTrait.class).getHostId());
            QuestNPCHost questNpcHost = (QuestNPCHost) questHost;
            event.setHost(questNpcHost);
        } catch (InvalidQuestHostException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}