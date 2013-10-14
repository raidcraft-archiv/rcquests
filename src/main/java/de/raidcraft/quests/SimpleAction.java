package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.action.AbstractAction;
import de.raidcraft.quests.tables.TQuestAction;
import de.raidcraft.util.CaseInsensitiveHashSet;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleAction<T> extends AbstractAction<T> {

    private final ConfigurationSection data;
    private final Set<String> executedPlayers = new CaseInsensitiveHashSet();
    private final Map<String, Long> lastExecution = new CaseInsensitiveMap<>();

    public SimpleAction(int id, T provider, ConfigurationSection data) {

        super(id, provider, data);
        this.data = data.getConfigurationSection("args");
        // load existing executed actions
        List<TQuestAction> players = RaidCraft.getDatabase(QuestPlugin.class).find(TQuestAction.class).where()
                .eq("action_id", getId())
                .eq("name", getName())
                .eq("provider", getProvider().hashCode()).findList();
        for (TQuestAction action : players) {
            executedPlayers.add(action.getPlayer());
        }
    }

    @Override
    public void execute(final QuestHolder player, T holder) throws QuestException {

        String name = player.getName();
        if (isExecuteOnce() && executedPlayers.contains(name)) {
            return;
        }
        if (getCooldown() > 0 && lastExecution.containsKey(name)
                && lastExecution.get(name) - TimeUtil.ticksToMillis(getCooldown()) < System.currentTimeMillis()) {
            return;
        }
        if (getDelay() > 0) {
            Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(QuestPlugin.class), new Runnable() {
                @Override
                public void run() {

                    execute(player);
                }
            }, getDelay());
        } else {
            execute(player);
        }
    }

    private void execute(final QuestHolder player) {

        try {
            RaidCraft.getComponent(QuestManager.class).executeAction(getName(), player.getPlayer(), data);
            if (isExecuteOnce()) {
                executedPlayers.add(player.getName().toLowerCase());
                save();
            }
        } catch (QuestException e) {
            player.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        for (String player : executedPlayers) {
            player = player.toLowerCase();
            TQuestAction action = database.find(TQuestAction.class).where()
                    .eq("action_id", getId())
                    .eq("name", getName())
                    .eq("provider", getProvider().hashCode())
                    .eq("player", player).findUnique();
            if (action == null) {
                action = new TQuestAction();
                action.setActionId(getId());
                action.setName(getName());
                action.setProvider(getProvider().toString());
                action.setPlayer(player);
                database.save(action);
            }
        }
    }
}
