package de.raidcraft.quests.actions;

import de.raidcraft.api.quests.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("teleport")
public class Teleport implements QuestType {

    @Method(name = "coords", type = Type.ACTION)
    public static void warpToCoords(Player player, ConfigurationSection data) {

        Location loc =
                new Location(player.getWorld(), data.getInt("x"), data.getInt("y"), data.getInt("z"), (float)data.getDouble("yaw", 0), (float)data.getDouble("pitch", 0));
        player.teleport(loc);
    }

    @Method(name = "player", type = Type.ACTION)
    public static void warpToPlayer(Player player, ConfigurationSection data) {

        Player targetPlayer = Bukkit.getPlayer(data.getString("player"));
        if(targetPlayer != null) {
            player.teleport(targetPlayer);
        }
    }
}
