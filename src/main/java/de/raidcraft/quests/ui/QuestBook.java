package de.raidcraft.quests.ui;

import de.raidcraft.api.quests.player.PlayerObjective;
import de.raidcraft.api.quests.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

/**
 * @author Silthus
 */
public class QuestBook extends ItemStack {

    private final Quest quest;

    protected QuestBook(Quest quest) {

        super(Material.WRITTEN_BOOK, 1);
        this.quest = quest;
        // lets set the title description and so on
        BookMeta meta = (BookMeta) getItemMeta();
        meta.setDisplayName(quest.getFriendlyName());
        meta.setAuthor("Quest");
        meta.setTitle(quest.getFriendlyName());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + quest.getDescription());
        lore.add("");
        // add quest objectives
        for (PlayerObjective objective : quest.getPlayerObjectives()) {
            String friendlyName = objective.getObjective().getFriendlyName();
            if (objective.isCompleted()) {
                lore.add(ChatColor.STRIKETHROUGH + "" + ChatColor.GRAY + friendlyName);
            } else if (objective.getObjective().isOptional()) {
                lore.add(ChatColor.ITALIC + friendlyName);
            } else {
                lore.add(ChatColor.WHITE + friendlyName);
            }
        }
        meta.setLore(lore);
        setItemMeta(meta);
    }

    public void open() {

        // TODO: find a way to implement book opening
    }
}
