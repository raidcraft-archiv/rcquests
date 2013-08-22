package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.quests.InvalidTypeException;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.QuestType;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
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

    protected QuestManager(QuestPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(QuestManager.class, this);
        load();
    }

    private void load() {

        // we need to look recursivly thru all folders under the defined base folder
        File baseFolder = new File(plugin.getDataFolder(), plugin.getConfiguration().quests_base_folder);
        if (baseFolder.mkdirs()) {
            loadQuests(baseFolder, "");
        }
    }

    public void reload() {

        loadedQuests.clear();
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

        String questId = (path + file.getName().toLowerCase()).substring(1);
        SimpleQuestTemplate quest = new SimpleQuestTemplate(questId, plugin.configure(new SimpleConfiguration<>(plugin, file)));
        loadedQuests.put(questId, quest);
    }

    @Override
    public void callTrigger(JavaPlugin plugin, String name, Player player, ConfigurationSection data) {

        this.plugin.getTriggerManager().callTrigger((plugin.getName() + "." + name).toLowerCase(), player, data);
    }

    public boolean checkRequirement(String name, Player player, ConfigurationSection data) {

        return requirementMethods.containsKey(name) && (boolean) invokeMethod(requirementMethods.get(name), player, data);
    }

    public void executeAction(String name, Player player, ConfigurationSection data) {

        if (!actionMethods.containsKey(name)) {
            return;
        }
        invokeMethod(actionMethods.get(name), player, data);
    }

    @Override
    public void registerQuestType(JavaPlugin plugin, QuestType questType) throws InvalidTypeException {

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
        String baseName = plugin.getName().replace(" ", "-") + "."
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

    private Object invokeMethod(Method method, Player player, ConfigurationSection data) {

        try {
            method.setAccessible(true);
            return method.invoke(null, player, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }
}
