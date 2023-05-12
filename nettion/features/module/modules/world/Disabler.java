package nettion.features.module.modules.world;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import nettion.event.events.world.*;
import nettion.features.module.ModuleManager;
import nettion.features.module.modules.movement.LongJump;
import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;
import nettion.utils.player.PacketUtils;
import nettion.utils.time.TimerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Disabler extends Module {
    private final Mode<Enum<?>> mode = new Mode<>("mode", disMode.values(), disMode.Watchdog);
    private final Option<Boolean> clickBypass = new Option<>("Click Bypass", true);
    private final Option<Boolean> pingSpoof = new Option<>("Ping Spoof", false);
    private final Numbers<Double> pingDelay = new Numbers<>("Ping Delay", 400.0, 300.0, 800.0, 50.0);

    private final List<Packet<?>> packets = new ArrayList<>();
    private final TimerUtils timer = new TimerUtils(), timerClose = new TimerUtils();

    public Disabler() {
        super("Disabler", ModuleType.World);
        addValues(mode, clickBypass, pingSpoof, pingDelay);
    }

    enum disMode {
        Watchdog,
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == disMode.Watchdog) {
            packets.clear();
            timer.reset();
            timerClose.reset();
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == disMode.Watchdog) {
            resetDisabler();
        }
    }

    @EventHandler
    private void onWorldLoad(EventWorldLoad e) {
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        this.setSuffix(mode.getValue().name());
        if (mode.getValue() == disMode.Watchdog) {
            if (Boolean.FALSE.equals(pingSpoof.getValue())) return;
            if (!packets.isEmpty()) {
                if (timer.delay(needPreventAttack() ? (float) 4000.0 : (pingDelay.getValue().floatValue() - mc.getNetHandler().getPlayerInfo(mc.thePlayer.getName()).getResponseTime()))) {
                    resetDisabler();
                }
            }
        }
    }

    @EventHandler
    public void onPacketSend(EventPacketSend event) {
        if (mode.getValue() == disMode.Watchdog) {
            final Packet<?> packet = event.getPacket();
            if (mc.thePlayer.isMoving() && packet instanceof C0EPacketClickWindow && Boolean.TRUE.equals(clickBypass.getValue())) {
                event.setCancelled(true);
                //NotificationManager.post(NotificationType.WARNING, "Warning", "In Motion Click was cancelled to keep your safety", 2.5f);

                if (timerClose.delay(160)) {
                    PacketUtils.sendPacketNoEvent(new C0DPacketCloseWindow(0));
                    timerClose.reset();
                }
            }

            if ((mc.isIntegratedServerRunning()) && mc.thePlayer.isMoving()) {
                //NotificationManager.post(NotificationType.WARNING, "Warning", "In Motion Click was cancelled to keep your safety", 2.5f);
                event.setCancelled(true);
            }

            if (packet instanceof C0BPacketEntityAction) {
                final C0BPacketEntityAction pea = (C0BPacketEntityAction) packet;
                if (pea.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || pea.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    event.setCancelled(true);
                }
            }

            if (packet instanceof C16PacketClientStatus) {
                final C16PacketClientStatus pcs = (C16PacketClientStatus) packet;
                if (pcs.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT && Boolean.TRUE.equals(clickBypass.getValue())) {
                    event.setCancelled(true);
                }
            }

            if (packet instanceof C02PacketUseEntity) {
                final C02PacketUseEntity pue = (C02PacketUseEntity) packet;
                if (pue.getAction() == C02PacketUseEntity.Action.ATTACK && needPreventAttack()) {
                    event.setCancelled(true);
                }
            }

            if (packet instanceof C0APacketAnimation) {
                if (needPreventAttack()) {
                    event.setCancelled(true);
                }
            }

            if (packet instanceof C0FPacketConfirmTransaction) {
                final C0FPacketConfirmTransaction pct = (C0FPacketConfirmTransaction) packet;
                if (pct.getWindowId() < 0 && Boolean.TRUE.equals(pingSpoof.getValue())) {
                    event.setCancelled(true);
                    packets.add(pct);
                }
            }

            if (packet instanceof C00PacketKeepAlive) {
                final C00PacketKeepAlive pka = (C00PacketKeepAlive) packet;
                if (Boolean.TRUE.equals(pingSpoof.getValue())) {
                    pka.setKey(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
                    event.setCancelled(true);
                    packets.add(pka);
                }
            }
        }
    }

    private boolean isKeepAlive(Packet<?> packet) {
        return packet instanceof C00PacketKeepAlive;
    }

    private boolean isConfirm(Packet<?> packet) {
        return packet instanceof C0FPacketConfirmTransaction;
    }

    private void resetDisabler() {
        packets.forEach(p -> {
            if (isKeepAlive(p)) PacketUtils.sendPacketNoEvent(p);
        });
        packets.forEach(p -> {
            if (isConfirm(p)) PacketUtils.sendPacketNoEvent(p);
        });
        packets.clear();
        timer.reset();
    }

    private boolean needPreventAttack() {
        final GameSpeed gameSpeed = (GameSpeed) ModuleManager.getModuleByClass(GameSpeed.class);
        final LongJump longJump = (LongJump) ModuleManager.getModuleByClass(LongJump.class);
        final Blink blink = (Blink) ModuleManager.getModuleByClass(Blink.class);

        return gameSpeed.isEnabled() || longJump.isEnabled() || blink.isEnabled();
    }
}
