package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.host.AbstractQuestHost;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rcconversations.npc.TalkCloseTrait;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Silthus
 */
public class QuestNPCHost extends AbstractQuestHost {

    private NPC npc;
    private ConfigurationSection data;

    private HashMap<UUID, String> playerConversations = new HashMap<>();

    public QuestNPCHost(String id, ConfigurationSection data) {

        super(id, data);
        this.data = data;
        // spawn a NPC
        if (!spawn()) {
            return;
        }

        npc.addTrait(QuestTrait.class);
        npc.addTrait(ConversationsTrait.class);
        npc.getTrait(QuestTrait.class).setHostId(getId());
        npc.getTrait(ConversationsTrait.class).setConversationName(getDefaultConversationName());

        // add talk close
        if (data.getBoolean("talk-close", false)) {
            npc.addTrait(TalkCloseTrait.class);
        }

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
                RaidCraft.getComponent(QuestPlugin.class).warning("QuestNPCHost: canot equip " + getName() + " : " + getBasePath());
                e.printStackTrace();
            }
        }
    }

    private String getConversation(String key, String fileKey) {
        return data.getString(key, getId() + "." + fileKey);
    }

    @Override
    public String getDefaultConversationName() {
        return getConversation("default-conv", "default");
    }

    @Override
    public Location getLocation() {
        return npc.getStoredLocation();
    }

    // this ID is not uniquie for the entity, because we create a new one
    // on startup and reload
    @Override
    public String getUniqueId() {
        return getId();
    }

    @Override
    public void setConversation(Player player, String conversation) {
        playerConversations.put(player.getUniqueId(), conversation);
    }

    @Override
    public String getConversation(Player player) {
        // TODO: check quest status?
        if (playerConversations.containsKey(player.getUniqueId())) {
            return playerConversations.get(player.getUniqueId());
        }
        return getDefaultConversationName();
    }

    @Override
    public boolean spawn() {
        // spawn a NPC
        npc = QuestTrait.getNPC(getId());
        // if NPC not exists, warn admin and create new one
        if (npc == null) {
            QuestPlugin plugin = RaidCraft.getComponent(QuestPlugin.class);
            ConfigurationSection loc = data.getConfigurationSection("location");
            World world;
            world = Bukkit.getWorld(loc.getString("world"));
            if (world == null) {
                plugin.info("Quest NPC world is not loaded and NPC was not spawned: " + getId(), "npc");
                return false;
            }
            Location defaultLocation = new Location(world, loc.getInt("x", 23), loc.getInt("y", 3), loc.getInt("z", 178));
            plugin.info("Quest NPC not exists and spawned automaticly hostid: " + getId(), "npc");
            npc = NPC_Quest_Manager.getInstance().spawnNonPersistNpcQuest(
                    defaultLocation, getFriendlyName(), plugin.getName(), getDefaultConversationName(), getId());
            return true;
        }
        return false;
    }

    @Override
    public void despawn() {
        npc.despawn(DespawnReason.PLUGIN);
    }
}
