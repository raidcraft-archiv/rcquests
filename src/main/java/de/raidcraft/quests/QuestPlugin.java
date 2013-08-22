package de.raidcraft.quests;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.quests.InvalidTypeException;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.questtypes.Item;
import de.raidcraft.quests.questtypes.Text;
import de.raidcraft.quests.tables.TQuestHolder;

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
        triggerManager = new TriggerManager(this);
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

        try {
            questManager.registerQuestType(new Text());
            questManager.registerQuestType(new Item());
        } catch (InvalidTypeException e) {
            getLogger().warning(e.getMessage());
        }
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

    public TriggerManager getTriggerManager() {

        return triggerManager;
    }

    public static class LocalConfiguration extends ConfigurationBase<QuestPlugin> {

        @Setting("quests-base-folder")
        public String quests_base_folder = "quests";

        public LocalConfiguration(QuestPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
