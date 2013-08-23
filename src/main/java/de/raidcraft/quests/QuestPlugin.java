package de.raidcraft.quests;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.quests.InvalidTypeException;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.actions.Item;
import de.raidcraft.quests.actions.QuestActions;
import de.raidcraft.quests.actions.Text;
import de.raidcraft.quests.commands.BaseCommands;
import de.raidcraft.quests.listener.PlayerListener;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.tables.TQuestAction;
import de.raidcraft.quests.trigger.LocationTrigger;
import de.raidcraft.quests.trigger.PlayerTrigger;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class QuestPlugin extends BasePlugin {

    private LocalConfiguration configuration;
    private QuestManager questManager;
    private TriggerManager triggerManager;

    @Override
    public void enable() {

        configuration = configure(new LocalConfiguration(this));

        questManager = new QuestManager(this);
        triggerManager = new TriggerManager(this);
        Quests.enable(questManager);

        registerGlobalQuestTypes();
        registerGlobalTrigger();
        // register our events
        registerEvents(new PlayerListener(this));
        // commands
        registerCommands(BaseCommands.class);
        // load all of the quests after 2sec server start delay
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                questManager.load();
            }
        }, 40L);
    }

    @Override
    public void disable() {

        Quests.disable(questManager);
    }

    @Override
    public void reload() {

        getQuestManager().reload();
    }

    private void registerGlobalQuestTypes() {

        try {
            questManager.registerQuestType(new Text());
            questManager.registerQuestType(new Item());
            questManager.registerQuestType(new QuestActions());
        } catch (InvalidTypeException e) {
            getLogger().warning(e.getMessage());
        }
    }

    private void registerGlobalTrigger() {

        try {
            Quests.registerTrigger(this, LocationTrigger.class, true);
            Quests.registerTrigger(this, PlayerTrigger.class, true);
        } catch (InvalidTypeException e) {
            getLogger().warning(e.getMessage());
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        ArrayList<Class<?>> tables = new ArrayList<>();
        tables.add(TPlayer.class);
        tables.add(TPlayerQuest.class);
        tables.add(TPlayerObjective.class);
        tables.add(TQuestAction.class);
        return tables;
    }

    public LocalConfiguration getConfiguration() {

        return configuration;
    }

    public QuestManager getQuestManager() {

        return questManager;
    }

    public TriggerManager getTriggerManager() {

        return triggerManager;
    }

    public static class LocalConfiguration extends ConfigurationBase<QuestPlugin> {

        @Setting("quests-base-folder")
        public String quests_base_folder = "quests";

        public LocalConfiguration(QuestPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
