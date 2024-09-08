OxysBlockRegn/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── oxys/
│                   └── blockregen/
│                       └── OxysBlockRegn.java
│
└── src/
    └── main/
        └── resources/
            └── plugin.yml
            └── config.yml

package com.oxys.blockregen;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OxysBlockRegn extends JavaPlugin implements Listener {

    private Set<Material> regenerableBlocks = new HashSet<>();
    private long regenerationDelay;

    @Override
    public void onEnable() {
        // Load the config file
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        
        // Get the list of regenerable blocks from the config
        List<String> blockTypes = config.getStringList("regenerable-blocks");
        for (String blockType : blockTypes) {
            regenerableBlocks.add(Material.valueOf(blockType.toUpperCase()));
        }

        // Get the regeneration delay from the config
        regenerationDelay = config.getLong("regeneration-delay") * 20L; // Convert to ticks (20 ticks = 1 second)

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Oxy's Block Regn Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Oxy's Block Regn Plugin disabled!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (regenerableBlocks.contains(block.getType())) {
            scheduleBlockRegeneration(block);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (regenerableBlocks.contains(block.getType())) {
                scheduleBlockRegeneration(block);
            }
        }
    }

    private void scheduleBlockRegeneration(Block block) {
        Material originalMaterial = block.getType();
        block.setType(Material.AIR);  // Remove the block immediately

        // Schedule the regeneration after the configured delay
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(originalMaterial);
                getLogger().info("Block at " + block.getLocation() + " regenerated as " + originalMaterial.name());
            }
        }.runTaskLater(this, regenerationDelay);
    }
}
