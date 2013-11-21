package de.raidcraft.quests.trigger;

import com.sk89q.worldedit.Vector;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.QuestTrigger;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
@QuestTrigger.Name("player")
public class PlayerTrigger extends QuestTrigger implements Listener {

    // crafting
    private ItemStack craftResult;

    // clicking
    private Vector location;

    protected PlayerTrigger(Trigger trigger) {

        super(trigger);
    }

    @Override
    protected void load(ConfigurationSection data) {

        location = new Vector(data.getInt("x"), data.getInt("y"), data.getInt("z"));

        try {
            craftResult = RaidCraft.getItem(data.getString("item", "0"));
        } catch (CustomItemException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    @Method("craft")
    @EventHandler(ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent event) {

        if (craftResult == null || event.getRecipe().getResult().isSimilar(craftResult)) {
            inform("craft", (Player) event.getWhoClicked());
        }
    }

    @Method("click")
    @EventHandler(ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent event) {

        if(event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) return;

        Location clickedLocation = event.getClickedBlock().getLocation();

        if(clickedLocation.getBlockX() == location.getBlockX()
            && clickedLocation.getBlockY() == location.getBlockY()
            && clickedLocation.getBlockZ() == location.getBlockZ()) {
            inform("click", event.getPlayer());
        }
    }
}
