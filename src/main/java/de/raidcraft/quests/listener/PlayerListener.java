package de.raidcraft.quests.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.events.ObjectiveCompletedEvent;
import de.raidcraft.quests.api.events.ObjectiveStartedEvent;
import de.raidcraft.quests.api.events.QuestCompleteEvent;
import de.raidcraft.quests.api.events.QuestStartedEvent;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.quests.util.QuestUtil;
import de.raidcraft.util.CustomItemUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

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
                FancyMessage msg = new FancyMessage("Du hast das Quest Item ").color(ChatColor.DARK_AQUA);
                msg = CustomItemUtil.getFormattedItemTooltip(msg, customItem);
                msg.then(" aufgenommen.")
                        .color(ChatColor.DARK_AQUA)
                        .then(" ")
                        .then("Klicke hier um dein Quest Inventar zu öffnen.")
                        .color(ChatColor.AQUA)
                        .command("/qi").send(event.getPlayer());
                event.getItem().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onObjectiveStarted(ObjectiveStartedEvent event) {

        FancyMessage msg = QuestUtil.getQuestTooltip(new FancyMessage(""), event.getObjective().getQuest());
        msg.then(": ").color(ChatColor.YELLOW)
                .then(event.getObjective().getObjectiveTemplate().getFriendlyName()).color(ChatColor.DARK_AQUA)
                .tooltip(event.getObjective().getObjectiveTemplate().getDescription().split("|"))
                .send(event.getObjective().getQuest().getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onObjectiveComplete(ObjectiveCompletedEvent event) {

        PlayerObjective objective = event.getObjective();
        FancyMessage msg = QuestUtil.getQuestTooltip(new FancyMessage(""), event.getObjective().getQuest());
        msg.then(": ").color(ChatColor.YELLOW)
                .then("Aufgabe ").color(ChatColor.GREEN)
                .then(objective.getObjectiveTemplate().getFriendlyName()).color(ChatColor.DARK_AQUA)
                .tooltip(objective.getObjectiveTemplate().getDescription().split("|"))
                .then(" abgeschlossen").color(ChatColor.GREEN)
                .send(event.getObjective().getQuest().getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestStart(QuestStartedEvent event) {

        FancyMessage msg = new FancyMessage("Quest ").color(ChatColor.YELLOW);
        msg = QuestUtil.getQuestTooltip(msg, event.getQuest());
        msg.then(" angenommen.").color(ChatColor.YELLOW)
                .send(event.getQuest().getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestComplete(QuestCompleteEvent event) {

        FancyMessage msg = new FancyMessage(event.getQuest().getPlayer().getName()).color(ChatColor.AQUA)
                .then(" hat die Quest ").color(ChatColor.YELLOW);
        msg = QuestUtil.getQuestTooltip(msg, event.getQuest());
        msg.then(" abgeschlossen.").color(ChatColor.YELLOW)
                .send(Arrays.asList(Bukkit.getOnlinePlayers()));
    }
}
