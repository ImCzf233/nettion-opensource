package nettion.utils.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import nettion.event.events.world.EventMove;
import net.minecraft.block.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;

public class PlayerUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return mc.theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }

    public static void setMotion(double speed) {
        float yaw = mc.thePlayer.rotationYaw;
        double forward = mc.thePlayer.moveForward;
        double strafe = mc.thePlayer.moveStrafing;
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }

    public static void setMoveSpeed(final EventMove event, final double speed) {
        double forward = mc.thePlayer.moveForward;
        double strafe = mc.thePlayer.moveStrafing;
        float yaw = mc.thePlayer.rotationYaw;

        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public static boolean isMoving() {
        if(mc.thePlayer.moveStrafing!=0||mc.thePlayer.moveForward!=0){
            return true;
        }
        return false;
    }

    public static boolean hasInvWeapon() {
        if (mc.thePlayer.inventory.getCurrentItem() != null)
            return false;

        return (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAxe) || (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword);
    }

    public static void stop() {
        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
    }

    public static Float movingYaw() {
        return (float) (direction() * 180f / Math.PI);
    }


    public static Block block(double x, double y, double z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    /**
     * make player move slowly like when using item
     * @author liulihaocai
     */
    public static void limitSpeedByPercent(Float percent) {
        mc.thePlayer.motionX *= percent;
        mc.thePlayer.motionZ *= percent;
    }

    public static Block block(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static boolean isPlayerInGame() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    public static Double direction() {
        double rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0f) rotationYaw += 180f;
        double forward = 1f;
        if (mc.thePlayer.moveForward < 0f) {
            forward = -0.5f;
        }  else if (mc.thePlayer.moveForward > 0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward;
        if (mc.thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward;
        return Math.toRadians(rotationYaw);
    }

    public static BlockPos getBlockCorner(BlockPos start, BlockPos end) {
        for(int x = 0; x <= 1; ++x) {
            for(int y = 0; y <= 1; ++y) {
                for(int z = 0; z <= 1; ++z) {
                    BlockPos pos = new BlockPos(end.getX() + x, end.getY() + y, end.getZ() + z);
                    if (!isBlockBetween(start, pos)) {
                        return pos;
                    }
                }
            }
        }

        return null;
    }

    public static boolean isBlockBetween(BlockPos start, BlockPos end) {
        int startX = start.getX();
        int startY = start.getY();
        int startZ = start.getZ();
        int endX = end.getX();
        int endY = end.getY();
        int endZ = end.getZ();
        double diffX = (double)(endX - startX);
        double diffY = (double)(endY - startY);
        double diffZ = (double)(endZ - startZ);
        double x = (double)startX;
        double y = (double)startY;
        double z = (double)startZ;
        double STEP = 0.1D;
        int STEPS = (int)Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ))) * 4;

        for(int i = 0; i < STEPS - 1; ++i) {
            x += diffX / (double)STEPS;
            y += diffY / (double)STEPS;
            z += diffZ / (double)STEPS;
            if (x != (double)endX || y != (double)endY || z != (double)endZ) {
                BlockPos pos = new BlockPos(x, y, z);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isInsideBlock() {
        for (int x = net.minecraft.util.MathHelper.floor_double(mc.thePlayer.boundingBox.minX); x < net.minecraft.util.MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1; ++x) {
            for (int y = net.minecraft.util.MathHelper.floor_double(mc.thePlayer.boundingBox.minY); y < net.minecraft.util.MathHelper.floor_double(mc.thePlayer.boundingBox.maxY) + 1; ++y) {
                for (int z = net.minecraft.util.MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); z < net.minecraft.util.MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; ++z) {
                    final Block block = mc.thePlayer.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB((double)x, (double)y, z, (double)(x + 1), y + 1, z + 1);
                        }
                        if (boundingBox != null && mc.thePlayer.boundingBox.intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static double getJumpBoostModifier(double baseJumpHeight, boolean potionJump) {
        if (mc.thePlayer.isPotionActive(Potion.jump) && potionJump) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += ((float) (amplifier + 1) * 0.1f);
        }

        return baseJumpHeight;
    }

    public static boolean isOverVoid() {
        boolean isOverVoid = true;
        BlockPos block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
        for (double i = mc.thePlayer.posY + 1; i > 0; i -= 0.5) {
            if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
                isOverVoid = false;
                break;
            }
            block = block.add(0, -1, 0);
        }

        for (double i = 0; i < 10; i += 0.1) {
            if (isOnGround(i) && isOverVoid) {
                isOverVoid = false;
                break;
            }
        }

        return isOverVoid;
    }

    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0) return true;
        for (int i = (int) (mc.thePlayer.posY - 1); i > 0; --i)
            if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir))
                return false;
        return true;
    }

    public static boolean MovementInput() {
        return PlayerUtils.mc.gameSettings.keyBindForward.isPressed() || PlayerUtils.mc.gameSettings.keyBindLeft.isPressed() || PlayerUtils.mc.gameSettings.keyBindRight.isPressed() || PlayerUtils.mc.gameSettings.keyBindBack.isPressed();
    }

    public static boolean isAirUnder(Entity ent) {
        return mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY - 1, ent.posZ)).getBlock() == Blocks.air;
    }

    public static int getSpeedEffect() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }

    public static void setMotion(double speed, boolean smoothStrafe) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        int direction = smoothStrafe ? 45 : 90;

        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -direction : direction);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? direction : -direction);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }

            mc.thePlayer.motionX = forward * speed * (-Math.sin(Math.toRadians(yaw))) + strafe * speed * Math.cos(Math.toRadians(yaw));
            mc.thePlayer.motionZ = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * (-Math.sin(Math.toRadians(yaw)));
        }
    }

    public static void setMotion(EventMove event, double speed, double motion, boolean smoothStrafe) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        double yaw = mc.thePlayer.rotationYaw;
        int direction = smoothStrafe ? 45 : 90;

        if ((forward == 0.0) && (strafe == 0.0)) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (forward > 0.0 ? -direction : direction);
                } else if (strafe < 0.0) {
                    yaw += (forward > 0.0 ? direction : -direction);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }

            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            event.setX((forward * speed * cos + strafe * speed * sin) * motion);
            event.setZ((forward * speed * sin - strafe * speed * cos) * motion);
        }
    }

    public static double getBaseMoveSpeed(double base) {
        double baseSpeed = base;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed *= 1.0D + 0.2D * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);

        return baseSpeed;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        Minecraft.getMinecraft();
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            Minecraft.getMinecraft();
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static boolean isOnGround(double height) {
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInLiquid() {
        if(mc.thePlayer == null)return false;
        if (mc.thePlayer.isInWater()) {
            return true;
        } else {
            boolean var1 = false;
            int var2 = (int)mc.thePlayer.getEntityBoundingBox().minY;

            for(int var3 = net.minecraft.util.MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); var3 < net.minecraft.util.MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++var3) {
                for(int var4 = net.minecraft.util.MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); var4 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++var4) {
                    Block var5 = mc.theWorld.getBlockState(new BlockPos(var3, var2, var4)).getBlock();
                    if (var5 != null && var5.getMaterial() != Material.air) {
                        if (!(var5 instanceof BlockLiquid)) {
                            return false;
                        }

                        var1 = true;
                    }
                }
            }

            return var1;
        }
    }

    public static void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward == 0.0D && pseudoStrafe == 0.0D) {
            moveEvent.setZ(0.0D);
            moveEvent.setX(0.0D);
        } else {
            if (pseudoForward != 0.0D) {
                if (pseudoStrafe > 0.0D) {
                    yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
                } else if (pseudoStrafe < 0.0D) {
                    yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;
                if (pseudoForward > 0.0D) {
                    forward = 1.0D;
                } else if (pseudoForward < 0.0D) {
                    forward = -1.0D;
                }
            }

            double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
            double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
            moveEvent.setX(forward * moveSpeed * cos + strafe * moveSpeed * sin);
            moveEvent.setZ(forward * moveSpeed * sin - strafe * moveSpeed * cos);
        }

    }

    public static float getDirection() {
        Minecraft.getMinecraft();
        float yaw = mc.thePlayer.rotationYaw;
        Minecraft.getMinecraft();
        if (mc.thePlayer.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        Minecraft.getMinecraft();
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else {
            Minecraft.getMinecraft();
            if (mc.thePlayer.moveForward > 0.0f) {
                forward = 0.5f;
            }
        }
        Minecraft.getMinecraft();
        if (mc.thePlayer.moveStrafing > 0.0f) {
            yaw -= 90.0f * forward;
        }
        Minecraft.getMinecraft();
        if (mc.thePlayer.moveStrafing < 0.0f) {
            yaw += 90.0f * forward;
        }
        return yaw *= 0.017453292f;
    }

    public static boolean isInWater() {
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock().getMaterial() == Material.water;
    }

    public static void setSpeed(double speed) {
        Minecraft.getMinecraft();
        mc.thePlayer.motionX = - Math.sin(PlayerUtils.getDirection()) * speed;
        Minecraft.getMinecraft();
        mc.thePlayer.motionZ = Math.cos(PlayerUtils.getDirection()) * speed;
    }

    public static float absYaw() {
        return Math.abs(mc.thePlayer.rotationYaw);
    }

    public void clip(double dist, float y) {
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -Math.sin(yaw) * dist * 0.42;
        double z = Math.cos(yaw) * dist * 0.42;
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
    }

    public static void setSpeed(float speed) {
        Minecraft.getMinecraft();
        mc.thePlayer.motionX = - Math.sin(PlayerUtils.getDirection()) * speed;
        Minecraft.getMinecraft();
        mc.thePlayer.motionZ = Math.cos(PlayerUtils.getDirection()) * speed;
    }

    public static double getSpeed(double motionX, double motionZ) {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }

    public static double getSpeed() {
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        double var10000 = mc.thePlayer.motionX;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        double var10001 = mc.thePlayer.motionZ;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        return Math.sqrt((var10000 *= mc.thePlayer.motionX) + var10001 * mc.thePlayer.motionZ);
    }

    public static Block getBlock(BlockPos pos) {
        Minecraft.getMinecraft();
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public static Block getBlock(double d, double d2, double d3) {
        return PlayerUtils.mc.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock();
    }

    public static void strafe() {
        strafe(getSpeed2());
    }
    public static void strafe(final double speed) {
        if(!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
    
    public static void forward(final double length) {
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }
    
    public static boolean onGround(double height) {
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
        return false;
        }
    }
   
    public static float getSpeed2() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }
}

