package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.util.CommandUtil;
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
            aliases = {"accept", "start", "a"},
            desc = "Starts a quest",
            usage = "<Quest>",
            min = 1
    )
    @CommandPermissions("rcquests.admin.accept")
    public void accept(CommandContext args, CommandSender sender) throws CommandException {

        try {
            QuestTemplate questTemplate = plugin.getQuestManager().getQuestTemplate(args.getString(0));
            QuestHolder player = plugin.getQuestManager().getQuestHolder((Player) sender);
            if (questTemplate == null) {
                throw new CommandException("Unknown quest: " + args.getString(0));
            }
            player.startQuest(questTemplate);
        } catch (QuestException e) {
            throw new CommandException(e);
        }
    }

    @Command(
            aliases = {"abort", "abbruch", "abrechen", "cancel"},
            desc = "Cancels the quest",
            min = 1,
            flags = "p:",
            usage = "[-p <Player>] <Quest>"
    )
    @CommandPermissions("rcquests.admin.abort")
    public void abort(CommandContext args, CommandSender sender) throws CommandException {

        Player targetPlayer = args.hasFlag('p') ? CommandUtil.grabPlayer(args.getFlag('p')) : (Player) sender;
        String questName = args.getJoinedStrings(0);
        if (targetPlayer == null) {
            throw new CommandException("Der angegebene Spieler ist nicht Online!");
        }
        try {
            QuestHolder questPlayer = plugin.getQuestManager().getQuestHolder(targetPlayer);
            Quest quest = plugin.getQuestManager().findQuest(questPlayer, questName);
            if (quest.isActive()) {
                quest.abort();
                quest.delete();
                plugin.getQuestManager().clearCache(questPlayer.getPlayer());
                sender.sendMessage(ChatColor.GREEN + "Die Quest '" + quest.getFriendlyName() + "' wurde abgebrochen!");
            } else {
                throw new CommandException("Quest " + questName + " ist nicht aktiv und kann nicht abgebrochen werden!");
            }
        } catch (QuestException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"delete", "remove", "del"},
            desc = "Removes the quest",
            min = 1,
            flags = "p:",
            usage = "[-p <Player>] <Quest>"
    )
    @CommandPermissions("rcquests.admin.remove")
    public void remove(CommandContext args, CommandSender sender) throws CommandException {

        Player targetPlayer = args.hasFlag('p') ? CommandUtil.grabPlayer(args.getFlag('p')) : (Player) sender;
        String questName = args.getJoinedStrings(0);
        if (targetPlayer == null) {
            throw new CommandException("Der angegebene Spieler ist nicht Online!");
        }
        try {
            QuestHolder questPlayer = plugin.getQuestManager().getQuestHolder(targetPlayer);
            Quest quest = plugin.getQuestManager().findQuest(questPlayer, questName);
            if (!quest.isCompleted()) {
                throw new CommandException("Die Quest " + quest.getFriendlyName() + " wurde noch nicht abgeschlossen!");
            }
            quest.delete();
            plugin.getQuestManager().clearCache(questPlayer.getPlayer());
            sender.sendMessage(ChatColor.GREEN + "Die Quest '" + quest.getFriendlyName() + "' wurde entfernt!");
        } catch (QuestException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
