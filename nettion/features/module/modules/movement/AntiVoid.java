package nettion.features.module.modules.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import nettion.event.events.world.EventMove;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.event.EventHandler;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.PacketUtils;
import nettion.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AntiVoid
extends Module {
    private final Mode<Enum> mode = new Mode("Mode", fallmode.values(), fallmode.Packet);
    private final Numbers<Double> fallDistance = new Numbers<Double>("FallDistance", 10.0, 1.0, 20.0, 1.0);
    private double prevX;
    private double prevY;
    private double prevZ;

    enum fallmode {
        Packet,
        Watchdog,
        Bounce,
        Teleport,
        Flag
    }

    public AntiVoid() {
        super("AntiVoid", ModuleType.Movement);
        this.addValues(this.mode, this.fallDistance);
    }

    @Override
    public void onDisable() {
        this.prevX = 0.0;
        this.prevY = 0.0;
        this.prevZ = 0.0;
    }

    @EventHandler
    private void onMove(EventMove e) {
        boolean isInVoid;
        this.setSuffix(this.mode.getValue());
        EntityPlayerSP player = mc.thePlayer;
        boolean bl = isInVoid = !this.isBlockUnder();
        if (!isInVoid && (double)player.fallDistance < 1.0 && player.onGround) {
            this.prevX = player.prevPosX;
            this.prevY = player.prevPosY;
            this.prevZ = player.prevPosZ;
        }
        if (isInVoid) {
            if (mode.getValue() == fallmode.Teleport && this.fallDistanceCheck()) {
                player.setPositionAndUpdate(this.prevX, this.prevY, this.prevZ);
            }
            if (mode.getValue() == fallmode.Watchdog && this.fallDistanceCheck()) {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    e.setX(e.getX() + Math.max(PlayerUtils.getSpeed(), 0.2 + Math.random() / 100.0));
                    e.setZ(e.getZ() + Math.max(PlayerUtils.getSpeed(), Math.random() / 100.0));
                } else {
                    e.setX(e.getX() - Math.max(PlayerUtils.getSpeed(), 0.2 + Math.random() / 100.0));
                    e.setZ(e.getZ() - Math.max(PlayerUtils.getSpeed(), Math.random() / 100.0));
                }
            }
            if (mode.getValue() == fallmode.Flag && this.fallDistanceCheck()) {
                player.motionY += 0.1;
                player.fallDistance = 0.0f;
            }
            if (mode.getValue() == fallmode.Bounce && !player.onGround && !player.isCollidedVertically && player.fallDistance > 4.0f && player.prevPosY < this.prevY) {
                player.motionY += 0.23;
            }
            if (mode.getValue() == fallmode.Packet && this.fallDistanceCheck()) {
                PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(player.posX, player.posY + (double)player.fallDistance, player.posZ, false));
            }
        }
    }

    private boolean fallDistanceCheck() {
        EntityPlayerSP player = mc.thePlayer;
        if (!player.onGround && !player.isCollidedVertically) {
            return (double)player.fallDistance > (Double)this.fallDistance.getValue();
        }
        return false;
    }

    private boolean isBlockUnder() {
        for (int i = (int)(mc.thePlayer.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }
}

