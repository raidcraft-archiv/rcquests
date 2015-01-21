package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.holder.DailyQuestsAvailable;
import de.raidcraft.api.quests.holder.QuestHolder;
import de.raidcraft.quests.QuestPlugin;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DailyRewardAction implements Action<Player> {

    private QuestPlugin plugin;

    public DailyRewardAction(QuestPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    @SneakyThrows
    public void accept(Player player, ConfigurationSection config) {
        if (!player.hasPermission("rcquests.quest.daily")) {
            throw new QuestException("Du hast nicht das Recht Dailies abzuschließen!");
        }
        QuestHolder holder = plugin.getQuestManager().getPlayer(player.getUniqueId());
        if (!(holder instanceof DailyQuestsAvailable)) {
            throw new QuestException("Du bist kein gueltiger DailyQuest Holder");
        }
        int dailyCounter = ((DailyQuestsAvailable) holder).getDailyCounter();
        RaidCraft.getEconomy().add(player.getUniqueId(), calculateMoney(dailyCounter), BalanceSource.QUEST, "Daily Quest");
        ((DailyQuestsAvailable) holder).setDailyCounter(dailyCounter + 1);
    }

    private double calculateMoney(int dailyCounter) {
        return dailyCounter * 10;
    }
}
