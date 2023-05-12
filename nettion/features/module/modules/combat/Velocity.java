package nettion.features.module.modules.combat;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import nettion.event.events.world.*;
import nettion.features.module.ModuleManager;
import nettion.features.module.modules.movement.LongJump;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.event.EventHandler;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import nettion.utils.time.TimerUtils;

public class Velocity extends Module {
    private final Mode mode = new Mode<>("mode", modes.values(), modes.Custom);
    private final Numbers<Double> horizontal = new Numbers<>("Horizontal", -0.2, -1.0, 1.0, 0.1);
    private final Numbers<Double> vertically = new Numbers<>("Vertically", 1.0, 0.0, 1.0, 0.1);
    TimerUtils timer = new TimerUtils();
    boolean hurt = false;
    public Velocity() {
        super("Velocity", ModuleType.Combat);
        this.addValues(mode, horizontal, vertically);
    }

    @EventHandler
    private void onPacket(EventPacketReceive event) {
        if (this.mode.getValue() == modes.Custom) {
            if (ModuleManager.getModuleByClass(LongJump.class).isEnabled()) {
                return;
            }
            if (event.getPacket() instanceof S12PacketEntityVelocity || event.getPacket() instanceof S27PacketExplosion) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
                if(horizontal.getValue() == 0f && vertically.getValue() == 0f) {
                    event.setCancelled(true);
                } else {
                    packet.motionX = (int) (packet.motionX * horizontal.getValue());
                    packet.motionY = (int) (packet.motionY * vertically.getValue());
                    packet.motionZ = (int) (packet.motionZ * horizontal.getValue());
                }
            }
        } else if (mode.getValue() == modes.AAC4) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof S12PacketEntityVelocity) {
                if (mc.thePlayer == null){
                    return;
                }
                timer.reset();
                hurt = true;
            }
            if (packet instanceof S06PacketUpdateHealth) {
                event.cancel();
            }
            if (packet instanceof S27PacketExplosion) {
                event.cancel();
            }
        } else if (mode.getValue() == modes.Reverse) {
            if (ModuleManager.getModuleByClass(LongJump.class).isEnabled()) {
                return;
            }
            if (event.getPacket() instanceof S12PacketEntityVelocity || event.getPacket() instanceof S27PacketExplosion) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
                packet.motionX = packet.motionX * -1;
                packet.motionZ = packet.motionZ * -1;
            }
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (mode.getValue() == modes.Custom) {
            this.setSuffix(horizontal.getValue()*100 + "% " + vertically.getValue()*100 + "%");
        } else {
            this.setSuffix(mode.getValue());
        }
        if (mode.getValue() == modes.AAC4) {
            if (!mc.thePlayer.onGround) {
                if (mc.thePlayer.hurtTime != 0){
                    if (hurt) {
                        mc.gameSettings.keyBindForward.setPressed(true);
                        mc.thePlayer.speedInAir = 0.02F;
                        mc.thePlayer.motionX *= 0.6;
                        mc.thePlayer.motionZ *= 0.6;
                    }
                }
            } else{
                if (timer.hasTimePassed(80)) {
                    hurt = false;
                    mc.thePlayer.speedInAir = 0.02F;
                }
            }
        }
        if (mode.getValue() == modes.AAC5) {
            if (mc.thePlayer.hurtTime > 8) {
                mc.thePlayer.motionX *= 0.6;
                mc.thePlayer.motionZ *= 0.6;
            }
        }
    }

    enum modes {
        Custom,
        Reverse,
        AAC5,
        AAC4,
    }
}

