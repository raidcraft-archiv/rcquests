package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestHost;
import de.raidcraft.rcconversations.conversations.ConversationManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 16.03.13 - 02:19
 */
public class NPCListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onNpcInteract(NPCRightClickEvent event) {

        if (event.getNPC().hasTrait(QuestTrait.class)) {
            QuestHost questHost = event.getNPC().getTrait(QuestTrait.class).getQuestHost();
            if (questHost != null) {
                questHost.setConversation(event.getClicker(), questHost.getDefaultConversationName());
                questHost.interact(event.getClicker());
                // fire the default conversation if set
                RaidCraft.getComponent(ConversationManager.class).triggerConversation(questHost, event.getClicker());
            }
        }
    }
}