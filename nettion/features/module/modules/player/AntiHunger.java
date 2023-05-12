package nettion.features.module.modules.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketSend;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class AntiHunger extends Module {
    public AntiHunger() {
        super("AntiHunger", ModuleType.Player);
    }

    @EventHandler
    private void onPacketSend(EventPacketSend event) {
        if (EventPacketSend.packet instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) EventPacketSend.packet;
            packet.onGround = false;
        }
    }
}
