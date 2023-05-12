package nettion.features.module.modules.movement;

import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import nettion.event.events.misc.EventJump;
import nettion.event.events.render.EventRender2D;
import nettion.event.events.world.*;
import nettion.features.module.ModuleManager;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Timer;
import nettion.utils.player.PacketUtils;
import nettion.utils.player.PlayerUtils;
import nettion.utils.time.TimerUtils;

import java.util.Objects;
import java.util.Random;

public class LongJump extends Module {
    private final Mode<Enum<?>> mode = new Mode<>("Mode", modes.values(), modes.Watchdog);
    private final Option<Boolean> lagbackcheck = new Option<>("LagBackCheck", true);

    public LongJump() {
        super("LongJump", ModuleType.Movement);
        this.addValues(mode, lagbackcheck);
    }

    enum modes {
        Watchdog,
    }

    private double aa,P;
    private boolean K;
    private double U;
    private boolean O;
    private double X;
    private boolean H,ab;
    private int I;
    private final TimerUtils jumpTimer = new TimerUtils();

    private int a() {
        int n = mc.thePlayer.b(Potion.jump);
        int n2 = 3;
        if (n == 1) {
            n2 = 4;
        }
        if (n == 2) {
            n2 = 5;
        }
        if (n == 3) {
            n2 = 5;
        }
        if (n == 4) {
            n2 = 6;
        }
        if (n > 4) {
            n2 = 3 + n;
        }
        return n2;
    }

    @EventHandler
    private void render(EventRender2D e) {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mode.getValue() == modes.Watchdog) {
            Timer.timerSpeed = 1;
            ab = true;
            jumpTimer.reset();
            I = 0;
            this.O = false;
            this.K = false;
            this.H = false;
            this.P = 0.0;
            this.U = mc.thePlayer.m();
            this.K = false;
            PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
    }

