package me.hqm.plugindev.wands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WandListener implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            double K = -1.25, C = 3.5;
            double x = K + Math.random() * C;
            double y = K + Math.random() * C;
            double z = K + Math.random() * C;

            FallingBlock fallingblock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getTypeId(), block.getData());
            fallingblock.setDropItem(false);
            fallingblock.setVelocity(new Vector(x, y, z));
            block.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK: {
                Method method = WandsPlugin.getWandMethod(event.getPlayer().getItemInHand());
                if (method != null) {
                    try {
                        method.invoke(WandsPlugin.getInst(), event.getPlayer());
                    } catch (IllegalAccessException | InvocationTargetException oops) {
                        oops.printStackTrace();
                    }
                }
            }
        }
    }
}
