package nettion.features.module.modules.player;

import nettion.event.EventHandler;
import nettion.features.value.values.Mode;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.PacketUtils;

public class Kick extends Module {
    private Mode mode = new Mode("mode", modes.values(), modes.quit);
    public Kick() {
        super("Kick", ModuleType.Player);
        addValues(mode);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (mode.getValue() == modes.quit) {
            mc.theWorld.sendQuittingDisconnectingPacket();
            this.setEnabled(false);
        } else if (mode.getValue() == modes.clientsidequit) {
            PacketUtils.sendPacketNoEvent(new S40PacketDisconnect(new ChatComponentText("Disconnected")));
            this.setEnabled(false);
        } else if (mode.getValue() == modes.invalidpacket) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !mc.thePlayer.onGround));
            this.setEnabled(false);
        } else if (mode.getValue() == modes.selfhurt) {
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK));
            this.setEnabled(false);
        }
    }

    enum modes {
        quit,
        clientsidequit,
        invalidpacket,
        selfhurt,
    }
}
