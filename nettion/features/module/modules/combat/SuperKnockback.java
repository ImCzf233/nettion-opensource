package nettion.features.module.modules.combat;

import nettion.event.EventHandler;
import nettion.event.events.world.EventAttack;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import nettion.utils.player.PacketUtils;

public class SuperKnockback extends Module {
    public SuperKnockback() {
        super("SuperKnockback", ModuleType.Combat);
    }

    @EventHandler
    private void onUpdate(EventAttack event) {
        if(event.getTargetEntity() != null) {
            if (mc.thePlayer.isSprinting())
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

            PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
    }
}
