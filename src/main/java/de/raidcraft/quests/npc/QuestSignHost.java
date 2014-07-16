package de.raidcraft.quests.npc;

import de.raidcraft.api.conversations.ConversationHost;
import de.raidcraft.api.quests.AbstractQuestHost;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Philip Urban
 */
public class QuestSignHost extends AbstractQuestHost implements ConversationHost {

    public final static String QUEST_SIGN_IDENTIFIER = "[Quest]";

    private final Sign sign;
    private final String defaultConversationName;
    private final Map<String, String> playerConversations = new CaseInsensitiveMap<>();

    public QuestSignHost(String id, ConfigurationSection data) {

        super(id, data);

        // read configured location
        ConfigurationSection loc = data.getConfigurationSection("location");
        Location location = new Location(Bukkit.getWorld(loc.getString("world", "world")),
                loc.getInt("x", 23), loc.getInt("y", 3), loc.getInt("z", 178));
        this.defaultConversationName = data.getString("default-conv", id + ".default");

        // build sign if not already exists
        if(location.getBlock().getType() != Material.SIGN_POST && location.getBlock().getType() != Material.WALL_SIGN)
        {
            location.getBlock().setType(Material.SIGN_POST);
        }
        sign = (Sign)location.getBlock().getState();

        // update sign content
        sign.setLine(0, ChatColor.GOLD + QUEST_SIGN_IDENTIFIER);
        sign.setLine(1, ChatColor.GREEN + getFriendlyName());

        String shortId = id;
        if(id.length() > 13) { shortId = id.substring(id.length() - 13); }

        sign.setLine(3, ChatColor.GRAY.toString() + shortId);

        sign.update(true);
    }

    @Override
    public Location getLocation() {

        return sign.getLocation();
    }

    @Override
    public String getUniqueId() {

        return getId() + getLocation().toString();
    }

    @Override
    public String getDefaultConversationName() {

        return defaultConversationName;
    }

    @Override
    public void setConversation(Player player, String conversation) {

        playerConversations.put(player.getName(), conversation);
    }

    @Override
    public String getConversation(Player player) {

        if (playerConversations.containsKey(player.getName())) {
            return playerConversations.get(player.getName());
        }
        return defaultConversationName;
    }
    // TODO fix compile error
    @Deprecated
    public void despawn() {

        sign.getBlock().setType(Material.AIR);
    }
}
