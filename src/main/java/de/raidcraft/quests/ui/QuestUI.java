package de.raidcraft.quests.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.items.commands.BookUtilCommands;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.quests.api.objective.PlayerTask;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.holder.QuestHolder;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class QuestUI implements Listener {

    public enum Type {

        ACTIVE("Angenommene Aufgaben"),
        CLOSED("Erledigte Aufgaben");

        private final String inventoryName;

        private Type(String inventoryName) {

            this.inventoryName = inventoryName;
        }

        public String getInventoryName() {

            return inventoryName;
        }
    }

    private final QuestHolder holder;
    private final List<Quest> quests;
    private final Type type;
    private final Inventory inventory;
    private final QuestBook[] questBooks;
    private final ItemStack questBook;

    public QuestUI(QuestHolder holder, List<Quest> quests, Type type) {

        this.holder = holder;
        this.quests = quests;
        this.type = type;
        this.questBooks = new QuestBook[quests.size()];
        this.inventory = Bukkit.createInventory(
                holder.getPlayer(),
                ((RaidCraft.getComponent(QuestPlugin.class).getConfiguration().maxQuests + 9) / 9) * 9,
                type.getInventoryName());
        // lets fill the inventory with our quest books
        for (int i = 0; i < quests.size(); i++) {
            QuestBook questBook = new QuestBook(quests.get(i));
            inventory.setItem(i, questBook);
            questBooks[i] = questBook;
        }

        BookUtil.BookBuilder book = BookUtil.writtenBook().title("Quest Buch");

        BookUtil.PageBuilder index = BookUtil.PageBuilder.of(BookUtil.TextBuilder.of("Quests").color(ChatColor.GOLD).build())
                .newLine().newLine();
        ArrayList<BookUtil.PageBuilder> pages = new ArrayList<>();
        for (int i = 0; i < quests.size(); i++) {
            Quest quest = quests.get(i);
            index.add(BookUtil.TextBuilder.of(quest.getFriendlyName()).color(ChatColor.AQUA)
                    .onHover(BookUtil.HoverAction.showText(ChatColor.GRAY + "Klicke auf den Titel der Quest um zur Seite im Buch zu springen."))
                    .onClick(BookUtil.ClickAction.changePage(i + 2))
                    .build()).newLine();
            BookUtil.PageBuilder questText = BookUtil.PageBuilder.of(BookUtil.TextBuilder.of("Zurück zur Übersicht").color(ChatColor.GRAY).style(ChatColor.ITALIC).onClick(BookUtil.ClickAction.changePage(1)).build())
                    .newLine()
                    .add(BookUtil.TextBuilder.of(quest.getFriendlyName()).color(ChatColor.GOLD).build())
                    .newLine()
                    .newLine();
            for (PlayerObjective objective : quest.getObjectives()) {
                BookUtil.PageBuilder objectiveText = questText.add(BookUtil.TextBuilder.of("  - ").color(ChatColor.GOLD).text(objective.getObjectiveTemplate().getFriendlyName())
                        .color(objective.isCompleted() ? ChatColor.DARK_GREEN : objective.isActive() ? ChatColor.AQUA : ChatColor.DARK_GRAY).build())
                        .newLine()
                        .add(BookUtil.TextBuilder.of("    " + objective.getObjectiveTemplate().getDescription()).color(ChatColor.GRAY).style(ChatColor.ITALIC).build())
                        .newLine();
                for (PlayerTask playerTask : objective.getTasks()) {
                    objectiveText.add(BookUtil.TextBuilder.of("     > ").color(ChatColor.GOLD).text(playerTask.getTaskTemplate().getFriendlyName())
                            .color(playerTask.isCompleted() ? ChatColor.DARK_GREEN : playerTask.isActive() ? ChatColor.AQUA : ChatColor.DARK_GRAY).build())
                            .add(BookUtil.TextBuilder.of("       " + playerTask.getTaskTemplate().getDescription()).color(ChatColor.GRAY).style(ChatColor.ITALIC).build())
                            .newLine();
                }
                questText.add(objectiveText.build());
            }
            pages.add(questText);
        }

        pages.add(0, index);
        book.pages(pages.stream().map(BookUtil.PageBuilder::build).collect(Collectors.toList()));

        this.questBook = book.build();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getWhoClicked().equals(holder.getPlayer()) || !event.getInventory().equals(inventory)) {
            return;
        }
        if (0 <= event.getSlot() && event.getSlot() < questBooks.length) {
            questBooks[event.getSlot()].open();
        }
        event.setCancelled(true);
    }

    public void open() {

        BookUtil.openPlayer(getHolder().getPlayer(), questBook);
//        holder.getPlayer().openInventory(inventory);
//        // also register ourselves as event listener to catch the clicks
//        RaidCraft.getComponent(QuestPlugin.class).registerEvents(this);
    }

    public void close() {

//        holder.getPlayer().closeInventory();
//        // dont forget to unregister our listener
//        HandlerList.unregisterAll(this);
    }

    public QuestHolder getHolder() {

        return holder;
    }

    public List<Quest> getQuests() {

        return quests;
    }

    public Type getType() {

        return type;
    }
}
