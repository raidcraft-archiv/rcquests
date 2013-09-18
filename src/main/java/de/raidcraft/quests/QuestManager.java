package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.quests.InvalidTypeException;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.QuestType;
import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class QuestManager implements QuestProvider, Component {

    private static final String QUEST_FILE_SUFFIX = ".quest.yml";

    private final QuestPlugin plugin;
    private final Map<String, QuestTemplate> loadedQuests = new CaseInsensitiveMap<>();
    private final Map<String, Method> actionMethods = new CaseInsensitiveMap<>();
    private final Map<String, Method> requirementMethods = new CaseInsensitiveMap<>();
    private final Map<String, QuestHolder> questPlayers = new CaseInsensitiveMap<>();

    protected QuestManager(QuestPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(QuestManager.class, this);
    }

    public void load() {

        // we need to look recursivly thru all folders under the defined base folder
        File baseFolder = new File(plugin.getDataFolder(), plugin.getConfiguration().quests_base_folder);
        baseFolder.mkdirs();
        loadQuests(baseFolder, "");
    }

    public void unload() {

        for (QuestHolder holder : questPlayers.values()) {
            holder.save();
        }
        loadedQuests.clear();
        questPlayers.clear();
    }

    public void reload() {

        unload();
        load();
    }

    private void loadQuests(File baseFolder, String path) {

        for (File file : baseFolder.listFiles()) {
            if (file.isDirectory()) {
                path += "." + file.getName().toLowerCase();
                loadQuests(file, path);
            } else if (file.getName().endsWith(QUEST_FILE_SUFFIX)) {
                // this will load the quest file
                loadQuest(file, path);
            }
        }
    }

    private void loadQuest(File file, String path) {

        String questId = (path + "." + file.getName().toLowerCase()).substring(1).replace(QUEST_FILE_SUFFIX, "");
        SimpleQuestTemplate quest = new SimpleQuestTemplate(questId, plugin.configure(new SimpleConfiguration<>(plugin, file)));
        loadedQuests.put(questId, quest);
        plugin.getLogger().info("Loaded quest: " + questId + " - " + quest.getFriendlyName());
    }

    @Override
    public void callTrigger(String name, Player player) {

        this.plugin.getTriggerManager().callTrigger(name, player);
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
        // actually register the methods into our system
        for (Method method : validMethods) {
            registerMethod(baseName + method.getAnnotation(QuestType.Method.class).name(),
                    method,
                    method.getAnnotation(QuestType.Method.class).type()
            );
        }
    }

    public void registerMethod(String name, Method method, QuestType.Type type) throws InvalidTypeException {

        name = name.substring(1);
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

    public QuestHolder getPlayer(Player player) {

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

    public QuestTemplate getQuestTemplate(String name) {

        return loadedQuests.get(name);
    }
}
