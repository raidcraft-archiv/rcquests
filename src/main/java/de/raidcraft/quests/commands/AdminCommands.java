package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.QuestTemplate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class AdminCommands {

    private final QuestPlugin plugin;

    public AdminCommands(QuestPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"reload"},
            desc = "Reloads the quest plugin"
    )
    @CommandPermissions("rcquests.admin.reload")
    public void reload(CommandContext args, CommandSender sender) {

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded Quest Plugin sucessfully!");
    }

    @Command(
            aliases = {"accept", "a"},
            desc = "Accepts a quest",
            min = 1
    )
    @CommandPermissions("rcquests.admin.accept")
    public void accept(CommandContext args, CommandSender sender) throws CommandException {

        try {
            QuestTemplate questTemplate = plugin.getQuestManager().getQuestTemplate(args.getString(0));
            QuestHolder player = plugin.getQuestManager().getPlayer((Player) sender);
            if (questTemplate == null) {
                throw new CommandException("Unknown quest: " + args.getString(0));
            }
            player.startQuest(questTemplate);
        } catch (QuestException e) {
            throw new CommandException(e);
        }
    }
}
