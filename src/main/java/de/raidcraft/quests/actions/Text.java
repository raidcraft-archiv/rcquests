package de.raidcraft.quests.actions;

import de.raidcraft.api.quests.QuestType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("text")
public class Text implements QuestType {

    @Method(name = "whisper", type = Type.ACTION)
    public static void whisper(Player player, ConfigurationSection data) {

        String sender = data.getString("sender");
        String msg = data.getString("msg");
        player.sendMessage(ChatColor.GRAY + sender + " flüstert: " + ChatColor.WHITE + ChatColor.ITALIC + msg);
    }

    @Method(name = "say", type = Type.ACTION)
    public static void say(Player player, ConfigurationSection data) {

        String sender = data.getString("sender");
        String msg = data.getString("msg");
        player.sendMessage(ChatColor.YELLOW + sender + ChatColor.GREEN + " : " + ChatColor.WHITE + msg);
    }
}
