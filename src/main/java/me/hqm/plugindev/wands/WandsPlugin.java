package me.hqm.plugindev.wands;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WandsPlugin extends JavaPlugin implements Listener, CommandExecutor {
    private static final List<Wand.Meta> WAND_CACHE = new ArrayList<>();
    private static WandsPlugin INST;

    @Override
    public void onEnable() {
        INST = this;
        registerWands();
        getCommand("wands").setExecutor(this);
        getServer().getPluginManager().registerEvents(new WandListener(), this);
    }

    private void registerWands() {
        getLogger().info("Registering wands...");
        for (Method method : WandsPlugin.class.getMethods()) {
            getLogger().info(method.getName());
            if (method.isAnnotationPresent(Wand.class)) {
                getLogger().info(" ^^ REGISTERED");
                Wand wand = method.getAnnotation(Wand.class);
                Wand.Meta meta = new Wand.Meta(wand, method);
                WAND_CACHE.add(meta);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wands")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                createGUI(player);
            }
        }
        return true;
    }

    public void createGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Wands");
        int num = 0;
        for (int i = 0; i < 9; i++) {
            if (i > WAND_CACHE.size() - 1) {
                break;
            }
            ItemStack item = new ItemStack(Material.STICK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + WAND_CACHE.get(i).getName());
            meta.setLore(WAND_CACHE.get(i).getLore());
            item.setItemMeta(meta);
            inv.setItem(num, item);
            num += 1;
        }
        player.openInventory(inv);
    }

    public static WandsPlugin getInst() {
        return INST;
    }

    public static Method getWandMethod(ItemStack itemInHand) {
        final ItemMeta itemInHandMeta = itemInHand.getItemMeta();
        if (Material.STICK.equals(itemInHand.getType())) {
            Wand.Meta meta = Iterables.find(WAND_CACHE, new Predicate<Wand.Meta>() {
                @Override
                public boolean apply(Wand.Meta meta) {
                    return meta.getName().equals(ChatColor.stripColor(itemInHandMeta.getDisplayName())) &&
                            meta.getLore().equals(meta.getLore());
                }
            }, null);
            if (meta != null) {
                return meta.getMethod();
            }
        }
        return null;
    }

    @Wand(name = "EnderWand", lore = "Shoots an EnderPearl.")
    public void enderWand(Player player) {
        EnderPearl ep = player.launchProjectile(EnderPearl.class);
        ep.setVelocity(player.getLocation().getDirection().multiply(3));
        ep.setShooter(player);
    }

    @Wand(name = "WitherWand", lore = "Shoots an WitherSkull.")
    public void witherWand(Player player) {
        WitherSkull ws = player.launchProjectile(WitherSkull.class);
        ws.setVelocity(player.getLocation().getDirection().multiply(2));
        ws.setIsIncendiary(true);
        ws.setYield(8F);
        ws.setShooter(player);
    }

    @Wand(name = "ExplosionWand", lore = "Shoots an fireball.")
    public void explosionWand(Player player) {
        // Handle location
        Location fireballLocation = player.getEyeLocation();
        Vector victor = fireballLocation.getDirection().multiply(2);
        fireballLocation.add(victor);

        Fireball fireball = player.getWorld().spawn(fireballLocation, Fireball.class);
        fireball.setYield(8F);
        fireball.setIsIncendiary(false);
        fireball.setShooter(player);
    }

    @Wand(name = "SplashWand", lore = "Shoots a some water.")
    public void splashWand(Player player) {
        // Handle location
        Location splashLocation = player.getEyeLocation();
        Vector victor = splashLocation.getDirection().multiply(2);
        splashLocation.add(victor);

        player.getWorld().spawnFallingBlock(splashLocation, Material.WATER.getId(), (byte) 0);
    }
}