    @Override
    public void onDisable() {
        Timer.timerSpeed = 1.0f;
        if (mode.getValue() == modes.Watchdog) {
            NotificationManager.post(NotificationType.WARNING, "Warning", "LongJump was disabled to prevent flags/errors (1.5s)", 1.5f);
            super.onDisable();
            ab = true;
            I = 0;
            this.O = false;
            this.K = false;
            this.H = false;
            this.P = 0.0;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionY = -0.0785;
            mc.thePlayer.motionZ = 0.0;
            Timer.timerSpeed = 1.0f;
            PacketUtils.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @EventHandler
    private void onLag(EventPacketReceive event) {
        if(!(event.getPacket() instanceof S08PacketPlayerPosLook)) {
            return;
        }
        LongJump longjump;
        if(this.lagbackcheck.getValue()) {
            if((Objects.requireNonNull(longjump = (LongJump) ModuleManager.getModuleByClass(LongJump.class))).isEnabled()) {
                longjump.setEnabled(false);
                NotificationManager.post(NotificationType.WARNING, "LagBackCheck", "(" + this.getName() + ")The current module is down due to lag.", 2);
            }
        }
    }

    @EventHandler
    private void onPacketReceive(EventPacketReceive event) {
        if (mode.getValue() == modes.Watchdog) {
            if(!this.isEnabled()){
                return;
            }
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                setEnabled(false);
            }
        }
    }

    @EventHandler
    private void onMove(EventMove e) {
        if (mode.getValue() == modes.Watchdog) {
            if(!this.isEnabled()){
                return;
            }
            if (this.ab || !mc.thePlayer.isMoving()) {
                e.setMoveSpeed(0.0);
            }
            else {
                this.c(e);
                this.b(e);
            }
        }
    }

    @EventHandler
    private void onUpdate(EventUpdate e) {
        if (mode.getValue() == modes.Watchdog) {
            this.aa = mc.thePlayer.getLastTickDistance();
            this.X = this.c();
        }
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        this.setSuffix(mode.getValue().name());
        if (mode.getValue() == modes.Watchdog) {
            if(!this.isEnabled()){
                return;
            }
            if(mc.thePlayer.hurtTime<=0) {
                if (mc.thePlayer.isCollidedVertically) {
                    PlayerUtils.strafe((Math.max(0.44 + PlayerUtils.getSpeedEffect() * 0.1, PlayerUtils.getBaseMoveSpeed(0.2873))));
                    PlayerUtils.setSpeed((Math.max(0.44 + PlayerUtils.getSpeedEffect() * 0.1, PlayerUtils.getBaseMoveSpeed(0.2873))));
                }
            }
            if (mc.thePlayer.onGround) {
                e.setGround(false);
                if (this.I < this.a()) {
                    mc.thePlayer.motionY = 0.42f;
                    ++this.I;
                }else {
                    this.ab = false;
                }
            }
            if(!ab){
                if(mc.thePlayer.offGroundTicks == 1) {
                    this.O = true;
                    e.setGround(true);
                }


            }

            if (this.K && mc.thePlayer.isCollided) {
                this.setEnabled(false);
                this.K = false;
            }
        }
    }

    @EventHandler
    private void onJump(EventJump e) {
        if (mode.getValue() == modes.Watchdog) {
            if(!this.isEnabled()){
                return;
            }
            if (mc.thePlayer.isMoving()) {
                e.setCancelled(true);
            }
        }
    }

    private void b(EventMove moveEvent) {
        if (mc.thePlayer.onGround) {
            if (this.H) {
                mc.thePlayer.motionY = mc.thePlayer.getJumpMotion();
                moveEvent.setY(mc.thePlayer.motionY);
                this.U *= 2.1399999;
            }else {
                this.U = 0.28630206268501246;
            }

        }
        if (this.H) {
            this.U = this.aa - 0.3 * (this.aa - this.X);
        }
        this.U -= this.aa / 59.0;
        this.K = true;
        this.U = Math.max(this.U, this.X);
        if(mc.thePlayer.hurtTime>0&&mc.thePlayer.hurtTime<=6) {
            PlayerUtils.setSpeed(this.U * 0.75f);
            PlayerUtils.strafe(this.U * 0.75f);
            moveEvent.setY(mc.thePlayer.motionY += (mc.thePlayer.motionY<0)?mc.thePlayer.motionY*-0.1f:mc.thePlayer.motionY*0.1f);
        }
        this.H = mc.thePlayer.onGround;
    }
    private void c(EventMove moveEvent) {
        if (mc.thePlayer.onGround) {
            if (this.H) {
                mc.thePlayer.motionY = mc.thePlayer.getJumpMotion();
                moveEvent.setY(mc.thePlayer.motionY);
                this.U *= 2.1399999;
            }else {
                this.U = 0.28630206268501246;
            }

        }
        if (this.H) {
            this.U = this.aa - 0.76999 * (this.aa - this.X);
        }
        this.U *= 0.9788305162963192;
        this.K = true;
        if (mc.thePlayer.offGroundTicks > 1) {
            if(mc.thePlayer.hurtTime>0&&mc.thePlayer.hurtTime<=8) {
                PlayerUtils.setSpeed(this.U * 0.7f);
                PlayerUtils.strafe(this.U * 0.75f);
                if(mc.thePlayer.hurtTime<=6) {
                    moveEvent.setY(mc.thePlayer.motionY += (mc.thePlayer.motionY<0)?mc.thePlayer.motionY*-0.18f:mc.thePlayer.motionY*0.18f);
                }
            }
            if (this.P > 0.0 && mc.thePlayer.motionY < 0.0) {
                mc.thePlayer.motionY = this.P;
                moveEvent.setY(mc.thePlayer.motionY);
                this.P = 0.0;
            }
        }
        if (mc.thePlayer.offGroundTicks > 3 && this.O) {
            this.U *= 2.2 - 0.35 * (double)mc.thePlayer.b(Potion.moveSpeed);
            this.O = false;
        }
        this.U = Math.max(this.U, this.X);

        this.H = mc.thePlayer.onGround;
    }
    public static double c(Random random) {
        return random.nextDouble();
    }
    private double c() {
        double d = 0.28630206268501246;
        int n = mc.thePlayer.b(Potion.moveSpeed);
        int n2 = mc.thePlayer.b(Potion.moveSlowdown);
        double d2 = n - n2;
        return d + d2 * 0.15;
    }

}

