package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestType;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;

/**
 * @author Silthus
 */
@QuestType.Name("block")
public class Block implements QuestType {

    @Method(name = "set", type = Type.ACTION)
    public static void setBlock(Player player, ConfigurationSection data) {

        Location loc = new Location(player.getWorld(), data.getInt("x"), data.getInt("y"), data.getInt("z"));
        String matName = data.getString("material", "AIR");
        Material material;
        material = ItemUtils.getItem(matName);
        if(material == null) {
            RaidCraft.LOGGER.warning("Wrong material in Quest action! Material '" + matName + "' does not exist!");
            return;
        }

        loc.getBlock().setType(material);
    }

    @Method(name = "door", type = Type.ACTION)
    public static void changeDoorState(Player player, ConfigurationSection data) {

        Location loc = new Location(player.getWorld(), data.getInt("x"), data.getInt("y"), data.getInt("z"));
        boolean open = data.getBoolean("open", true);

        org.bukkit.block.Block block = loc.getBlock();
        if(block.getType() != Material.WOODEN_DOOR && block.getType() != Material.IRON_DOOR) {
            return;
        }

        BlockState state = block.getState();
        Door door = (Door) state.getData();
        door.setOpen(open);
        state.update();
    }
}
