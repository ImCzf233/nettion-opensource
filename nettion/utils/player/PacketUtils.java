package nettion.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtils {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static void sendPacketNoEvent(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void sendPacket(Packet<?> packet, boolean silent) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(packet, silent);
        }
    }

    public static void sendPacketWithoutEvent(final Packet<?> packet) {
        mc.getNetHandler().addToSendQueueWithoutEvent(packet);
    }

    public static void sendPacket(Packet packet) {
        sendPacket(packet, false);
    }

    public static class TimedPacket {
        private final Packet<?> packet;
        private final long time;

        public TimedPacket(Packet<?> packet, long time) {
            this.packet = packet;
            this.time = time;
        }

        public Packet<?> getPacket() {
            return this.packet;
        }

        public long getTime() {
            return this.time;
        }
    }
}
