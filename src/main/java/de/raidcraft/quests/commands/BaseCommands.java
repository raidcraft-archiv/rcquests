package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.quests.QuestPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class BaseCommands {

    private final QuestPlugin plugin;

    public BaseCommands(QuestPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"quest", "q"},
            desc = "Base Command for quests"
    )
    @NestedCommand(PlayerCommands.class)
    public void quest(CommandContext args, CommandSender sender) {


    }

    @Command(
            aliases = {"rcq"},
            desc = "Admin Commands"
    )
    @NestedCommand(AdminCommands.class)
    public void admin(CommandContext args, CommandSender sender) {


    }
}
