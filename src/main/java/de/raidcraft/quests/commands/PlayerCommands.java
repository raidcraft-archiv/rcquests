package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.quests.QuestPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PlayerCommands {

    private final QuestPlugin plugin;

    public PlayerCommands(QuestPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"list"},
            desc = "List all active quests"
    )
    public void list(CommandContext args, CommandSender sender) throws CommandException {

        QuestHolder player = plugin.getQuestManager().getQuestHolder((Player) sender);
        String questList = "";
        for(Quest quest : player.getActiveQuests()) {

            if(!questList.isEmpty()) questList += ChatColor.GREEN + ", ";
            questList += ChatColor.YELLOW + quest.getFriendlyName();
        }

        if(questList.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Du hast keine aktiven Quests!");
        }
        else {
            sender.sendMessage(ChatColor.GREEN + "Folgende Quests sind aktiv:");
            sender.sendMessage(questList);
        }
    }
}
