package de.raidcraft.quests.config;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigBuilderException;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.QuestConfigLoader;
import de.raidcraft.quests.QuestPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class QuestHostConfigLoader extends QuestConfigLoader implements ConfigGenerator {

    private final QuestPlugin plugin;

    public QuestHostConfigLoader(QuestPlugin plugin) {

        super("host");
        this.plugin = plugin;
    }

    @Override
    public void loadConfig(String id, ConfigurationSection config) {

        String hostType = config.getString("type");
        if (plugin.getQuestManager().isQuestHostType(hostType)) {
            plugin.getQuestManager().createQuestHost(hostType, id, config);
        } else {
            plugin.getLogger().warning("Failed to load quest host \"" + id + "\"! Invalid host type: " + hostType);
        }
    }

    @Override
    public String replaceReference(String key) {

        try {
            return plugin.getQuestManager().getQuestHost(key).getFriendlyName();
        } catch (InvalidQuestHostException e) {
            return key;
        }
    }

    @Information(
            value = "quest.host",
            desc = "Creates a quest host at the current location with the current equipment (-e)",
            usage = "[-c <default-conv(this.id.default)>] [-t <type(NPC)>] [-e] <id> <Host Name>",
            flags = "t:c:e",
            min = 2,
            help = "Execute at the position you want to create the Quest Host. Equip the items the host should have and use the -e flag."
    )
    public <T extends BasePlugin> void build(ConfigBuilder<T> builder, CommandContext args, Player player) throws ConfigBuilderException {

        ConfigurationSection config = new MemoryConfiguration();
        String questHostType = args.getFlag('t', "NPC");
        if (!plugin.getQuestManager().isQuestHostType(questHostType)) {
            throw new ConfigBuilderException("Quest Host Type " + args.getFlag('t') + " not found!");
        }
        // set name
        config.set("name", args.getJoinedStrings(1));
        // set type
        config.set("type", questHostType);
        // default conv
        config.set("default-conv", args.getFlag('c', "this." + args.getString(0) + ".default.conv.yml"));
        // set location
        ConfigurationSection location = config.createSection("location");
        location.set("world", player.getWorld().getName());
        location.set("x", player.getLocation().getBlockX());
        location.set("y", player.getLocation().getBlockY());
        location.set("z", player.getLocation().getBlockZ());
        // set equipment
        if (args.hasFlag('e')) {
            ConfigurationSection equipment = config.createSection("equipment");
            equipment.set("hand", RaidCraft.getItemIdString(player.getEquipment().getItemInHand()));
            equipment.set("head", RaidCraft.getItemIdString(player.getEquipment().getHelmet()));
            equipment.set("chest", RaidCraft.getItemIdString(player.getEquipment().getChestplate()));
            equipment.set("legs", RaidCraft.getItemIdString(player.getEquipment().getLeggings()));
            equipment.set("boots", RaidCraft.getItemIdString(player.getEquipment().getBoots()));
        }
        builder.createConfig(args.getString(0) + getSuffix(), config);
        builder.setLocked(true);
    }
}
