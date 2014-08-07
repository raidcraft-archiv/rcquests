package de.raidcraft.quests;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.actions.CompleteQuestAction;
import de.raidcraft.quests.actions.StartQuestAction;
import de.raidcraft.quests.commands.BaseCommands;
import de.raidcraft.quests.listener.PlayerListener;
import de.raidcraft.quests.npc.NPCListener;
import de.raidcraft.quests.npc.QuestNPCHost;
import de.raidcraft.quests.npc.QuestSignHost;
import de.raidcraft.quests.npc.QuestTrait;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.trigger.HostTrigger;
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

        registerTrigger();
        registerActions();
        registerRequirements();

        try {
            // register our quest hosts
            Quests.registerQuestHost(this, "NPC", QuestNPCHost.class);
            Quests.registerQuestHost(this, "SIGN", QuestSignHost.class);
        } catch (InvalidQuestHostException e) {
            getLogger().warning(e.getMessage());
            e.printStackTrace();
        }

        // register our events
        registerEvents(new PlayerListener(this));
        // commands
        registerCommands(BaseCommands.class);
        // load all of the quests after 2sec server start delay
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                Quests.enable(questManager);
                getQuestManager().load();
            }
        }, 8 * 20L);

        // load NPC's
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        NPC_Manager.getInstance().registerTrait(QuestTrait.class, "quest");
        NPC_Manager.getInstance().loadNPCs(getName());
    }

    @Override
    public void disable() {

        getQuestManager().unload();
        Quests.disable(questManager);
    }

    @Override
    public void reload() {

        Quests.disable(questManager);
        getQuestManager().reload();
        Quests.enable(questManager);
    }

    private void registerActions() {

        ActionFactory.getInstance().registerGlobalAction("quest.start", new StartQuestAction());
        ActionFactory.getInstance().registerGlobalAction("quest.complete", new CompleteQuestAction());
    }

    private void registerRequirements() {

    }

    private void registerTrigger() {

        TriggerManager.getInstance().registerGlobalTrigger(new HostTrigger());
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        ArrayList<Class<?>> tables = new ArrayList<>();
        tables.add(TPlayer.class);
        tables.add(TPlayerQuest.class);
        tables.add(TPlayerObjective.class);
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
        @Setting("max-quests")
        public int maxQuests = 27;

        public LocalConfiguration(QuestPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
