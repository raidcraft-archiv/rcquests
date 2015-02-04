package de.raidcraft.quests.api;

import de.raidcraft.RaidCraft;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public abstract class AbstractQuestHost implements QuestHost {

    private final String id;
    private final String name;
    private final String type;
    private final String basePath;
    private final String friendlyName;

    public AbstractQuestHost(String id, ConfigurationSection data) {

        this.id = id;
        String[] split = id.split("\\.");
        this.name = split[split.length - 1];
        this.basePath = id.replace("." + name, "");
        this.type = data.getString("type");
        this.friendlyName = data.getString("name", name);
    }

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public String getBasePath() {

        return basePath;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public String toString() {

        return getFriendlyName();
    }

    @Override
    public final void interact(Player player) {

        RaidCraft.callEvent(new QuestHostInteractEvent(this, player));
    }
}
