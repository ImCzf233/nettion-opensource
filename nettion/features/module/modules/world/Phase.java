/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.world;

import nettion.event.events.world.EventPreUpdate;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovementInput;
import nettion.Nettion;
import nettion.event.EventHandler;
import nettion.features.value.values.Mode;
import nettion.event.events.world.EventPostUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.Helper;
import nettion.utils.player.PlayerUtils;


public class Phase
extends Module {
    private final Mode<Enum> mode = new Mode("mode", PhaseMode.values(), PhaseMode.Clip);
    final NetHandlerPlayClient netHandlerPlayClient = mc.getNetHandler();
    public Phase() {
        super("Phase", ModuleType.World);
        this.addValues(this.mode);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(mode.getValue());
    }

    @EventHandler
    private void onUpdate(EventPostUpdate e) {
        Phase Phase = (nettion.features.module.modules.world.Phase) Nettion.instance.getModuleManager().getModuleByClass(nettion.features.module.modules.world.Phase.class);
        if (this.mode.getValue() == PhaseMode.Vanilla) {
            if(!mc.thePlayer.onGround || !mc.thePlayer.isCollidedHorizontally) {
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0.5D, 0, 0.5D, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2D, mc.thePlayer.posZ, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0.5D, 0, 0.5D, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 0.5D, mc.thePlayer.posY, mc.thePlayer.posZ + 0.5D, true));
                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                final double x = -Math.sin(yaw) * 0.04D;
                final double z = Math.cos(yaw) * 0.04D;
                mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
            }
        } else if (this.mode.getValue() == PhaseMode.Spartan) {
            if(!mc.thePlayer.onGround || !mc.thePlayer.isCollidedHorizontally) {
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0.5D, 0, 0.5D, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.2D, mc.thePlayer.posZ, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0.5D, 0, 0.5D, true));
                netHandlerPlayClient.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 0.5D, mc.thePlayer.posY, mc.thePlayer.posZ + 0.5D, true));
                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                final double x = -Math.sin(yaw) * 0.04D;
                final double z = Math.cos(yaw) * 0.04D;
                mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
            }
        } else if (this.mode.getValue() == PhaseMode.Skip) {
            if(!mc.thePlayer.onGround || !mc.thePlayer.isCollidedHorizontally) {
                final double direction = PlayerUtils.getDirection();
                final double posX = -Math.sin(direction) * 0.3;
                final double posZ = Math.cos(direction) * 0.3;

                for(int i = 0; i < 3; ++i) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06, mc.thePlayer.posZ, true));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + posX * i, mc.thePlayer.posY, mc.thePlayer.posZ + posZ * i, true));
                }

                mc.thePlayer.setEntityBoundingBox(mc.thePlayer.getEntityBoundingBox().offset(posX, 0.0D, posZ));
                mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX + posX, mc.thePlayer.posY, mc.thePlayer.posZ + posZ);
            }
        } else if (this.mode.getValue() == PhaseMode.Clip) {
            mc.thePlayer.setPosition(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + (double)5, Helper.mc.thePlayer.posZ);
            Phase.setEnabled(false);
        } else if (mode.getValue() == PhaseMode.Silent) {
            double multiplier = 0.3;
            final double mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
            final double mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
            final double x = MovementInput.moveForward * multiplier * mx + MovementInput.moveStrafe * multiplier * mz;
            final double z = MovementInput.moveForward * multiplier * mz - MovementInput.moveStrafe * multiplier * mx;
            if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !PlayerUtils.isInsideBlock()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
                for (int i = 1; i < 10; ++i) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 8.988465674311579E307, mc.thePlayer.posZ, false));
                }
                mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
            }
        }
    }

    static enum PhaseMode {
        Vanilla,
        Spartan,
        Silent,
        Skip,
        Clip,
    }

}

