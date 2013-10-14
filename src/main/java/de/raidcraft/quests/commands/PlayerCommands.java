package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
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
            aliases = {"abort", "abbruch", "abrechen", "cancel"},
            desc = "Cancels the quest",
            min = 1
    )
    public void abort(CommandContext args, CommandSender sender) throws CommandException {

        try {
            QuestHolder player = plugin.getQuestManager().getQuestHolder((Player) sender);
            Quest quest = player.getQuest(args.getString(0));
            quest.abort();
        } catch (QuestException e) {
            throw new CommandException(e);
        }
    }
}
