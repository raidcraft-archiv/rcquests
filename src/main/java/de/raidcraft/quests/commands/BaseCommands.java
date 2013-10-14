package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.quests.ui.QuestUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class BaseCommands {

    private final QuestPlugin plugin;

    public BaseCommands(QuestPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"quests", "ql"},
            desc = "Lists all quests of the player",
            // p: player, c: closed, d: daily
            flags = "p:cd"
    )
    public void quests(CommandContext args, CommandSender sender) throws CommandException {

        QuestManager manager = plugin.getQuestManager();
        QuestHolder player = manager.getQuestHolder((Player) sender);
        if (args.hasFlag('p')) {
            try {
                player = manager.getPlayer(args.getFlag('p'));
            } catch (UnknownPlayerException e) {
                throw new CommandException(e.getMessage());
            }
        }

        List<Quest> quests;
        if (args.hasFlag('c')) {
            quests = player.getCompletedQuests();
        } else if (args.hasFlag('d')) {
            // daily quests
            quests = new ArrayList<>();
        } else {
            quests = player.getActiveQuests();
        }

        new QuestUI(player, quests, (args.hasFlag('c') ? QuestUI.Type.CLOSED : QuestUI.Type.ACTIVE)).open();
    }

    @Command(
            aliases = {"quest", "rcq"},
            desc = "Base Command for quests"
    )
    @NestedCommand(value = PlayerCommands.class)
    public void quest(CommandContext args, CommandSender sender) {


    }

    @Command(
            aliases = {"rcqa"},
            desc = "Admin Commands"
    )
    @NestedCommand(AdminCommands.class)
    public void admin(CommandContext args, CommandSender sender) {


    }
}
