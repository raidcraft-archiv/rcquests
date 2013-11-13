package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("block")
public class Block implements QuestType {

    @Method(name = "set", type = Type.ACTION)
    public static void setBlock(Player player, ConfigurationSection data) {

        Location loc =
                new Location(player.getWorld(), data.getInt("x"), data.getInt("y"), data.getInt("z"));
        String matName = data.getString("material", "AIR");
        Material material = null;
        try {
            material = Material.valueOf(matName);
        } catch (Exception e) {
            RaidCraft.LOGGER.warning("Wrong material in Quest action! Material '" + matName + "' does not exist!");
            return;
        }

        loc.getBlock().setType(material);
    }
}
