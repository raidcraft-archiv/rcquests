package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.QuestHost;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.util.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Philip
 */
public class QuestSignListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // TODO: state is slow ... use CustomSign event?
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (!SignUtil.isLineEqual(sign.getLine(0), QuestSignHost.QUEST_SIGN_IDENTIFIER)) {
            return;
        }

        String hostId = ChatColor.stripColor(sign.getLine(3));

        try {
            QuestHost questHost = Quests.getQuestHost(hostId);
            if (questHost != null && questHost instanceof QuestSignHost) {
                QuestSignHost questSignHost = (QuestSignHost) questHost;
                questSignHost.interact(event.getPlayer());
                RaidCraft.getComponent(RCConversationsPlugin.class).getConversationManager().triggerConversation(questSignHost, event.getPlayer());
            }
        } catch (InvalidQuestHostException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {

        if (!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase(QuestSignHost.QUEST_SIGN_IDENTIFIER)) {
            return;
        }

        if (!event.getPlayer().hasPermission("rcconversations.sign.build")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to create this sign!");
            event.setCancelled(true);
            return;
        }
    }
}
