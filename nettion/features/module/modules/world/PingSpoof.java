package nettion.features.module.modules.world;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.optifine.util.MathUtils;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketSend;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.utils.time.TimerUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingSpoof
extends Module {
    private final Numbers<Double> delay = new Numbers<>("Delay(ms)", 750.0, 300.0, 3000.0, 10.0);
    private final List<Packet> packetList = new CopyOnWriteArrayList<>();
    private final TimerUtils timer = new TimerUtils();

    public PingSpoof() {
        super("PingSpoof", ModuleType.World);
        addValues(delay);
    }

    @EventHandler
    private void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C00PacketKeepAlive && mc.thePlayer.isEntityAlive()) {
            this.packetList.add(e.getPacket());
            e.setCancelled(true);
        }
        if (this.timer.hasReached(delay.getValue())) {
            if (!this.packetList.isEmpty()) {
                int i = 0;
                double totalPackets = MathUtils.getIncremental(Math.random() * 10.0, 1.0);
                for (Packet packet : this.packetList) {
                    if ((double)i >= totalPackets) continue;
                    ++i;
                    mc.getNetHandler().getNetworkManager().sendPacket(packet);
                    this.packetList.remove(packet);
                }
            }
            mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(10000));
            this.timer.reset();
        }
    }
}
