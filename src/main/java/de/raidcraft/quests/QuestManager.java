package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.quests.*;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import de.raidcraft.api.quests.util.QuestUtil;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.util.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Silthus
 */
public final class QuestManager implements QuestProvider, Component {

    public static Requirement[] loadRequirements(ConfigurationSection data, String basePath) {

        if (data == null) {
            return new Requirement[0];
        }
        List<Requirement> requirements = new ArrayList<>();
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    if (key.equalsIgnoreCase("ordered")) {
                        continue;
                    }
                    ConfigurationSection section = QuestUtil.replaceThisReferences(data.getConfigurationSection(key), basePath);
                    SimpleRequirement requirement = new SimpleRequirement(Integer.parseInt(key), section);
                    requirements.add(requirement);
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning("Wrong requirement id in " + basePath + ": " + key);
                }
            }
        }
        Collections.sort(requirements);
        return requirements.toArray(new Requirement[requirements.size()]);
    }

    public static Trigger[] loadTrigger(ConfigurationSection data, QuestTemplate questTemplate, Trigger.Type type) {

        if (data == null) {
            return new Trigger[0];
        }
        List<Trigger> triggers = new ArrayList<>();
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    ConfigurationSection section = QuestUtil.replaceThisReferences(data.getConfigurationSection(key), questTemplate.getBasePath());
                    Trigger trigger;
                    if (type == Trigger.Type.QUEST_ACCEPTED) {
                        trigger = new ActiveQuestTrigger(Integer.parseInt(key), questTemplate, section, type);
                    } else if (type == Trigger.Type.QUEST_OBJECTIVE) {
                        trigger = new ObjectiveTrigger(Integer.parseInt(key), questTemplate, section, type);
                    } else {
                        trigger = new SimpleTrigger(Integer.parseInt(key), questTemplate, section, type);
                    }
                    triggers.add(trigger);
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning("Wrong trigger id in " + questTemplate.getId() + ": " + key);
                }
            }
        }
        Trigger[] loadedTriggers = triggers.toArray(new Trigger[triggers.size()]);
        RaidCraft.getComponent(TriggerManager.class).registerTrigger(loadedTriggers);
        return loadedTriggers;
    }

    public static <T> List<Action<T>> loadActions(T provider, ConfigurationSection data, String basePath) {

        ArrayList<Action<T>> actions = new ArrayList<>();
        if (data == null) {
            return actions;
        }
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    if (key.equalsIgnoreCase("execute-once")) {
                        continue;
                    }
                    ConfigurationSection section = QuestUtil.replaceThisReferences(data.getConfigurationSection(key), basePath);
                    actions.add(new SimpleAction<>(Integer.parseInt(key), provider, section));
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning("Wrong action id in " + basePath + ": " + key);
                }
            }
        }
        return actions;
    }


    private final QuestPlugin plugin;
    private final Map<String, QuestConfigLoader> configLoader = new CaseInsensitiveMap<>();
    private final Map<String, QuestTemplate> loadedQuests = new CaseInsensitiveMap<>();
    private final Map<String, QuestHost> loadedQuestHosts = new CaseInsensitiveMap<>();
    private final Map<String, Method> actionMethods = new CaseInsensitiveMap<>();
    private final Map<String, Method> requirementMethods = new CaseInsensitiveMap<>();
    private final Map<String, Constructor<? extends QuestHost>> questHostTypes = new CaseInsensitiveMap<>();

    private final Map<String, QuestHolder> questPlayers = new CaseInsensitiveMap<>();

    private boolean loadedQuestFiles = false;

    protected QuestManager(final QuestPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(QuestManager.class, this);
        try {
            // lets register our own config loader
            registerQuestConfigLoader(new QuestConfigLoader("quest") {
                @Override
                public void loadConfig(String id, ConfigurationSection config) {

                    SimpleQuestTemplate quest = new SimpleQuestTemplate(id, config);
                    loadedQuests.put(id, quest);
                    plugin.getLogger().info("Loaded quest: " + id + " - " + quest.getFriendlyName());
                }
            });
            // and the quest host loader
            registerQuestConfigLoader(new QuestConfigLoader("host") {
                @Override
                public void loadConfig(String id, ConfigurationSection config) {

                    String hostType = config.getString("type");
                    if (questHostTypes.containsKey(hostType)) {
                        try {
                            Constructor<? extends QuestHost> constructor = questHostTypes.get(hostType);
                            constructor.setAccessible(true);
                            QuestHost questHost = constructor.newInstance(id, config);
                            loadedQuestHosts.put(questHost.getId(), questHost);
                            plugin.getLogger().info("Loaded quest host: " + questHost.getId() + " - " + questHost.getFriendlyName());
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            plugin.getLogger().warning(e.getMessage());
                        }
                    } else {
                        plugin.getLogger().warning("Failed to load quest host \"" + id + "\"! Invalid host type: " + hostType);
                    }
                }
            });
        } catch (QuestException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void  load() {

        // we need to look recursivly thru all folders under the defined base folder
        File baseFolder = new File(plugin.getDataFolder(), plugin.getConfiguration().quests_base_folder);
        baseFolder.mkdirs();
        loadQuestConfigs(baseFolder, "");
        loadedQuestFiles = true;
    }

    public void unload() {

        for (QuestHolder holder : questPlayers.values()) {
            holder.save();
        }
        loadedQuests.clear();
        questPlayers.clear();
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
                        ConfigurationSection config = plugin.configure(new SimpleConfiguration<>(plugin, file));
                        config = QuestUtil.replaceThisReferences(config, path);
                        loader.loadConfig(id, config);
                    }
                }
            }
        }
    }

    public boolean checkRequirement(String name, Player player, ConfigurationSection data) throws QuestException {

        return requirementMethods.containsKey(name) && (boolean) invokeMethod(requirementMethods.get(name), player, data);
    }

    public void executeAction(String name, Player player, ConfigurationSection data) throws QuestException {

        if (!actionMethods.containsKey(name)) {
            return;
        }
        invokeMethod(actionMethods.get(name), player, data);
    }

    @Override
    public void registerQuestConfigLoader(QuestConfigLoader loader) throws QuestException {

        if (configLoader.containsKey(loader.getSuffix())) {
            throw new QuestException("Config loader with the suffix " + loader.getSuffix() + " is already registered!");
        }
        configLoader.put(loader.getSuffix(), loader);
        if (loadedQuestFiles) {
            // load again
            load();
        }
    }

    @Override
    public void registerQuestType(JavaPlugin plugin, QuestType questType) throws InvalidTypeException {

        registerQuestType(plugin.getName(), questType);
    }

    public void registerQuestType(QuestType questType) throws InvalidTypeException {

        registerQuestType("", questType);
    }

    public void registerQuestType(String baseName, QuestType questType) throws InvalidTypeException {

        if (!questType.getClass().isAnnotationPresent(QuestType.Name.class)) {
            throw new InvalidTypeException(
                    plugin.getName() + " tried to register invalid quest type: " + questType.getClass().getCanonicalName());
        }
        Method[] methods = questType.getClass().getDeclaredMethods();
        List<Method> validMethods = new ArrayList<>();
        // we need to filter out methods with invalid parameters or missing annotations
        for (Method method : methods) {
            if (method.isAnnotationPresent(QuestType.Method.class)) {
                if (method.getParameterTypes().length == 2
                        && method.getParameterTypes()[0].isAssignableFrom(Player.class)
                        && method.getParameterTypes()[1].isAssignableFrom(ConfigurationSection.class)) {
                    validMethods.add(method);
                } else {
                    throw new InvalidTypeException(plugin.getName() + " failed to register method " + method.getName()
                            + " in " + questType.getClass().getCanonicalName() + ": Invalid method parameters!");
                }
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new InvalidTypeException(plugin.getName() + " failed to register method " + method.getName()
                            + " in " + questType.getClass().getCanonicalName() + ": Method needs to be static!");
                }
            }
        }
        if (validMethods.isEmpty()) {
            throw new InvalidTypeException(plugin.getName() + " failed to register quest type "
                    + questType.getClass().getCanonicalName() + ": No valid methods defined!");
        }
        baseName = baseName.replace(" ", "-") + "."
                + questType.getClass().getAnnotation(QuestType.Name.class).value() + ".";
        if (baseName.startsWith(".")) {
            baseName = baseName.replaceFirst("\\.", "");
        }
        // actually register the methods into our system
        for (Method method : validMethods) {
            registerMethod(baseName + method.getAnnotation(QuestType.Method.class).name(),
                    method,
                    method.getAnnotation(QuestType.Method.class).type()
            );
        }
    }

    public void registerMethod(String name, Method method, QuestType.Type type) throws InvalidTypeException {

        switch (type) {

            case ACTION:
                actionMethods.put(name, method);
                plugin.getLogger().info(plugin.getName() + " - Action - " + name);
                break;
            case REQUIREMENT:
                if (method.getReturnType() != boolean.class) {
                    throw new InvalidTypeException("Failed to register method " + method.getName()
                            + " in " + method.getClass().getCanonicalName() + ": Requirement methods need to return a boolean.");
                }
                requirementMethods.put(name, method);
                plugin.getLogger().info(plugin.getName() + " - Requirement - " + name);
                break;
        }
    }

    @Override
    public void registerQuestHost(String type, Class<? extends QuestHost> clazz) throws InvalidQuestHostException {

        if (questHostTypes.containsKey(type)) {
            throw new InvalidQuestHostException("Tried to register duplicate quest host type: " + type);
        }

        try {
            Constructor<? extends QuestHost> constructor = clazz.getDeclaredConstructor(String.class, ConfigurationSection.class);
            questHostTypes.put(type, constructor);
            plugin.getLogger().info("Registered quest host type " + type + ": " + clazz.getCanonicalName());
        } catch (NoSuchMethodException e) {
            throw new InvalidQuestHostException(e.getMessage());
        }
    }

    @Override
    public QuestHost getQuestHost(String id) throws InvalidQuestHostException {

        if (loadedQuestHosts.containsKey(id)) {
            return loadedQuestHosts.get(id);
        }
        throw new InvalidQuestHostException("Unknown quest host with the id: " + id);
    }

    private Object invokeMethod(Method method, Player player, ConfigurationSection data) throws QuestException {

        try {
            method.setAccessible(true);
            return method.invoke(null, player, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public QuestHolder clearPlayerCache(String player) {

        return questPlayers.remove(player);
    }

    public QuestHolder getQuestHolder(Player player) {

        try {
            return getPlayer(player.getName());
        } catch (UnknownPlayerException e) {
            // will never occur
        }
        return null;
    }

    public QuestHolder getPlayer(String name) throws UnknownPlayerException {

        Player bukkitPlayer = Bukkit.getPlayer(name);
        if (bukkitPlayer != null) {
            name = bukkitPlayer.getName();
        }
        if (!questPlayers.containsKey(name)) {
            TPlayer table = plugin.getDatabase().find(TPlayer.class).where()
                    .eq("player", name.toLowerCase()).findUnique();
            if (table == null) {
                if (bukkitPlayer != null) {
                    table = new TPlayer();
                    table.setCompletedQuests(0);
                    table.setActiveQuests(0);
                    table.setPlayer(name.toLowerCase());
                    plugin.getDatabase().save(table);
                } else {
                    throw new UnknownPlayerException("Der Spieler " + name + " war noch nie auf RaidCraft eingeloggt.");
                }
            }
            questPlayers.put(name, new BukkitQuestHolder(table.getId(), name));
        }
        return questPlayers.get(name);
    }

    public QuestTemplate getQuestTemplate(String name) throws QuestException {

        if (loadedQuests.containsKey(name)) {
            return loadedQuests.get(name);
        }
        ArrayList<QuestTemplate> foundQuests = new ArrayList<>();
        for (QuestTemplate quest : loadedQuests.values()) {
            if (quest.getFriendlyName().toLowerCase().contains(name.toLowerCase())) {
                foundQuests.add(quest);
            }
        }
        if (foundQuests.isEmpty()) {
            throw new QuestException("Du hast keine Quest mit dem Namen: " + name);
        }
        if (foundQuests.size() > 1) {
            throw new QuestException("Du hast mehrere Quests mit dem Namen " + name + ": " + StringUtils.join(foundQuests, ", "));
        }
        return foundQuests.get(0);
    }

    public List<String> getLoadedRequirements() {

        return new ArrayList<>(requirementMethods.keySet());
    }

    public List<String> getLoadedActions() {

        return new ArrayList<>(actionMethods.keySet());
    }
}
