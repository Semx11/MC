package me.semx11.minecars.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.semx11.minecars.MineCars;
import me.semx11.minecars.nms.ControllableArmorStand;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.Vector3f;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

public class CarHandler {

    private double carRotation = 0;
    private Boolean teleportChange = false;
    private double turningRadius = 0;
    private double steeringTime = 0;
    private ControllableArmorStand controlAS;
    private float velocity;
    private EntityArmorStand frontLeft;
    private EntityArmorStand frontRight;
    private EntityArmorStand rearLeft;
    private EntityArmorStand rearRight;
    private EntityArmorStand carStand;
    private ArmorStand spigotFL;
    private ArmorStand spigotFR;

    private static double easeInOutQuad(double t, double b, double c, double d) {
        t /= d / 2;
        if (t < 1) {
            return c / 2 * t * t + b;
        }
        t--;
        return -c / 2 * (t * (t - 2) - 1) + b;
    }

    public CarHandler(ControllableArmorStand controllableArmorStand) {
        controlAS = controllableArmorStand;
        velocity = 0.0F;
        controlAS.enderTeleportTo(controlAS.locX, Math.round(controlAS.locY), controlAS.locZ);

        for (Entity e : controlAS.getBukkitEntity().getNearbyEntities(1.0, 1.0, 1.0)) {
            if (e.getCustomName() != null) {
                if (e.getCustomName().startsWith("Wheel") && e.getType()
                        .equals(EntityType.ARMOR_STAND)) {
                    e.remove();
                }
            }
        }

        CraftWorld cw = (CraftWorld) Bukkit.getWorlds().get(0);
        World w = Bukkit.getWorlds().get(0);
        List<EntityArmorStand> wheels = new ArrayList<>();
        List<ArmorStand> spigotWheels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ArmorStand as = (ArmorStand) w
                    .spawnEntity(new Location(w, controlAS.locX, controlAS.locY, controlAS.locZ),
                            EntityType.ARMOR_STAND);
            wheels.add(((CraftArmorStand) as).getHandle());
            spigotWheels.add(as);
        }

        List<String> wheelNames = Arrays
                .asList("WheelFL", "WheelFR", "WheelRL", "WheelRR", "CarModel");
        int count = 0;

        for (EntityArmorStand wheel : wheels) {
            wheel.setBodyPose(new Vector3f(0.0F, 0.0F, 0.0F));
            wheel.setHeadPose(new Vector3f(0.0F, 0.0F, 0.0F));
            wheel.setRightArmPose(new Vector3f(0.0F, 0.0F, 0.0F));
            wheel.setRightLegPose(new Vector3f(0.0F, 0.0F, 0.0F));
            wheel.setLeftArmPose(new Vector3f(0.0F, 0.0F, 0.0F));
            wheel.setLeftLegPose(new Vector3f(0.0F, 0.0F, 0.0F));
            wheel.setPositionRotation(0.0, 0.0, 0.0, 0, 0);
            wheel.setInvisible(true);
            wheel.setCustomName(wheelNames.get(count));
            count++;
            cw.getHandle().addEntity(wheel);
        }
        frontLeft = wheels.get(0);
        frontRight = wheels.get(1);
        rearLeft = wheels.get(2);
        rearRight = wheels.get(3);
        carStand = wheels.get(4);

        spigotFL = spigotWheels.get(0);
        spigotFR = spigotWheels.get(1);

        frontLeft.yaw = 90F;
        frontRight.yaw = -90F;
        rearLeft.yaw = 90F;
        rearRight.yaw = -90F;

        for (ArmorStand wheel : spigotWheels) {
            wheel.setHeadPose(new EulerAngle(-0.5 * Math.PI, 0.0, 0.0));
        }

        MineCars.getInstance().setNoGravity(wheels);

