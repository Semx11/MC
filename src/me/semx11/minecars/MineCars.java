package me.semx11.minecars;

import me.semx11.minecars.commands.SpawnCar;
import me.semx11.minecars.nms.ControllableArmorStand;
import me.semx11.minecars.nms.RegisterEntities;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MineCars extends JavaPlugin implements Listener {

    private static MineCars plugin;

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();
        plugin = this;
        RegisterEntities.registerCAS();
    }

    @Override
    public void onDisable() {

    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    private void registerCommands() {
        getCommand("SpawnCar").setExecutor(new SpawnCar(this));
    }

    public static ControllableArmorStand spawnControllableArmorStand(Location l) {
        CraftWorld cWorld = ((CraftWorld) l.getWorld());
        net.minecraft.server.v1_9_R1.World world = cWorld.getHandle();
        ControllableArmorStand car = new ControllableArmorStand(world);

        car.setBodyPose(new Vector3f(0.0F, 0.0F, 0.0F));
        car.setHeadPose(new Vector3f(0.0F, 0.0F, 0.0F));
        car.setRightArmPose(new Vector3f(0.0F, 0.0F, 0.0F));
        car.setRightLegPose(new Vector3f(0.0F, 0.0F, 0.0F));
        car.setLeftArmPose(new Vector3f(0.0F, 0.0F, 0.0F));
        car.setLeftLegPose(new Vector3f(0.0F, 0.0F, 0.0F));
        car.setPositionRotation(0.0, 0.0, 0.0, 0, 0);
        car.setGravity(false);
        car.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);

        ((CraftWorld) l.getWorld()).getHandle().addEntity(car, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return car;
    }

    @EventHandler
    public void onPlayerClickArmorStand(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            e.getRightClicked().setPassenger(e.getPlayer());
        }
    }

    public void setNoGravity(List<EntityArmorStand> armorStandList) {
        getServer().getScheduler().runTaskLater(this, () -> {
            for (EntityArmorStand entityArmorStand : armorStandList) {
                NBTTagCompound tag = new NBTTagCompound();
                entityArmorStand.c(tag);
                tag.setBoolean("NoGravity", true);
                EntityLiving el = (EntityLiving) entityArmorStand;
                el.a(tag);
                if (!entityArmorStand.getCustomName().equals("CarModel")) {
                    ((ArmorStand) entityArmorStand.getBukkitEntity()).setHelmet(new ItemStack(Material.SAPLING, 1, (short) 5));
                }
            }

        }, 40L);
    }

    public static MineCars getInstance() {
        return plugin;
    }

}
