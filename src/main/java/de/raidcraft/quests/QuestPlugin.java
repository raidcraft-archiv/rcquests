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
import de.raidcraft.quests.npc.QuestTrait;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.trigger.HostTrigger;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Getter
public class QuestPlugin extends BasePlugin {

    private LocalConfiguration configuration;
    private QuestManager questManager;

    @Override
    public void enable() {

        configuration = configure(new LocalConfiguration(this));
        questManager = new QuestManager(this);

        registerTrigger();
        registerActions();

        try {
            // register our quest hosts
            Quests.registerQuestHost(this, "NPC", QuestNPCHost.class);
        } catch (InvalidQuestHostException e) {
            e.printStackTrace();
        }

        // register our events
        registerEvents(new PlayerListener(this));
        // commands
        registerCommands(BaseCommands.class);
        // load all of the quests after 2sec server start delay
        // then all plugins, actions and co are loaded
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                Quests.enable(questManager);
                getQuestManager().load();
            }
        }, 8 * 20L);

        // register NPC stuff
        // DO NOT LOAD NPC's we have no persitent npc's
        // their are automatically spawaned over the host.yml files
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        NPC_Manager.getInstance().registerTrait(QuestTrait.class, "quest");
    }

    @Override
    public void disable() {

        getQuestManager().unload();
        Quests.disable(questManager);
    }

    @Override
    public void reload() {
        // despawn all quests
        NPC_Manager.getInstance().clear(getName());
        Quests.disable(questManager);
        getQuestManager().reload();
        Quests.enable(questManager);
    }

    private void registerActions() {

        ActionFactory.getInstance().registerGlobalAction("quest.start", new StartQuestAction());
        ActionFactory.getInstance().registerGlobalAction("quest.complete", new CompleteQuestAction());
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
