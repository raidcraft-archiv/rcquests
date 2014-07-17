package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.ConversationHost;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.QuestHost;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.QuestPlugin;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 16.03.13 - 02:19
 */
public class NPCListener implements Listener {

    private QuestPlugin plugin;

    public NPCListener(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(NPCRightClickEvent event) {
        ConversationHost host = getClickedHost(event);
        if (host == null) return;
//        plugin.get().triggerConversation(host, event.getClicker());
    }

    // TODO: check perfromance
    // TODO: how to trigger quest actions?
    private ConversationHost getClickedHost(NPCClickEvent event) {
        if (!event.getNPC().hasTrait(QuestTrait.class)) {
            return null;
        }

        if (event.getClicker().getLocation().distance(event.getNPC().getEntity().getLocation()) > 4) {
            return null;
        }
        ConversationHost host = null;
        try {
            QuestHost questHost = Quests.getQuestHost(event.getNPC().getTrait(QuestTrait.class).getHostId());
            if (questHost != null && questHost instanceof QuestNPCHost) {
                QuestNPCHost questNpcHost = (QuestNPCHost) questHost;
                questNpcHost.interact(event.getClicker());
                host = questNpcHost;
            }
        } catch (InvalidQuestHostException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return host;
    }
}