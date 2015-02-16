package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.host.QuestHost;
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

    // TODO: hotfix wg 6.0 that you can rightclick
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onNpcInteract(NPCRightClickEvent event) {

        if (!event.getNPC().hasTrait(QuestTrait.class)) {
            return;
        }
        QuestHost questHost = event.getNPC().getTrait(QuestTrait.class).getQuestHost();
        if (questHost == null) {
            return;
        }
        questHost.setConversation(event.getClicker(), questHost.getDefaultConversationName());
        questHost.interact(event.getClicker());
        // conversation is changed by trigger
        // fire the default conversation if set
        RaidCraft.getComponent(ConversationManager.class).triggerConversation(questHost, event.getClicker());
        // TODO: missing?
        event.setCancelled(true);
    }
}
