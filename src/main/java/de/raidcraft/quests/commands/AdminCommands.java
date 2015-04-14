package de.raidcraft.quests.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

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
        String questName = args.getString(0);
        if (targetPlayer == null) {
            throw new CommandException("Der angegebene Spieler ist nicht Online!");
        }
        QuestHolder questPlayer = plugin.getQuestManager().getQuestHolder(targetPlayer);
        Optional<Quest> quest = questPlayer.getQuest(questName);
        if (quest.isPresent() && quest.get().isActive()) {
            quest.get().abort();
            sender.sendMessage(ChatColor.GREEN + "Die Quest '" + quest.get().getFriendlyName() + "' wurde abgebrochen!");
        } else {
            throw new CommandException("Quest " + questName + " existiert nicht oder ist nicht aktiv!");
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
        String questName = args.getString(0);
        if (targetPlayer == null) {
            throw new CommandException("Der angegebene Spieler ist nicht Online!");
        }
        QuestHolder questPlayer = plugin.getQuestManager().getQuestHolder(targetPlayer);
        Optional<Quest> quest = questPlayer.getQuest(questName);
        if (!quest.isPresent()) {
            throw new CommandException("Die Quest " + questName + " existiert nicht!");
        }
        if (!quest.get().isCompleted()) {
            throw new CommandException("Die Quest " + quest.get().getFriendlyName() + " wurde noch nicht abgeschlossen!");
        }
        quest.get().delete();
        sender.sendMessage(ChatColor.GREEN + "Die Quest '" + quest.get().getFriendlyName() + "' wurde entfernt!");
    }

    @Command(
            aliases = {"create"},
            desc = "Starts the Quest Creation Wizard",
            min = 1,
            usage = "<path.to.the.quest.root.dir>"
    )
    @CommandPermissions("rcquests.admin.create")
    public void create(CommandContext args, CommandSender sender) {

        ConfigBuilder.createBuilder(plugin, (Player) sender, args.getString(0));
        sender.sendMessage(ChatColor.RED + "Started Quest Builder! Exit with '/rccb save'. See commands with '/rccb' and '/rccb <TabAutoComplete> ?'");
    }
}
