package de.raidcraft.quests.commands;

import com.google.common.base.Joiner;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.quests.player.QuestHolder;
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
        sender.sendMessage(ChatColor.YELLOW + Joiner.on(',').join(player.getActiveQuests()));
    }
}
