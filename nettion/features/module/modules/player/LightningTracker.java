package nettion.features.module.modules.player;

import net.minecraft.network.play.server.S29PacketSoundEffect;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketReceive;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;

public class LightningTracker extends Module {
    public LightningTracker() {
        super("LightningTracker", ModuleType.Player);
    }

    @EventHandler
    private void onPacketReceive(EventPacketReceive e) {
        if (e.getPacket() instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundPacket = ((S29PacketSoundEffect) e.getPacket());
            if (soundPacket.getSoundName().equals("ambient.weather.thunder")) {
                NotificationManager.post(NotificationType.INFO, "Info", String.format("Lightning detected at (%s, %s, %s)", (int) soundPacket.getX(), (int) soundPacket.getY(), (int) soundPacket.getZ()), 3);
            }
        }
    }
}
