package nettion.features.module.modules.world;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketReceive;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class FakeLag extends Module {
    private final Numbers<Double> delay = new Numbers<>("Delay", 5.0, 2.0, 30.0, 1.0);
    private final ArrayList<Packet> packets = new ArrayList<>();
    private final ArrayList<Vector3d> locations = new ArrayList<>();
    private boolean isEnabled;

    public FakeLag() {
        super("FakeLag", ModuleType.World);
        addValues(delay);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(delay.getValue());
        for (int i = 0; i < mc.theWorld.getLoadedEntityList().size(); i++) {
            Entity ent = mc.theWorld.getLoadedEntityList().get(i);
            if (!(ent instanceof EntityPlayer)) continue;
            if (ent.getName().contains(mc.thePlayer.getName()) && ent != mc.thePlayer) {
                mc.theWorld.removeEntity(ent);
            }
        }
    }

    @EventHandler
    private void onPacket(EventPacketReceive event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.ticksExisted % delay.getValue() != 0) {
            if (!isEnabled) {
                isEnabled = true;
                if (mc.gameSettings.thirdPersonView == 0) return;
                if (mc.theWorld == null) return;
                final EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
                entityOtherPlayerMP.inventory = mc.thePlayer.inventory;
                entityOtherPlayerMP.inventoryContainer = mc.thePlayer.inventoryContainer;
                entityOtherPlayerMP.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                entityOtherPlayerMP.rotationYawHead = mc.thePlayer.rotationYawHead;
                entityOtherPlayerMP.setSneaking(mc.thePlayer.isSneaking());
                if (hasMoved()) {
                    mc.theWorld.addEntityToWorld(-13376969, entityOtherPlayerMP);
                }
                packets.clear();
            }
        } else {
            isEnabled = false;
            if (mc.theWorld == null) return;
            mc.theWorld.removeEntityFromWorld(-13376969);
            packets.clear();
            locations.clear();
        }
        if (isEnabled) {
            if (hasMoved()) {
                locations.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }
            packets.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        final EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        entityOtherPlayerMP.inventory = mc.thePlayer.inventory;
        entityOtherPlayerMP.inventoryContainer = mc.thePlayer.inventoryContainer;
        entityOtherPlayerMP.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        entityOtherPlayerMP.rotationYawHead = mc.thePlayer.rotationYawHead;
        entityOtherPlayerMP.setSneaking(mc.thePlayer.isSneaking());
        mc.theWorld.addEntityToWorld(-13376969, entityOtherPlayerMP);
        packets.clear();
    }

    @Override
    public void onDisable() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        isEnabled = false;
        mc.theWorld.removeEntityFromWorld(-13376969);
        packets.clear();
        locations.clear();
    }

    private boolean hasMoved() {
        return mc.thePlayer.posX != mc.thePlayer.prevPosX || mc.thePlayer.posY != mc.thePlayer.prevPosY || mc.thePlayer.posZ != mc.thePlayer.prevPosZ;
    }
}
