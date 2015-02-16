package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.mobs.Mobs;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.items.ItemsPlugin;
import de.raidcraft.items.configs.NamedYAMLCustomItem;
import de.raidcraft.mobs.MobsPlugin;
import de.raidcraft.quests.api.InvalidQuestHostException;
import de.raidcraft.quests.api.QuestConfigLoader;
import de.raidcraft.quests.api.QuestException;
import de.raidcraft.quests.api.provider.Quests;
import de.raidcraft.quests.actions.CompleteObjectiveAction;
import de.raidcraft.quests.actions.CompleteQuestAction;
import de.raidcraft.quests.actions.StartConversationAction;
import de.raidcraft.quests.actions.StartQuestAction;
import de.raidcraft.quests.commands.BaseCommands;
import de.raidcraft.quests.listener.PlayerListener;
import de.raidcraft.quests.npc.NPCListener;
import de.raidcraft.quests.npc.QuestNPCHost;
import de.raidcraft.quests.npc.QuestTrait;
import de.raidcraft.quests.requirements.HasCompletedQuestRequirement;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.trigger.HostTrigger;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

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
        Bukkit.getScheduler().runTaskLater(this, () -> {

            Quests.enable(questManager);
            getQuestManager().load();
        }, 8 * 20L);

        // register NPC stuff
        // DO NOT LOAD NPC's we have no persitent npc's
        // their are automatically spawaned over the host.yml files
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        NPC_Manager.getInstance().registerTrait(QuestTrait.class, "quest");

        // register conversation quest loader
        try {
            Quests.registerQuestLoader(new QuestConfigLoader("conv") {
                @Override
                public void loadConfig(String id, ConfigurationSection config) {

                    RaidCraft.getComponent(RCConversationsPlugin.class).getConversationManager()
                            .registerConversationTemplate(id, config);
                }
            });
        } catch (QuestException e) {
            getLogger().warning(e.getMessage());
        }
        // also register a quest config loader for custom items in quests
        try {
            Quests.registerQuestLoader(new QuestConfigLoader("item") {
                @Override
                public void loadConfig(String id, ConfigurationSection config) {

                    try {
                        CustomItem customItem = new NamedYAMLCustomItem(config.getString("name", id), config);
                        RaidCraft.getComponent(ItemsPlugin.class).getCustomItemManager().registerNamedCustomItem(id, customItem);
                        getLogger().info("Loaded custom quest item: " + id + " (" + customItem.getName() + ")");
                    } catch (CustomItemException e) {
                        getLogger().warning(e.getMessage());
                    }
                }

                @Override
                public String replaceReference(String key) {

                    ItemStack unsafeItem = RaidCraft.getUnsafeItem(key);
                    if (unsafeItem != null) {
                        if (unsafeItem instanceof CustomItemStack) {
                            return ((CustomItemStack) unsafeItem).getItem().getName();
                        }
                        return unsafeItem.getType().name();
                    }
                    return key;
                }
            });
        } catch (QuestException e) {
            warning(e.getMessage());
            e.printStackTrace();
        }
        // register mob config loader
        try {
            Quests.registerQuestLoader(new QuestConfigLoader("mob") {
                @Override
                public void loadConfig(String id, ConfigurationSection config) {
                    RaidCraft.getComponent(MobsPlugin.class).getMobManager().registerMob(id, config);
                }

                @Override
                public String replaceReference(String key) {

                    return Mobs.getFriendlyName(key);
                }
            });
            Quests.registerQuestLoader(new QuestConfigLoader("mobgroup") {
                @Override
                public void loadConfig(String id, ConfigurationSection config) {
                    RaidCraft.getComponent(MobsPlugin.class).getMobManager().registerMobGroup(id, config);
                }
            });
        } catch (QuestException e) {
            getLogger().warning(e.getMessage());
        }
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

    private void registerActionAPI() {

        ActionAPI.register(this)
                .global()
                    .trigger(new HostTrigger())
                    .action("quest.start", new StartQuestAction())
                    .action("quest.complete", new CompleteQuestAction())
                    .action("quest.objective.complete", new CompleteObjectiveAction())
                    .requirement("quest.comppleted", new HasCompletedQuestRequirement());
        ActionAPI.register(RaidCraft.getComponent(RCConversationsPlugin.class))
                .action("conversation.start", new StartConversationAction());
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
