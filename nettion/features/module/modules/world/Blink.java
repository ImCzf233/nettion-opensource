package nettion.features.module.modules.world;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.utils.time.TimerUtil;
import nettion.utils.time.TimerUtils;

public class Blink
extends Module {
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    private EntityOtherPlayerMP fakePlayer;
    private boolean disableLogger;
    private final LinkedList<double[]> positions = new LinkedList<>();
    //private final TimerUtils pulseTimer = new TimerUtils();

    public Blink() {
        super("Blink", ModuleType.World);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onEnable() {
        if (mc.thePlayer == null) {
            return;
        }
        this.fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        this.fakePlayer.clonePlayer(mc.thePlayer, true);
        this.fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        this.fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-1337, this.fakePlayer);

        synchronized (this.positions) {
            double[] dArray = new double[3];
            dArray[0] = mc.thePlayer.posX;
            dArray[1] = mc.thePlayer.getEntityBoundingBox().minY + (double)(mc.thePlayer.getEyeHeight() / 2.0f);
            dArray[2] = mc.thePlayer.posZ;
            this.positions.add(dArray);
            double[] dArray2 = new double[3];
            dArray2[0] = mc.thePlayer.posX;
            dArray2[1] = mc.thePlayer.getEntityBoundingBox().minY;
            dArray2[2] = mc.thePlayer.posZ;
            this.positions.add(dArray2);
        }
        //this.pulseTimer.reset();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) {
            return;
        }
        this.blink();
        if (this.fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.fakePlayer = null;
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend event) {
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer == null || this.disableLogger) {
            return;
        }
        if (packet instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
        if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C0APacketAnimation || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity) {
            event.setCancelled(true);
            this.packets.add(packet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        synchronized (this.positions) {
            double[] dArray = new double[3];
            dArray[0] = mc.thePlayer.posX;
            dArray[1] = mc.thePlayer.getEntityBoundingBox().minY;
            dArray[2] = mc.thePlayer.posZ;
            this.positions.add(dArray);
        }
        /* *
        if (this.pulseValue.getValue() && TimerUtil.hasTimePassed(ThreadLocalRandom.current().nextLong(this.pulseDelayValue.getValue().longValue(), this.pulseDelayValue.getValue().longValue()))) {
            this.blink();
            this.pulseTimer.reset();
        }
        * */
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void blink() {
        try {
            this.disableLogger = true;
            while (!this.packets.isEmpty()) {
                mc.getNetHandler().getNetworkManager().sendPacket(this.packets.take());
            }
            this.disableLogger = false;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.disableLogger = false;
        }
        synchronized (this.positions) {
            this.positions.clear();
        }
    }
}

