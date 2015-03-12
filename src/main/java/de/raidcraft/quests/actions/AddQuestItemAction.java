package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.holder.QuestHolder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public class AddQuestItemAction implements Action<Player> {

    @Override
    public void accept(Player player, ConfigurationSection config) {

        try {
            QuestHolder holder = RaidCraft.getComponent(QuestManager.class).getQuestHolder(player);
            ItemStack item = RaidCraft.getItem(config.getString("item"), config.getInt("amount", 1));
            holder.getQuestInventory().addItem(item);
        } catch (CustomItemException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }
}
