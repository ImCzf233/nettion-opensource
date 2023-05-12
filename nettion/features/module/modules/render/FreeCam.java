/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.misc.EventCollideWithBlock;
import nettion.event.events.world.EventPacketReceive;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.value.values.Numbers;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FreeCam
extends Module {
    public static Numbers<Double> speed = new Numbers<>("Speed", 0.5, 0.1, 3.0, 0.1);
    private EntityOtherPlayerMP copy;
    private double x;
    private double y;
    private double z;

    public FreeCam() {
        super("FreeCam", ModuleType.Render);
        addValues(speed);
    }

    @Override
    public void onEnable() {
        this.copy = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        this.copy.clonePlayer(mc.thePlayer, true);
        this.copy.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        this.copy.rotationYawHead = mc.thePlayer.rotationYawHead;
        this.copy.setEntityId(-1337);
        this.copy.setSneaking(mc.thePlayer.isSneaking());
        mc.theWorld.addEntityToWorld(this.copy.getEntityId(), this.copy);
        this.x = mc.thePlayer.posX;
        this.y = mc.thePlayer.posY;
        this.z = mc.thePlayer.posZ;
    }

    @EventHandler
    private void onPreMotion(EventPreUpdate e) {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY += speed.getValue();
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY -= speed.getValue();
        }
        if (mc.thePlayer.isMoving()) {
            mc.thePlayer.setSpeed(speed.getValue());
        }
        mc.thePlayer.noClip = true;
        mc.thePlayer.capabilities.setFlySpeed(0.1f);
        e.setCancelled(true);
    }

    @EventHandler
    private void onPacketSend(EventPacketReceive e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBB(EventCollideWithBlock e) {
        e.setBoundingBox(null);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSpeed(0.0);
        mc.thePlayer.setLocationAndAngles(this.copy.posX, this.copy.posY, this.copy.posZ, this.copy.rotationYaw, this.copy.rotationPitch);
        mc.thePlayer.rotationYawHead = this.copy.rotationYawHead;
        mc.theWorld.removeEntityFromWorld(this.copy.getEntityId());
        mc.thePlayer.setSneaking(this.copy.isSneaking());
        this.copy = null;
        mc.renderGlobal.loadRenderers();
        mc.thePlayer.setPosition(this.x, this.y, this.z);
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.01, mc.thePlayer.posZ, mc.thePlayer.onGround));
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.noClip = false;
        mc.theWorld.removeEntityFromWorld(-1);
    }
}

