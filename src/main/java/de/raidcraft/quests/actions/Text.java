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
        String[] text = data.getString("text").split("|");
        for (String msg : text) {
            player.sendMessage(ChatColor.GRAY + sender + " flüstert: " + ChatColor.WHITE + ChatColor.ITALIC + msg);
        }
    }

    @Method(name = "npc", type = Type.ACTION)
    public static void npc(Player player, ConfigurationSection data) {

        String sender = data.getString("sender");
        String[] text = data.getString("text").split("|");
        for (String msg : text) {
            player.sendMessage(ChatColor.GREEN + sender + " : " + ChatColor.WHITE + msg);
        }
    }

    @Method(name = "server", type = Type.ACTION)
    public static void server(Player player, ConfigurationSection data) {

        String[] text = data.getString("text").split("|");
        for (String msg : text) {
            player.sendMessage(ChatColor.YELLOW + "Server PM: " + msg);
        }
    }

    @Method(name = "emote", type = Type.ACTION)
    public static void emote(Player player, ConfigurationSection data) {

        String[] text = data.getString("text").split("|");
        for (String msg : text) {
            player.sendMessage(ChatColor.GOLD + msg);
        }
    }

    @Method(name = "player", type = Type.ACTION)
    public static void player(Player player, ConfigurationSection data) {

        String[] text = data.getString("text").split("|");
        for (String msg : text) {
            player.sendMessage(ChatColor.DARK_PURPLE + player.getName() + " : " + ChatColor.WHITE + msg);
        }
    }
}
