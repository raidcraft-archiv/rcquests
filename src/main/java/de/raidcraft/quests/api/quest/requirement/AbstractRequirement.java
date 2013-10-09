package de.raidcraft.quests.api.quest.requirement;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractRequirement implements Requirement {

    private final int id;
    private final String type;

    public AbstractRequirement(int id, ConfigurationSection data) {

        this.id = id;
        this.type = data.getString("type");
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public int compareTo(Requirement o) {

        return Integer.compare(getId(), o.getId());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractRequirement)) return false;

        AbstractRequirement that = (AbstractRequirement) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }

    @Override
    public String toString() {

        return getType();
    }
}
