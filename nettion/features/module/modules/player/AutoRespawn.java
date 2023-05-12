package nettion.features.module.modules.player;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", ModuleType.Player);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0) {
            mc.thePlayer.respawnPlayer();
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    }
}
