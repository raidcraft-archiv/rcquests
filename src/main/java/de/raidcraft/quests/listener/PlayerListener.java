package de.raidcraft.quests.listener;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.QuestPool;
import de.raidcraft.quests.api.events.*;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.quests.api.objective.PlayerTask;
import de.raidcraft.quests.util.QuestUtil;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.fanciful.FancyMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.Optional;

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
                msg.then(" in dein ").color(ChatColor.DARK_AQUA)
                        .then("Quest Inventar")
                        .color(ChatColor.AQUA)
                        .tooltip("Klicke hier um dein Quest Inventar zu öffnen.",
                                "Oder gebe /qi ein.")
                        .command("/qi")
                        .then(" aufgenommen.").color(ChatColor.DARK_AQUA)
                        .send(event.getPlayer());
                event.getItem().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onObjectiveStarted(ObjectiveStartedEvent event) {

        if (event.getObjective().getObjectiveTemplate().isSilent()) return;
        if (event.getObjective().getQuest().getObjectives().get(0).equals(event.getObjective())) return;

        FancyMessage message = new FancyMessage("[").color(ChatColor.DARK_GRAY)
                .text(event.getObjective().getQuest().getFriendlyName()).color(ChatColor.GREEN)
                .formattedTooltip(QuestUtil.getQuestTooltip(event.getObjective().getQuest()))
                .text("]").color(ChatColor.DARK_GRAY)
                .text(": ").color(ChatColor.YELLOW)
                .text("Aufgabe ").color(ChatColor.YELLOW)
                .text(event.getObjective().getObjectiveTemplate().getFriendlyName()).color(ChatColor.AQUA);

        if (!Strings.isNullOrEmpty(event.getObjective().getObjectiveTemplate().getDescription())) {
            message.formattedTooltip(new FancyMessage(event.getObjective().getObjectiveTemplate().getDescription()).color(ChatColor.GRAY));
        }

        message.text(" angenommen.").color(ChatColor.YELLOW);

        event.getObjective().getQuest().getPlayer().spigot().sendMessage(message.create());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onObjectiveComplete(ObjectiveCompletedEvent event) {

        if (event.getObjective().getObjectiveTemplate().isSilent()) return;
        PlayerObjective objective = event.getObjective();
        FancyMessage msg = QuestUtil.getQuestTooltip(new FancyMessage(""), event.getObjective().getQuest());
        msg.then(": ").color(ChatColor.YELLOW)
                .then("Aufgabe ").color(ChatColor.GREEN)
                .then(objective.getObjectiveTemplate().getFriendlyName()).color(ChatColor.DARK_AQUA)
                .tooltip(objective.getObjectiveTemplate().getDescription())
                .then(" abgeschlossen").color(ChatColor.GREEN)
                .send(event.getObjective().getQuest().getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestStart(QuestStartedEvent event) {

        FancyMessage text = new FancyMessage("Quest").color(ChatColor.YELLOW)
                .text(" [").color(ChatColor.DARK_GRAY)
                .text(event.getQuest().getFriendlyName()).color(ChatColor.GREEN)
                .formattedTooltip(QuestUtil.getQuestTooltip(event.getQuest()))
                .text("]").color(ChatColor.DARK_GRAY)
                .text(" angenommen.").color(ChatColor.YELLOW);
        event.getQuest().getPlayer().spigot().sendMessage(text.create());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestComplete(QuestCompletedEvent event) {

        if (event.getQuest().getTemplate().isSilent()) return;
        FancyMessage msg = new FancyMessage(event.getQuest().getPlayer().getName()).color(ChatColor.AQUA)
                .then(" hat die Quest ").color(ChatColor.YELLOW);
        msg = QuestUtil.getQuestTooltip(msg, event.getQuest());
        msg.then(" abgeschlossen.").color(ChatColor.YELLOW)
                .send(Bukkit.getOnlinePlayers());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestAbort(QuestAbortedEvent event) {

        if (event.getQuest().getTemplate().isSilent()) return;
        FancyMessage msg = new FancyMessage("Die Quest ").color(org.bukkit.ChatColor.RED);
        QuestUtil.getQuestTooltip(msg, event.getQuest())
                .then(" wurde abgebrochen.").color(org.bukkit.ChatColor.RED)
                .send(event.getQuest().getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuestCompleted(QuestPoolQuestCompletedEvent event) {

        Optional<QuestPool> questPool = plugin.getQuestManager().getQuestPool(event.getQuestPool().getQuestPool());
        questPool.ifPresent(questPool1 -> questPool1.getRewardActions()
                .forEach(playerAction -> playerAction.accept(event.getQuest().getPlayer())));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTaskCompletion(TaskCompletedEvent event) {

        if (event.getTask().getTaskTemplate().isSilent()) return;
        PlayerTask task = event.getTask();
        FancyMessage message = new FancyMessage(task.getQuest().getFriendlyName()).color(ChatColor.GOLD).formattedTooltip(QuestUtil.getQuestTooltip(task.getQuest())
                .then(": ")
                .color(ChatColor.YELLOW)
                .then("Aufgabe ").color(ChatColor.GREEN)
                .then(task.getTaskTemplate().getFriendlyName()).color(ChatColor.DARK_AQUA)
                .tooltip(task.getTaskTemplate().getDescription())
                .then(" abgeschlossen").color(ChatColor.GREEN));
        event.getPlayer().spigot().sendMessage(message.create());
    }
}
