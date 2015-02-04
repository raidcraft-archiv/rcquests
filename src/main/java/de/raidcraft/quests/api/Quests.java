package de.raidcraft.quests.api;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class Quests {

    private Quests() {

    }

    private static QuestProvider provider;
    private static Map<String, Class<? extends QuestHost>> queuedHosts = new CaseInsensitiveMap<>();
    private static Set<QuestConfigLoader> queuedConfigLoader = new HashSet<>();

    public static void enable(QuestProvider questProvider) {

        provider = questProvider;
        // and all queued hosts
        for (Map.Entry<String, Class<? extends QuestHost>> entry : queuedHosts.entrySet()) {
            try {
                provider.registerQuestHost(entry.getKey(), entry.getValue());
            } catch (InvalidQuestHostException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
        queuedHosts.clear();
        // and all queued config loader
        for (QuestConfigLoader loader : queuedConfigLoader) {
            try {
                provider.registerQuestConfigLoader(loader);
            } catch (QuestException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
        queuedConfigLoader.clear();
    }

    public static void disable(QuestProvider questProvider) {

        provider = null;
    }

    public static boolean isEnabled() {

        return provider != null;
    }

    public static void registerQuestLoader(QuestConfigLoader loader) throws QuestException {

        if (isEnabled()) {
            provider.registerQuestConfigLoader(loader);
        } else {
            if (queuedConfigLoader.contains(loader)) {
                throw new QuestException("Config loaded with same suffix is arelady registered: " + loader.getSuffix());
            }
            queuedConfigLoader.add(loader);
        }
    }

    public static void registerQuestHost(JavaPlugin plugin, String type, Class<? extends QuestHost> clazz) throws InvalidQuestHostException {

        if (isEnabled()) {
            provider.registerQuestHost(type, clazz);
        } else {
            if (queuedHosts.containsKey(type)) {
                throw new InvalidQuestHostException(plugin.getName() + " tried to register already registered quest host: " + type);
            }
            queuedHosts.put(type, clazz);
        }
    }

    public static QuestHolder getQuestHolder(Player player) {

        return provider.getQuestHolder(player);
    }

    public static QuestHost getQuestHost(String id) throws InvalidQuestHostException {

        return provider.getQuestHost(id);
    }
}
