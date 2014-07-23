package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.ConversationHost;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.AbstractQuestHost;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.rcconversations.npc.NPC_Conservations_Manager;
import de.raidcraft.util.CaseInsensitiveMap;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * @author Silthus
 */
public class QuestNPCHost extends AbstractQuestHost implements ConversationHost {

    private NPC npc;
    private String defaultConversationName;
    private Map<String, String> playerConversations = new CaseInsensitiveMap<>();

    public QuestNPCHost(String id, ConfigurationSection data) {

        super(id, data);
        ConfigurationSection loc = data.getConfigurationSection("location");
        Location location = new Location(Bukkit.getWorld(loc.getString("world", "world")),
                loc.getInt("x", 23), loc.getInt("y", 3), loc.getInt("z", 178));
        this.defaultConversationName = data.getString("default-conv", id + ".default");
        Plugin plugin = RaidCraft.getComponent(QuestPlugin.class);
        // spawn a NPC
        npc = QuestTrait.getNPC(id);
        // if NPC not exists, warn admin and create new one
        if(npc == null) {
            plugin.getLogger().warning("Quest NPC not exists and spawned automaticly hostid:" + id);
            npc = NPC_Quest_Manager.getInstance().spawnPersistNpcQuest(
                    location, getFriendlyName(), plugin.getName(), defaultConversationName, id);
        }

        npc.addTrait(QuestTrait.class);
        npc.getTrait(QuestTrait.class).setHostId(getId());

//        npc.getBukkitEntity().setCustomName(getFriendlyName());
//        npc.getBukkitEntity().setCustomNameVisible(true);
        // lets set the equipment
        ConfigurationSection equipment = data.getConfigurationSection("equipment");
        Equipment equipmentTrait = npc.getTrait(Equipment.class);
        if(equipment != null && equipmentTrait != null) {
            try {
                equipmentTrait.set(0, RaidCraft.getItem(equipment.getString("hand", Material.AIR.name())));
                equipmentTrait.set(1, RaidCraft.getItem(equipment.getString("helmet", Material.AIR.name())));
                equipmentTrait.set(2, RaidCraft.getItem(equipment.getString("chestplate", Material.AIR.name())));
                equipmentTrait.set(3, RaidCraft.getItem(equipment.getString("leggings", Material.AIR.name())));
                equipmentTrait.set(4, RaidCraft.getItem(equipment.getString("boots", Material.AIR.name())));
            } catch (CustomItemException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    @Override
    public String getDefaultConversationName() {

        return defaultConversationName;
    }

    @Override
    public Location getLocation() {

        return npc.getStoredLocation();
    }

    @Override
    public String getUniqueId() {

        return getId() + ":" + npc.getId();
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

    @Override
    public void despawn() {
        npc.despawn(DespawnReason.PLUGIN);
    }
}
