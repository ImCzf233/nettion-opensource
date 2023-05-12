package nettion.features.module.modules.player;

import nettion.event.EventHandler;
import nettion.features.value.values.Numbers;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import java.util.ArrayList;
import java.util.List;

public class BedNuker extends Module {
    public static BlockPos blockBreaking;
    List<BlockPos> beds = new ArrayList<>();
    private Numbers<Double> range = new Numbers<Double>("BedNukerRange", 3.0, 1.0, 8.0, 1.0);

    public BedNuker() {
        super("BedNuker", ModuleType.Player);
        addValues(this.range);
    }

    private boolean blockChecks(Block block) {
        return (block == Blocks.bed);
    }

    @Override
    public void onDisable() {
        if(blockBreaking != null)
            blockBreaking = null;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        int reach = (int) range.getValue().floatValue();
        for (int y = reach; y >= -reach; --y) {
            for (int x = -reach; x <= reach; ++x) {
                for (int z = -reach; z <= reach; ++z) {
                    if (mc.thePlayer.isSneaking()) {
                        return;
                    }
                    BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    if (getFacingDirection(pos) != null && blockChecks(mc.theWorld.getBlockState(pos).getBlock()) && mc.thePlayer.getDistance(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z) < mc.playerController.getBlockReachDistance() - 0.2) {
                        if(!beds.contains(pos))
                            beds.add(pos);

                    }
                }
            }
        }
        BlockPos closest = null;
        if(!beds.isEmpty())
            for(int i = 0; i < beds.size(); i++){
                BlockPos bed = beds.get(i);
                if(mc.thePlayer.getDistance(bed.getX(), bed.getY(), bed.getZ()) > mc.playerController.getBlockReachDistance() - 0.2
                        || mc.theWorld.getBlockState(bed).getBlock() != Blocks.bed){
                    beds.remove(i);
                }
                if(closest == null || mc.thePlayer.getDistance(bed.getX(), bed.getY(), bed.getZ()) < mc.thePlayer.getDistance(closest.getX(), closest.getY(), closest.getZ())){
                    closest = bed;
                }
            }

        if(closest != null){


            float[] rot = getRotations(closest, getClosestEnum(closest));
            event.setYaw(rot[0]);
            event.setPitch(rot[1]);
            //mc.thePlayer.rotationYaw = rot[0];
            //mc.thePlayer.rotationPitch = rot[1];
            blockBreaking = closest;
            EnumFacing direction = getClosestEnum(blockBreaking);
            mc.playerController.onPlayerDamageBlock(blockBreaking, direction);
            return;
        }
        blockBreaking = null;


        if (blockBreaking != null) {
            if (mc.playerController.blockHitDelay > 1) {
                mc.playerController.blockHitDelay = 1;
            }
            mc.thePlayer.swingItem();
            EnumFacing direction = getClosestEnum(blockBreaking);
            if (direction != null) {
                mc.playerController.onPlayerDamageBlock(blockBreaking, direction);
            }
        }
    }

    public static float[] getRotations(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5D - mc.thePlayer.posX + face.getFrontOffsetX() / 2.0D;
        double z = block.getZ() + 0.5D - mc.thePlayer.posZ + face.getFrontOffsetZ() / 2.0D;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - block.getY() + 0.5D;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360.0F;
        }
        return new float[] { yaw, pitch };
    }

    private EnumFacing getClosestEnum(BlockPos pos) {
        EnumFacing closestEnum = EnumFacing.UP;
        float rotations = MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[0]);
        if (rotations >= 45.0F && rotations <= 135.0F) {
            closestEnum = EnumFacing.EAST;
        } else if ((rotations >= 135.0F && rotations <= 180.0F) || (rotations <= -135.0F && rotations >= -180.0F)) {

            closestEnum = EnumFacing.SOUTH;
        } else if (rotations <= -45.0F && rotations >= -135.0F) {
            closestEnum = EnumFacing.WEST;
        } else if ((rotations >= -45.0F && rotations <= 0.0F) || (rotations <= 45.0F && rotations >= 0.0F)) {

            closestEnum = EnumFacing.NORTH;
        }
        if (MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) > 75.0F ||
                MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) < -75.0F) {
            closestEnum = EnumFacing.UP;
        }
        return closestEnum;
    }
    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isBlockNormalCube() && !(mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof net.minecraft.block.BlockBed)) {
            direction = EnumFacing.UP;
        } else if (!mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isBlockNormalCube() && !(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof net.minecraft.block.BlockBed)) {
            direction = EnumFacing.DOWN;
        } else if (!mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isBlockNormalCube() && !(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof net.minecraft.block.BlockBed)) {
            direction = EnumFacing.EAST;
        } else if (!mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isBlockNormalCube() && !(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof net.minecraft.block.BlockBed)) {
            direction = EnumFacing.WEST;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isBlockNormalCube() && !(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof net.minecraft.block.BlockBed)) {
            direction = EnumFacing.SOUTH;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isBlockNormalCube() && !(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof net.minecraft.block.BlockBed)) {
            direction = EnumFacing.NORTH;
        }
        MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D));
        if (rayResult != null && rayResult.getBlockPos() == pos) {
            return rayResult.sideHit;
        }
        return direction;


    }
}
