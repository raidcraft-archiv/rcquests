package de.raidcraft.quests.npc;

import de.raidcraft.api.conversations.RCConversationHostInteractEvent;
import de.raidcraft.api.quests.QuestHost;
import de.raidcraft.rcconversations.host.NPCHost;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 16.03.13 - 02:19
 */
public class NPCListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onNpcInteract(NPCRightClickEvent event) {

        if (event.getNPC().hasTrait(QuestTrait.class)) {
            QuestHost questHost = event.getNPC().getTrait(QuestTrait.class).getQuestHost();
            if (questHost != null) {
                questHost.interact(event.getClicker());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onConversationHostInteractEvent(RCConversationHostInteractEvent event) {

        if (event.getHost() instanceof NPCHost) {
            NPC npc = ((NPCHost) event.getHost()).getNPC();
            if (npc.hasTrait(QuestTrait.class)) {
                event.setCancelled(true);
            }
        }
    }
}