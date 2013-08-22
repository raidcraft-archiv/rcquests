package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.QuestType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
@QuestType.Name("item")
public class Item implements QuestType {

    @Method(name = "give", type = Type.ACTION)
    public static void giveItem(Player player, ConfigurationSection data) {

        try {
            ItemStack item = RaidCraft.getItem(data.getString("item"));
            item.setAmount(data.getInt("amount"));
            player.getInventory().addItem(item);
        } catch (CustomItemException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }
}
