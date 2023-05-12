package nettion.features.module.modules.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import nettion.features.module.ModuleManager;
import nettion.features.module.modules.combat.TargetStrafe;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketReceive;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.Nettion;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;
import nettion.utils.player.PlayerUtils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.Timer;
import net.optifine.util.MathUtils;

public class Speed
extends Module {
    private final Mode<Enum> mode = new Mode("Mode", SpeedMode.values(), SpeedMode.Watchdog);
    private final Numbers<Double> timerboostspeed = new Numbers<>("TimerBoost", 1.0, 0.5, 3.0, 0.1);
    private final Option<Boolean> lagbackcheck = new Option<>("LagBackCheck", true);
    private int movementTicksNCP = 0;
    private double speedNCP;
    double moveSpeed;
    private TargetStrafe targetStrafe;

    private double[] lastPos;

    public Speed() {
        super("Speed", ModuleType.Movement);
        this.addValues(mode, timerboostspeed, lagbackcheck);
    }

    @Override
    public void onDisable() {
        Timer.timerSpeed = 1.0f;
    }

    @Override
    public void onEnable() {
        if (this.targetStrafe == null) {
            this.targetStrafe = (TargetStrafe) ModuleManager.getModuleByClass(TargetStrafe.class);
        }
    }

    @EventHandler
    private void onLag(EventPacketReceive event) {
        if(!(event.getPacket() instanceof S08PacketPlayerPosLook)) {
            return;
        }
        Nettion.instance.getModuleManager();
        Speed speed;
        if(this.lagbackcheck.getValue()) {
            if((speed = (Speed) Nettion.instance.getModuleManager().getModuleByClass(Speed.class)).isEnabled()) {
                speed.setEnabled(false);
                NotificationManager.post(NotificationType.WARNING, "LagBackCheck", "(" + this.getName() + ")The current module is down due to lag.", 2);
            }
        }
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
        Timer.timerSpeed = timerboostspeed.getValue().floatValue();
    }

    @EventHandler
    private void onPacketReceive(EventPacketReceive event) {
    }

    @EventHandler
    private void onPreUpdate(EventPreUpdate e) {
        if (mode.getValue() == SpeedMode.BHop) {
            PlayerUtils.strafe();
            if (mc.thePlayer.isMoving()) {
                mc.thePlayer.setSprinting(true);
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = 0.42;
                }
                PlayerUtils.strafe(PlayerUtils.getBaseMoveSpeed() * 1.78f);
            }
        } else if (mode.getValue() == SpeedMode.Watchdog) {
            if (PlayerUtils.isInLiquid()) {
                return;
            }
            if (mc.thePlayer.isMoving()) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                float speed = 1.48f;
                if (mc.thePlayer.moveStrafing != 0f || mc.thePlayer.moveForward < 0) {
                    speed -= 0.20f;
                } else {
                    speed -= MathUtils.getIncremental(0.00411, 0.0465123);
                } if (mc.thePlayer.onGround) {
                    PlayerUtils.setSpeed((PlayerUtils.getBaseMoveSpeed() * speed));
                }
            }
        } else if (mode.getValue() == SpeedMode.NCPHop) {
            if (PlayerUtils.isMoving()) {
                if (PlayerUtils.isOnGround(0.00023)) {
                    mc.thePlayer.motionY = 0.41;
                }
                switch (movementTicksNCP) {
                    case 1:
                        speedNCP = PlayerUtils.getBaseMoveSpeed();
                        break;
                    case 2:
                        speedNCP = PlayerUtils.getBaseMoveSpeed() + (0.132535 * Math.random());
                        break;
                    case 3:
                        speedNCP = PlayerUtils.getBaseMoveSpeed() / 2;
                        break;
                }
                PlayerUtils.setSpeed(Math.max(speedNCP, PlayerUtils.getBaseMoveSpeed()));
                movementTicksNCP++;
            }
        } else if (mode.getValue() == SpeedMode.SlowHop) {
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && mc.thePlayer.isMoving()) {
                Timer.timerSpeed = 1.0f;
                e.y = 0.42f;
                mc.thePlayer.motionY = 0.42f;
                moveSpeed = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? PlayerUtils.getBaseMoveSpeed() * 0.525 : PlayerUtils.getBaseMoveSpeed() * 0.85;
            }
            PlayerUtils.setMotion(moveSpeed);
        } else if (mode.getValue() == SpeedMode.AAC5) {
            if (mc.thePlayer.onGround && PlayerUtils.isMoving()) {
                PlayerUtils.strafe(0.4);
                mc.thePlayer.motionY = 0.42;
                lastPos = null;
            } else if (lastPos == null) {
                lastPos = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
                e.setCancelled(true);
            } else {
                if (lastPos.length == 3)
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(lastPos[0], lastPos[1], lastPos[2], false));
                lastPos = new double[]{};
            }
        }
    }

    enum SpeedMode {
        BHop,
        AAC5,
        NCPHop,
        SlowHop,
        Watchdog,
    }
}
