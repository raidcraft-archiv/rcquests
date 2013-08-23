package de.raidcraft.quests.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.QuestTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
@QuestTrigger.Name("player")
public class PlayerTrigger extends QuestTrigger implements Listener {

    // crafting
    private ItemStack craftResult;

    @Override
    protected void load(ConfigurationSection data) {

        try {
            craftResult = RaidCraft.getItem(data.getString("item"));
        } catch (CustomItemException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {

        if (craftResult == null || event.getRecipe().getResult().isSimilar(craftResult)) {
            inform("craft", (Player) event.getWhoClicked());
        }
    }
}