        Bukkit.getServer().getScheduler().runTaskLater(MineCars.getInstance(), () -> {
            teleportChange = true;
            carStand.enderTeleportTo(carStand.locX, carStand.locY + 1, carStand.locZ);
            NBTTagCompound tag = new NBTTagCompound();
            controlAS.c(tag);
            tag.setBoolean("NoGravity", true);
            EntityLiving el = controlAS;
            el.a(tag);
        }, 40L);

    }

    public void moveCar(float sideMot, float forMot, CraftEntity player) {

        //-(e^( v * 0.249–2.3))+4.2

        if (forMot > 0 && velocity >= 0) {
            //log("Car: Accelerate");
            velocity += (-1 * (Math.exp(velocity * 0.249 - 2.3)) + 4.2) / 20;
        } else if (forMot == 0 && velocity > 0) {
            //log("Car: Idle but moving forward");
            velocity -= (-1 * (Math.exp(Math.abs(velocity) * 0.125 - 2.3)) + 2) / 20;
            if (velocity < 0) {
                velocity = 0;
            }
        } else if (forMot == 0 && velocity < 0) {
            //log("Car: Idle but moving backwards");
            velocity += (-1 * (Math.exp(velocity * 0.125 - 2.3)) + 1.5) / 20;
            if (velocity > 0) {
                velocity = 0;
            }
        } else if (forMot < 0 && velocity > 0) {
            //log("Car: Brake");
            velocity -= (-1 * (Math.exp(Math.abs(velocity) * 0.249 - 2.3)) + 6.2) / 20;
        } else if (forMot < 0 && velocity <= 0) {
            //log("Car: Reverse");
            velocity -= (-1 * (Math.exp(Math.abs(velocity) * 0.5 - 2.3)) + 3) / 20;
        } else if (forMot > 0 && velocity < 0) {
            //log("Car: Reverse Brake");
            //velocity += (-1 * (Math.exp(velocity * 0.249 - 2.3)) + 4.2) / 20;
            //velocity += (-1 * (Math.exp(velocity * 0.5 - 2.3)) + 3) / 20;
            velocity += (-1 * (Math.exp(velocity * 0.249 - 2.3)) + 6.2) / 20;
        }

        double maxAngle = 30;
        double angle = 0;
        if (sideMot > 0) {
            //A
            if (steeringTime >= 10) {
                if (steeringTime < 20) {
                    steeringTime++;
                }
                angle = easeInOutQuad(steeringTime - 10, 0, maxAngle, 10);
            } else {
                steeringTime++;
                angle = -easeInOutQuad(10 - steeringTime, 0, maxAngle, 10);
            }
        } else if (sideMot < 0) {
            //D
            if (steeringTime <= 10) {
                if (steeringTime > 0) {
                    steeringTime--;
                }
                angle = -easeInOutQuad(10 - steeringTime, 0, maxAngle, 10);
            } else {
                steeringTime--;
                angle = easeInOutQuad(steeringTime - 10, 0, maxAngle, 10);
            }
        } else if (sideMot == 0) {
            //Idle
            if (steeringTime > 10) {
                steeringTime--;
                angle = easeInOutQuad(steeringTime - 10, 0, maxAngle, 10);
            } else if (steeringTime < 10) {
                steeringTime++;
                angle = -easeInOutQuad(10 - steeringTime, 0, maxAngle, 10);
            }
        }

        //log("x: " + controlAS.locX + ", y: " + controlAS.locY + ", z: " + controlAS.locZ + ". World: " + controlAS.getWorld().getWorld().getName());

        if (angle != 0) {
            turningRadius = 2 * Math.tan((90 - angle) / 180 * Math.PI);
            if (turningRadius > 0) {
                spigotFR.setHeadPose(new EulerAngle(-0.5 * Math.PI,
                        Math.atan((turningRadius + 0.75) / 2) - 0.5 * Math.PI, 0.0));
                spigotFL.setHeadPose(new EulerAngle(-0.5 * Math.PI,
                        Math.atan((turningRadius - 0.75) / 2) - 0.5 * Math.PI, 0.0));
            } else {
                spigotFR.setHeadPose(new EulerAngle(-0.5 * Math.PI,
                        Math.atan((turningRadius + 0.75) / 2) + 0.5 * Math.PI, 0.0));
                spigotFL.setHeadPose(new EulerAngle(-0.5 * Math.PI,
                        Math.atan((turningRadius - 0.75) / 2) + 0.5 * Math.PI, 0.0));
            }
        } else {
            spigotFR.setHeadPose(new EulerAngle(-0.5 * Math.PI, 0.0, 0.0));
            spigotFL.setHeadPose(new EulerAngle(-0.5 * Math.PI, 0.0, 0.0));
        }

        Location asLoc;
        if (!teleportChange) {
            controlAS.enderTeleportTo(controlAS.locX, controlAS.locY,
                    controlAS.locZ + velocity / 20);

            frontLeft.enderTeleportTo(controlAS.locX + 0.75, controlAS.locY - 0.1,
                    controlAS.locZ + 1);
            frontRight.enderTeleportTo(controlAS.locX - 0.75, controlAS.locY - 0.1,
                    controlAS.locZ + 1);
            rearLeft.enderTeleportTo(controlAS.locX + 0.75, controlAS.locY - 0.1,
                    controlAS.locZ - 1);
            rearRight.enderTeleportTo(controlAS.locX - 0.75, controlAS.locY - 0.1,
                    controlAS.locZ - 1);
            carStand.enderTeleportTo(controlAS.locX, controlAS.locY - 0.1, controlAS.locZ - 1);

            asLoc = new Location(controlAS.getWorld().getWorld(), controlAS.locX, controlAS.locY,
                    controlAS.locZ, controlAS.pitch, controlAS.yaw);
            if (controlAS.locY + 1 > controlAS.getWorld().getWorld().getHighestBlockYAt(asLoc)) {
                controlAS.enderTeleportTo(controlAS.locX, controlAS.locY - 0.1, controlAS.locZ);
            }

        } else {

            //carStand.enderTeleportTo(carStand.locX, carStand.locY, carStand.locZ + velocity / 20);

            carStand.enderTeleportTo(
                    carStand.locX + (velocity / 20) * Math.cos(0.5 * Math.PI - carRotation),
                    carStand.locY,
                    carStand.locZ + (velocity / 20) * Math.sin(0.5 * Math.PI - carRotation));

            controlAS.enderTeleportTo(carStand.locX + Math.cos(-carRotation + 0.5 * Math.PI),
                    carStand.locY, carStand.locZ + Math.sin(-carRotation + 0.5 * Math.PI));
            frontRight.enderTeleportTo(
                    carStand.locX + Math.cos(-carRotation + 0.6142002512 * Math.PI) * 0.25 * Math
                            .sqrt(73), carStand.locY,
                    carStand.locZ + Math.sin(-carRotation + 0.6142002512 * Math.PI) * 0.25 * Math
                            .sqrt(73));
            frontLeft.enderTeleportTo(
                    carStand.locX + Math.cos(-carRotation + 0.3857997488 * Math.PI) * 0.25 * Math
                            .sqrt(73), carStand.locY,
                    carStand.locZ + Math.sin(-carRotation + 0.3857997488 * Math.PI) * 0.25 * Math
                            .sqrt(73));
            rearRight.enderTeleportTo(carStand.locX + Math.cos(-carRotation + Math.PI) * 0.75,
                    carStand.locY, carStand.locZ + Math.sin(-carRotation + Math.PI) * 0.75);
            rearLeft.enderTeleportTo(carStand.locX + Math.cos(-carRotation) * 0.75, carStand.locY,
                    carStand.locZ + Math.sin(-carRotation) * 0.75);

            controlAS.yaw = (float) (360 - Math.toDegrees(carRotation));
            frontRight.yaw = controlAS.yaw - 90;
            frontLeft.yaw = controlAS.yaw + 90;
            rearRight.yaw = controlAS.yaw - 90;
            rearLeft.yaw = controlAS.yaw + 90;

            /*
            EulerAngle rr = spigotRR.getHeadPose();
            spigotRR.setHeadPose(new EulerAngle(rr.getX(), rr.getY(), rr.getZ() - (2 * velocity / 0.8) / 20));
            EulerAngle rl = spigotRL.getHeadPose();
            spigotRL.setHeadPose(new EulerAngle(rl.getX(), rl.getY(), rl.getZ() + (2 * velocity / 0.8) / 20));
            */
//            EulerAngle fr = spigotFR.getHeadPose();
//            spigotFR.setHeadPose(new EulerAngle(fr.getX(), fr.getY(), fr.getZ() - (2 * velocity / 0.8) / 20));
//            EulerAngle fl = spigotFL.getHeadPose();
//            spigotFL.setHeadPose(new EulerAngle(fl.getX(), fl.getY(), fl.getZ() - (2 * velocity / 0.8) / 20));

            if (angle != 0) {
                carRotation += (velocity / 20) / turningRadius;
            }

            asLoc = new Location(carStand.getWorld().getWorld(), carStand.locX, carStand.locY,
                    carStand.locZ, carStand.pitch, carStand.yaw);
            if (carStand.locY + 1 > carStand.getWorld().getWorld().getHighestBlockYAt(asLoc)) {
                carStand.enderTeleportTo(carStand.locX, carStand.locY - 0.1, carStand.locZ);
            }
        }
    }


}
