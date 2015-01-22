package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.AbstractQuestHost;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.util.CaseInsensitiveMap;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Silthus
 */
public class QuestNPCHost extends AbstractQuestHost {

    private NPC npc;
    private String defaultConversationName;
    private Map<String, String> playerConversations = new CaseInsensitiveMap<>();
    private Location defaultLocation;

    public QuestNPCHost(String id, ConfigurationSection data) {

        super(id, data);
        ConfigurationSection loc = data.getConfigurationSection("location");
        defaultLocation = new Location(Bukkit.getWorld(loc.getString("world", "world")),
                loc.getInt("x", 23), loc.getInt("y", 3), loc.getInt("z", 178));
        this.defaultConversationName = data.getString("default-conv", id + ".default");

        // spawn a NPC
        spawn();

        npc.addTrait(QuestTrait.class);
        npc.addTrait(ConversationsTrait.class);
        npc.getTrait(QuestTrait.class).setHostId(getId());
        npc.getTrait(ConversationsTrait.class).setConversationName(getDefaultConversationName());

        //        npc.getBukkitEntity().setCustomName(getFriendlyName());
        //        npc.getBukkitEntity().setCustomNameVisible(true);
        // lets set the equipment
        ConfigurationSection equipment = data.getConfigurationSection("equipment");
        Equipment equipmentTrait = npc.getTrait(Equipment.class);
        if (equipment != null && equipmentTrait != null) {
            try {
                equipmentTrait.set(0, RaidCraft.getItem(equipment.getString("hand", Material.AIR.name())));
                equipmentTrait.set(1, RaidCraft.getItem(equipment.getString("helmet", Material.AIR.name())));
                equipmentTrait.set(2, RaidCraft.getItem(equipment.getString("chestplate", Material.AIR.name())));
                equipmentTrait.set(3, RaidCraft.getItem(equipment.getString("leggings", Material.AIR.name())));
                equipmentTrait.set(4, RaidCraft.getItem(equipment.getString("boots", Material.AIR.name())));
            } catch (CustomItemException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
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
    public void spawn() {

        // spawn a NPC
        npc = QuestTrait.getNPC(getId());
        // if NPC not exists, warn admin and create new one
        if (npc == null) {
            QuestPlugin plugin = RaidCraft.getComponent(QuestPlugin.class);
            plugin.info("Quest NPC not exists and spawned automaticly hostid:" + getId(), "npc");
            npc = NPC_Quest_Manager.getInstance().spawnNonPersistNpcQuest(
                    defaultLocation, getFriendlyName(), plugin.getName(), defaultConversationName, getId());
        }
    }

    @Override
    public void despawn() {

        npc.despawn(DespawnReason.PLUGIN);
    }
}
