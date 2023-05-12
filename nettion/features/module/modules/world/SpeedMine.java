package nettion.features.module.modules.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.utils.player.PacketUtils;

public class SpeedMine extends Module {
    private final Numbers<Double> speed = new Numbers<>("Speed", 1.1, 1.0, 3.0, 0.1);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost = false;
    private float damage = 0f;
    public SpeedMine() {
        super("SpeedMine", ModuleType.World);
        addValues(speed);
    }

    @EventHandler
    private void onPacket(EventPacketSend e) {
        if (e.packet instanceof C07PacketPlayerDigging) {
            if (((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                boost = true;
                pos = ((C07PacketPlayerDigging) e.getPacket()).position;
                facing = ((C07PacketPlayerDigging) e.getPacket()).facing;
                damage = 0f;
            } else if ((((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) || (((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                boost = false;
                pos = null;
                facing = null;
            }
        }
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        mc.playerController.blockHitDelay = 0;
        if (pos != null && boost) {
            IBlockState blockState = mc.theWorld.getBlockState(pos);
            damage += blockState.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) * speed.getValue();
            if (damage >= 1) {
                try {
                    mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 11);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
                PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                damage = 0f;
                boost = false;
            }
        }
    }
}
