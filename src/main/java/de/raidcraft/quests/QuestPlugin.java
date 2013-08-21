package de.raidcraft.quests;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.tables.TQuestHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class QuestPlugin extends BasePlugin {

    private LocalConfiguration configuration;
    private QuestManager questManager;

    @Override
    public void enable() {

        configuration = configure(new LocalConfiguration(this));

        questManager = new QuestManager(this);
        Quests.enable(questManager);

        registerGlobalQuestTypes();
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

        // TODO: add global quest types, like text.whisper
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        ArrayList<Class<?>> tables = new ArrayList<>();
        tables.add(TQuestHolder.class);
        return tables;
    }

    public LocalConfiguration getConfiguration() {

        return configuration;
    }

    public QuestManager getQuestManager() {

        return questManager;
    }

    public static class LocalConfiguration extends ConfigurationBase<QuestPlugin> {

        @Setting("quests-base-folder")
        public String quests_base_folder = "quests";

        public LocalConfiguration(QuestPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
