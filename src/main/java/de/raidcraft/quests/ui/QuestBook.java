package de.raidcraft.quests.ui;

import de.raidcraft.quests.api.player.PlayerObjective;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * @author Silthus
 */
public class QuestBook extends ItemStack {

    private final Quest quest;

    protected QuestBook(Quest quest) {

        super(Material.BOOK_AND_QUILL, 1);
        this.quest = quest;
        // lets set the title description and so on
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(quest.getFriendlyName());
        ArrayList<String> lore = new ArrayList<>();
        lore.add(quest.getDescription());
        // add quest objectives
        for (PlayerObjective objective : quest.getPlayerObjectives()) {
            if (objective.isCompleted()) {
                lore.add(ChatColor.STRIKETHROUGH + "" + ChatColor.GRAY + objective.getObjective().getFriendlyName());
            } else {
                lore.add(objective.getObjective().getFriendlyName());
            }
        }
        meta.setLore(lore);
        setItemMeta(meta);
    }

    public void open() {

        // TODO: find a way to implement book opening
    }
}
