package me.semx11.minecars.nms;

import me.semx11.minecars.runnable.CarHandler;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.World;
import org.bukkit.entity.Entity;

public class ControllableArmorStand extends EntityArmorStand {

    public float acceleration = 4.0F; //acceleration in blocks/second
    public float dragAcc = 3.0F;
    public float brakeAcc = 2.5F * acceleration;
    public float velocity = 0.0F;
    private CarHandler carHandler = null;
    private Entity e;

    public ControllableArmorStand(World world) {
        super(world);
        this.setMarker(false);
        this.setCustomName("Car");
    }

    @Override
    public void g(float sideMot, float forMot) {

        if (carHandler == null) {
            carHandler = new CarHandler(this);
        }

        if (cK()) {
            EntityLiving entityliving = (EntityLiving) bt();
            carHandler.moveCar(entityliving.bd, entityliving.be, entityliving.getBukkitEntity());

            //super.g(sideMot * 0.5F, forMot);
            super.g(0.0F, 0.0F);
        } else {
            carHandler.moveCar(0.0F, 0.0F, null);
            //super.g(sideMot * 0.5F, forMot);
            super.g(0.0F, 0.0F);
        }
    }

    public net.minecraft.server.v1_9_R1.Entity bt() {
        return bu().isEmpty() ? null : bu().get(0);
    }

    private boolean cK() {
        net.minecraft.server.v1_9_R1.Entity entity = bt();
        return entity instanceof EntityLiving;
    }

}
