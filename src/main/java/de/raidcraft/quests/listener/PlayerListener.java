package de.raidcraft.quests.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.util.CustomItemUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Silthus
 */
public class PlayerListener implements Listener {

    private final QuestPlugin plugin;

    public PlayerListener(QuestPlugin plugin) {

        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        plugin.getQuestManager().getQuestHolder(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        QuestHolder holder = plugin.getQuestManager().clearPlayerCache(event.getPlayer().getUniqueId());
        if (holder != null) {
            holder.save();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerPickupItemEvent event) {

        if (RaidCraft.isCustomItem(event.getItem().getItemStack())) {
            CustomItemStack customItem = RaidCraft.getCustomItem(event.getItem().getItemStack());
            if (customItem.getItem().getType() == ItemType.QUEST) {
                plugin.getQuestManager().getQuestHolder(event.getPlayer()).getQuestInventory().addItem(customItem);
                FancyMessage msg = new FancyMessage("Du hast das Quest Item ").color(ChatColor.GRAY);
                msg = CustomItemUtil.getFormattedItemTooltip(msg, customItem);
                msg.then(" aufgenommen.")
                        .color(ChatColor.DARK_AQUA)
                        .then(" ")
                        .then("Quest Inventar öffnen.")
                        .style(ChatColor.UNDERLINE)
                        .color(ChatColor.AQUA)
                        .command("/qi").send(event.getPlayer());
                event.getItem().remove();
                event.setCancelled(true);
            }
        }
    }
}
