package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.language.Translator;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.holder.QuestHolder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class CompleteQuestAction implements Action<Player> {

    @Override
    public void accept(Player player, ConfigurationSection config) {

        if (!player.hasPermission("rcquests.quest.complete")) {
            Translator.msg(QuestPlugin.class, player, "action.quest.complete.no-permission", "Du hast nicht das Recht Quests zu starten!");
            return;
        }
        try {
            QuestManager component = RaidCraft.getComponent(QuestManager.class);
            QuestHolder questHolder = component.getQuestHolder(player);
            Quest quest = questHolder.getQuest(config.getString("quest"));
            quest.complete();
        } catch (QuestException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}
