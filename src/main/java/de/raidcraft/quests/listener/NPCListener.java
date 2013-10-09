package de.raidcraft.quests.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestTrigger;
import de.raidcraft.quests.QuestPlugin;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

/**
 * @author Silthus
 */
@QuestTrigger.Name("host")
public class NPCListener extends QuestTrigger implements Listener {

    private final QuestPlugin plugin;
    private String hostId;

    public NPCListener() {

        this.plugin = RaidCraft.getComponent(QuestPlugin.class);
    }

    @Override
    protected void load(ConfigurationSection data) {

        hostId = data.getString("host");
    }

    @Method("interact")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(NPCRightClickEvent event) {

        if (!event.getNPC().getBukkitEntity().hasMetadata("HOST_ID")) {
            return;
        }
        List<MetadataValue> metadata = event.getNPC().getBukkitEntity().getMetadata("HOST_ID");
        if (metadata.get(0).asString().equalsIgnoreCase(hostId)) {
            inform(event.getClicker());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onAttack(NPCLeftClickEvent event) {

        if (!event.getNPC().getBukkitEntity().hasMetadata("HOST_ID")) {
            return;
        }
        List<MetadataValue> metadata = event.getNPC().getBukkitEntity().getMetadata("HOST_ID");
        if (metadata.get(0).asString().equalsIgnoreCase(hostId)) {
            inform(event.getClicker());
        }
    }
}
