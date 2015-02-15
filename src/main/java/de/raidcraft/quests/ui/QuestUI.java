package de.raidcraft.quests.ui;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.Quest;
import de.raidcraft.quests.api.QuestHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

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

    public QuestUI(QuestHolder holder, List<Quest> quests, Type type) {

        this.holder = holder;
        this.quests = quests;
        this.type = type;
        this.questBooks = new QuestBook[quests.size()];
        this.inventory = Bukkit.createInventory(
                holder.getPlayer(),
                ((RaidCraft.getComponent(QuestPlugin.class).getConfiguration().maxQuests + 9) / 9) * 9,
                type.getInventoryName());
        // lets now fill the inventory with our quest books
        for (int i = 0; i < quests.size(); i++) {
            QuestBook questBook = new QuestBook(quests.get(i));
            inventory.setItem(i, questBook);
            questBooks[i] = questBook;
        }
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

        holder.getPlayer().openInventory(inventory);
        // also register ourselves as event listener to catch the clicks
        RaidCraft.getComponent(QuestPlugin.class).registerEvents(this);
    }

    public void close() {

        holder.getPlayer().closeInventory();
        // dont forget to unregister our listener
        HandlerList.unregisterAll(this);
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
