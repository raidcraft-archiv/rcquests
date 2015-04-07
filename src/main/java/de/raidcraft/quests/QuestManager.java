package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.quests.api.objective.PlayerObjective;
import de.raidcraft.api.quests.QuestConfigLoader;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.api.quests.host.QuestHost;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.quests.config.QuestHostConfigLoader;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ConfigUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class QuestManager implements QuestProvider, Component {

    private final QuestPlugin plugin;
    private final Map<String, QuestConfigLoader> configLoader = new CaseInsensitiveMap<>();
    private final Map<String, QuestTemplate> loadedQuests = new CaseInsensitiveMap<>();
    private final Map<String, QuestHost> loadedQuestHosts = new CaseInsensitiveMap<>();
    private final Map<String, Constructor<? extends QuestHost>> questHostTypes = new CaseInsensitiveMap<>();

    private final Map<UUID, QuestHolder> questPlayers = new HashMap<>();

    private boolean loadedQuestFiles = false;

    protected QuestManager(final QuestPlugin plugin) {
        this.plugin = plugin;
        RaidCraft.registerComponent(QuestManager.class, this);
        // lets register our own config loader
        registerQuestConfigLoader(new QuestConfigLoader("quest") {
            @Override
            public void loadConfig(String id, ConfigurationSection config) {

                SimpleQuestTemplate quest = new SimpleQuestTemplate(id, config);
                // lets register the triggers of the quest
                quest.registerListeners();
                loadedQuests.put(id, quest);
                plugin.info("Loaded quest: " + id + " - " + quest.getFriendlyName());
            }
        });
        // and the quest host loader
        registerQuestConfigLoader(new QuestHostConfigLoader(plugin));
    }

    public void load() {
        // we need to look recursivly thru all folders under the defined base folder
        File baseFolder = new File(plugin.getDataFolder(), plugin.getConfiguration().quests_base_folder);
        baseFolder.mkdirs();
        loadQuestConfigs(baseFolder, "");
        loadedQuestFiles = true;
    }

    public void unload() {
        questPlayers.values().forEach(holder -> {
            holder.save();
            holder.getAllQuests()
                    .forEach(quest -> quest.getPlayerObjectives()
                            .forEach(PlayerObjective::unregisterListeners));
        });
        loadedQuests.values()
                .forEach(template -> template.getStartTrigger()
                        .forEach(trigger -> trigger.unregisterListener(template)));
        loadedQuests.clear();
        questPlayers.clear();
        loadedQuestHosts.clear();
        loadedQuestFiles = false;
    }

    public void reload() {
        unload();
        load();
    }

    private void loadQuestConfigs(File baseFolder, String path) {

        for (File file : baseFolder.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                loadQuestConfigs(file, path + "." + fileName.toLowerCase());
            } else {
                if (path.startsWith(".")) {
                    path = path.replaceFirst("\\.", "");
                }
                for (QuestConfigLoader loader : configLoader.values()) {
                    if (file.getName().toLowerCase().endsWith(loader.getSuffix())) {
                        String id = (path + "." + file.getName().toLowerCase()).replace(loader.getSuffix(), "");
                        ConfigurationSection configFile = plugin.configure(new SimpleConfiguration<>(plugin, file));
                        // repace "this." with the absolte path, feature: relative path
                        configFile = ConfigUtil.replacePathReferences(configFile, path);
                        loader.loadConfig(id, configFile);
                    }
                }
            }
        }
    }

    @Override
    public void registerQuestConfigLoader(QuestConfigLoader loader) {
        if (configLoader.containsKey(loader.getSuffix())) {
            RaidCraft.LOGGER.warning("Config loader with the suffix " + loader.getSuffix() + " is already registered!");
            return;
        }
        configLoader.put(loader.getSuffix(), loader);
        ConfigBuilder.registerConfigGenerator(loader);
        if (loadedQuestFiles) {
            load();
        }
    }

    @Nullable
    @Override
    public QuestConfigLoader getQuestConfigLoader(String suffix) {

        QuestConfigLoader loader = null;
        if (configLoader.containsKey(suffix)) {
            loader = configLoader.get(suffix);
        } else if (configLoader.containsKey("." + suffix + ".yml")) {
            loader = configLoader.get("." + suffix + ".yml");
        }
        return loader;
    }

    @Override
    public void registerQuestHost(String type, Class<? extends QuestHost> clazz) {
        if (isQuestHostType(type)) {
            RaidCraft.LOGGER.warning("Tried to register duplicate quest host type: " + type);
            return;
        }

        try {
            Constructor<? extends QuestHost> constructor = clazz.getDeclaredConstructor(String.class, ConfigurationSection.class);
            questHostTypes.put(type, constructor);
            plugin.info("Registered quest host type " + type + ": " + clazz.getCanonicalName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public boolean isQuestHostType(String type) {
        return questHostTypes.containsKey(type);
    }

    public void createQuestHost(String type, String id, ConfigurationSection config) {
        try {
            Constructor<? extends QuestHost> constructor = questHostTypes.get(type);
            constructor.setAccessible(true);
            QuestHost questHost = constructor.newInstance(id, config);
            loadedQuestHosts.put(questHost.getId(), questHost);
            plugin.info("Loaded quest host: " + questHost.getId() + " - " + questHost.getFriendlyName(), "host");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public QuestHost getQuestHost(String id) throws InvalidQuestHostException {
        if (loadedQuestHosts.containsKey(id)) {
            return loadedQuestHosts.get(id);
        }
        // search for string end
        for (String key : loadedQuestHosts.keySet()) {
            if (key.endsWith(id)) {
                return loadedQuestHosts.get(key);
            }
        }
        throw new InvalidQuestHostException("Unknown quest host with the id: " + id);
    }

    @Override
    public boolean hasQuestItem(Player player, ItemStack item, int amount) {

        QuestHolder questHolder = getQuestHolder(player);
        if (questHolder == null) {
            return false;
        }
        return questHolder.getQuestInventory().contains(item, amount);
    }

    @Override
    public void removeQuestItem(Player player, ItemStack... itemStack) {

        QuestHolder questHolder = getQuestHolder(player);
        if (questHolder == null) return;
        questHolder.getQuestInventory().removeItem(itemStack);
    }

    @Override
    public void addQuestItem(Player player, ItemStack... itemStack) {

        QuestHolder questHolder = getQuestHolder(player);
        if (questHolder == null) return;
        questHolder.getQuestInventory().addItem(itemStack);
    }

    public QuestHolder clearPlayerCache(UUID playerId) {
        return questPlayers.remove(playerId);
    }

    public QuestHolder getQuestHolder(Player player) {
        try {
            return getPlayer(player.getUniqueId());
        } catch (UnknownPlayerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public QuestHolder getPlayer(UUID playerId) throws UnknownPlayerException {
        Player bukkitPlayer = Bukkit.getPlayer(playerId);
        if (bukkitPlayer == null) {
            throw new UnknownPlayerException("Player not online?");
        }
        String name = bukkitPlayer.getName();
        if (!questPlayers.containsKey(playerId)) {
            TPlayer table = plugin.getDatabase().find(TPlayer.class).where()
                    .eq("player_id", playerId).findUnique();
            if (table == null) {
                table = new TPlayer();
                table.setCompletedQuests(0);
                table.setActiveQuests(0);
                table.setPlayer(name.toLowerCase());
                table.setPlayerId(playerId);
                plugin.getDatabase().save(table);
            }
            questPlayers.put(playerId, new BukkitQuestHolder(table.getId(), playerId));
        }
        return questPlayers.get(playerId);
    }

    public QuestTemplate getQuestTemplate(String name) throws QuestException {
        if (loadedQuests.containsKey(name)) {
            return loadedQuests.get(name);
        }

        List<QuestTemplate> foundQuests = loadedQuests.values().stream()
                .filter(quest -> quest.getFriendlyName().toLowerCase().contains(name.toLowerCase()))
                .map(quest -> quest)
                .collect(Collectors.toList());

        if (foundQuests.isEmpty()) {
            throw new QuestException("Du hast keine Quest mit dem Namen: " + name);
        }
        if (foundQuests.size() > 1) {
            throw new QuestException("Du hast mehrere Quests mit dem Namen " + name + ": " + StringUtils.join(foundQuests, ", "));
        }
        return foundQuests.get(0);
    }
}
