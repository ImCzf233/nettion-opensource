/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.movement;

import nettion.event.EventHandler;
import nettion.event.events.misc.EventCollideWithBlock;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class Jesus
extends Module {
    public Jesus() {
        super("Jesus", ModuleType.Movement);
    }

    private boolean canJeboos() {
        if (!(mc.thePlayer.fallDistance >= 3.0f || mc.gameSettings.keyBindJump.isPressed() || BlockHelper.isInLiquid() || mc.thePlayer.isSneaking())) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPre(EventPreUpdate e) {
        if (BlockHelper.isInLiquid() && !mc.thePlayer.isSneaking() && !mc.gameSettings.keyBindJump.isPressed()) {
            mc.thePlayer.motionY = 0.05;
            mc.thePlayer.onGround = true;
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer && this.canJeboos() && BlockHelper.isOnLiquid()) {
            C03PacketPlayer packet = (C03PacketPlayer)e.getPacket();
            packet.y = mc.thePlayer.ticksExisted % 2 == 0 ? packet.y + 0.01 : packet.y - 0.01;
        }
    }

    @EventHandler
    public void onBB(EventCollideWithBlock e) {
        if (e.getBlock() instanceof BlockLiquid && this.canJeboos()) {
            e.setBoundingBox(new AxisAlignedBB(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), (double)e.getPos().getX() + 1.0, (double)e.getPos().getY() + 1.0, (double)e.getPos().getZ() + 1.0));
        }
    }
}

