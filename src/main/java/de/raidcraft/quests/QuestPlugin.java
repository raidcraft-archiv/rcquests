package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.chat.Chat;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.random.RDS;
import de.raidcraft.quests.actions.AddQuestItemAction;
import de.raidcraft.quests.actions.CompleteObjectiveAction;
import de.raidcraft.quests.actions.CompleteQuestAction;
import de.raidcraft.quests.actions.RemoveQuestItemAction;
import de.raidcraft.quests.actions.StartConversationAction;
import de.raidcraft.quests.actions.StartQuestAction;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.commands.BaseCommands;
import de.raidcraft.quests.listener.PlayerListener;
import de.raidcraft.quests.npc.NPCListener;
import de.raidcraft.quests.npc.QuestNPCHost;
import de.raidcraft.quests.npc.QuestTrait;
import de.raidcraft.quests.random.RDSQuestObject;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.tables.TPlayerQuestPool;
import de.raidcraft.quests.tables.TQuestItem;
import de.raidcraft.quests.trigger.HostTrigger;
import de.raidcraft.quests.trigger.ObjectiveTrigger;
import de.raidcraft.quests.trigger.QuestPoolTrigger;
import de.raidcraft.quests.trigger.QuestTrigger;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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

        registerActionAPI();
        Chat.registerAutoCompletionProvider(this, new QuestAutoCompletionProvider());
        // register our quest hosts
        Quests.registerQuestHost(this, "NPC", QuestNPCHost.class);

        // register our events
        registerEvents(new PlayerListener(this));
        // commands
        registerCommands(BaseCommands.class);
        // load all of the quests after 20sec server start delay
        // then all plugins, actions and co are loaded
        Bukkit.getScheduler().runTaskLater(this, () -> {

            Quests.enable(questManager);
            getQuestManager().load();
        }, getConfiguration().questLoadDelay * 10L);

        // register NPC stuff
        // DO NOT LOAD NPC's we have no persitent npc's
        // their are automatically spawaned over the host.yml files
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        NPC_Manager.getInstance().registerTrait(QuestTrait.class, "quest");
        RDS.registerObject(new RDSQuestObject.RDSQuestFactory());
    }

    @Override
    public void disable() {

        NPC_Manager.getInstance().clear(getName());
        Quests.disable(questManager);
        getQuestManager().unload();
    }

    @Override
    public void reload() {
        // despawn all quests
        NPC_Manager.getInstance().clear(getName());
        Quests.disable(questManager);
        getQuestManager().reload();
        Quests.enable(questManager);
    }

    private void registerActionAPI() {

        ActionAPI.register(this)
                .global()
                    .trigger(new HostTrigger())
                    .trigger(new QuestTrigger())
                    .trigger(new ObjectiveTrigger())
                    .trigger(new QuestPoolTrigger())
                    .action(new StartQuestAction())
                    .action(new CompleteQuestAction())
                    .action(new CompleteObjectiveAction())
                    .action("quest.item.remove", new RemoveQuestItemAction())
                    .action("quest.item.add", new AddQuestItemAction())
                    .requirement("questpool.successive.count", (Player player, ConfigurationSection config) -> {
                        QuestHolder questHolder = getQuestManager().getQuestHolder(player);
                        if (questHolder == null) return false;
                        TPlayerQuestPool questPool = getDatabase().find(TPlayerQuestPool.class).where()
                                .eq("player_id", questHolder.getId())
                                .eq("quest_pool", config.getString("pool"))
                                .findUnique();
                        if (questPool == null) return false;
                        return questPool.getSuccessiveQuestCounter() >= config.getInt("count");
                    })
                    .requirement("quest.completed", (Player player, ConfigurationSection config) -> {
                        QuestHolder holder = getQuestManager().getQuestHolder(player);
                        return holder != null && holder.hasCompletedQuest(config.getString("quest"));
                    })
                    .requirement("quest.active", (Player player, ConfigurationSection config) -> {
                        QuestHolder holder = getQuestManager().getQuestHolder(player);
                        return holder != null && holder.hasActiveQuest(config.getString("quest"));
                    })
                    .requirement("quest.item.has", (Player player, ConfigurationSection config) -> {
                        QuestHolder holder = getQuestManager().getQuestHolder(player);
                        return holder != null && holder.getQuestInventory().contains(config.getString("item"));
                    });
        ActionAPI.register(RaidCraft.getComponent(RCConversationsPlugin.class))
                .action(new StartConversationAction());
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        ArrayList<Class<?>> tables = new ArrayList<>();
        tables.add(TPlayer.class);
        tables.add(TPlayerQuest.class);
        tables.add(TPlayerObjective.class);
        tables.add(TQuestItem.class);
        tables.add(TPlayerQuestPool.class);
        return tables;
    }

    public static class LocalConfiguration extends ConfigurationBase<QuestPlugin> {

        @Setting("quests-base-folder")
        public String quests_base_folder = "quests";
        @Setting("max-quests")
        public int maxQuests = 27;
        @Setting("quest-load-delay")
        public int questLoadDelay = 30;
        @Setting("quest-pool-delay")
        public int questPoolDelay = 100;

        public LocalConfiguration(QuestPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
