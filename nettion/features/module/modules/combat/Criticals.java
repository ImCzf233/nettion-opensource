package nettion.features.module.modules.combat;

import net.minecraft.network.play.client.C02PacketUseEntity;
import nettion.event.events.world.EventPacketSend;
import nettion.features.module.ModuleManager;
import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.network.play.client.C03PacketPlayer;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;

public class Criticals extends Module {
    public int random;
    public int attacks = 0;
    private final Delay stopwatch = new Delay();
    public final Mode<Enum<?>> mode = new Mode<>("Mode", critMode.values(), critMode.Packet);
    public final Numbers<Double> delay = new Numbers<>("Delay", 0.0, 0.0, 1000.0, 50.0);
    public final Option<Boolean> useC06 = new Option<>("UseC06Packet", false);

    public Criticals() {
        super("Criticals", ModuleType.Combat);
        addValues(mode, delay, useC06);
    }

    @Override
    public void onEnable() {
        attacks = 0;
    }

    @EventHandler
    public void onPacketSend(EventPacketSend event) {
        this.setSuffix(mode.getValue().name());
        if (!(event.getPacket() instanceof C02PacketUseEntity)) {
            return;
        }
        C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
        if (this.stopwatch.finished(((Number) delay.getValue()).longValue())) {
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                doCrit();
            }
            this.stopwatch.reset();
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (ModuleManager.getModuleByClass(Killaura.class).isEnabled() && Killaura.keepSprint.getValue() && Killaura.target != null) {
            if (this.stopwatch.finished(((Number) delay.getValue()).longValue())) {
                doCrit();
                this.stopwatch.reset();
            }
        }
    }

    private void doCrit() {
        if (mode.getValue() == critMode.Packet) {
            sendCriticalPacket(0.0625, true);
            sendCriticalPacket(0.0, false);
            sendCriticalPacket(1.1E-5, false);
            sendCriticalPacket(0.0, false);
        } else if (mode.getValue() == critMode.NCPPacket) {
            sendCriticalPacket(0.00001058293536, false);
            sendCriticalPacket(0.00000916580235, false);
            sendCriticalPacket(0.00000010371854, false);
        } else if (mode.getValue() == critMode.LowJump) {
            if(mc.thePlayer.onGround){
                mc.thePlayer.motionY = 0.16;
            }
        } else if (mode.getValue() == critMode.Vulcan) {
            attacks++;
            if (attacks > 7) {
                sendCriticalPacket(0.16477328182606651, false);
                sendCriticalPacket(0.08307781780646721, false);
                sendCriticalPacket(0.0030162615090425808, false);
                attacks = 0;
            }
        } else if (mode.getValue() == critMode.MiPacket) {
            sendCriticalPacket(0.0625, false);
            sendCriticalPacket(0.0, false);
        } else if (mode.getValue() == critMode.Matrix) {
            attacks++;
            if (attacks > 3) {
                sendCriticalPacket(0.0825080378093, false);
                sendCriticalPacket(0.023243243674, false);
                sendCriticalPacket(0.0215634532004, false);
                sendCriticalPacket(0.00150000001304, false);
                attacks = 0;
            }
        }
        mc.thePlayer.onCriticalHit(Killaura.target);
    }

    private void sendCriticalPacket(Double yOffset, Boolean ground) {
        double x = mc.thePlayer.posX + 0.0;
        double y = mc.thePlayer.posY + yOffset;
        double z = mc.thePlayer.posZ + 0.0;
        if (useC06.getValue()) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, ground));
        } else {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        }
    }

    public static class Delay {
        private long millis;

        public Delay() {
            this.reset();
        }

        public boolean finished(long delay) {
            return System.currentTimeMillis() - delay >= this.millis;
        }

        public void reset() {
            this.millis = System.currentTimeMillis();
        }
    }

    enum critMode {
        Packet,
        Matrix,
        Vulcan,
        MiPacket,
        NCPPacket,
        LowJump,
        Visual,
    }
}