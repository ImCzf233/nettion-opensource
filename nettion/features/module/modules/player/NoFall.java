package nettion.features.module.modules.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import nettion.event.events.world.EventTick;
import nettion.event.EventHandler;
import nettion.features.value.values.Mode;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import nettion.utils.player.BlockUtils;
import nettion.utils.player.PacketUtils;
import nettion.utils.player.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import nettion.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import nettion.utils.time.TimerUtils;

public class NoFall
extends Module {
    public static final Mode<Enum> mode = new Mode<>("mode", FallMod.values(), FallMod.Packet);
    public TimerUtils timerHelper = new TimerUtils();
    private boolean aac5Check = false;
    private boolean aac5doFlag = false;
    private int aac5Timer = 0;

    enum FallMod {
        MLG,
        AAC5,
        Packet,
        Matrix,
        Spartan,
    }

    public NoFall() {
        super("NoFall", ModuleType.Player);
        addValues(mode);
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == FallMod.AAC5) {
            aac5Check = false;
            aac5Timer = 0;
            aac5doFlag = false;
        }
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(mode.getValue().name());
        if (mode.getValue() == FallMod.Packet) {
            if (mc.thePlayer.fallDistance > 2 || PlayerUtils.getDirection() > 2) {
                event.setGround(true);
            }
        } else if (mode.getValue() == FallMod.AAC5) {
            double offsetYs = 0.0;
            aac5Check = false;
            while (mc.thePlayer.motionY - 1.5 < offsetYs) {
                BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + offsetYs, mc.thePlayer.posZ);
                Block block = BlockUtils.getBlock(blockPos);
                AxisAlignedBB axisAlignedBB = block.getCollisionBoundingBox(mc.theWorld, blockPos, BlockUtils.getState(blockPos));
                if (axisAlignedBB != null) {
                    offsetYs = -999.9;
                    aac5Check = true;
                }
                offsetYs -= 0.5;
            }
            if (mc.thePlayer.onGround) {
                mc.thePlayer.fallDistance = -2f;
                aac5Check = false;
            }
            if (aac5Timer > 0) {
                aac5Timer -= 1;
            }
            if (aac5Check && mc.thePlayer.fallDistance > 2.5 && !mc.thePlayer.onGround) {
                aac5doFlag = true;
                aac5Timer = 18;
            } else {
                if (aac5Timer < 2) aac5doFlag = false;
            }
            if (aac5doFlag) {
                if (mc.thePlayer.onGround) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
                } else {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, true));
                }
            }
        } else if (mode.getValue() == FallMod.Matrix) {
            if (mc.thePlayer.fallDistance > 3) {
                mc.thePlayer.fallDistance = (float) (Math.random() * 1.0E-12);
                PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                mc.thePlayer.fallDistance = 0;
            }
        } else if (mode.getValue() == FallMod.Spartan) {
            if (mc.thePlayer.fallDistance > 3.5f) {
                if (timerHelper.hasReached(150L)) {
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    timerHelper.reset();
                } else {
                    mc.thePlayer.onGround = false;
                }
            }
        }
    }

    @EventHandler
    private void onTick(EventTick e) {
        if (mode.getValue() == FallMod.MLG) {
            if (!PlayerUtils.isPlayerInGame()) {
                return;
            }
            if (mc.thePlayer.fallDistance > 4.0f && this.getSlotWaterBucket() != -1 && this.isMLGNeeded()) {
                mc.thePlayer.rotationPitch = 90.0f;
                this.swapToWaterBucket(this.getSlotWaterBucket());
            }
            if (mc.thePlayer.fallDistance > 4.0f && this.isMLGNeeded() && !mc.thePlayer.isOnLadder() && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - getDistanceToFall() - 1.0, mc.thePlayer.posZ);
                this.placeWater(pos, EnumFacing.UP);
                if (mc.thePlayer.getHeldItem().getItem() == Items.bucket) {
                    Thread thr = new Thread(() -> {
                        try {
                            Thread.sleep(50);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        ReflectionUtil.rightClickMouse();
                    });
                    thr.start();
                }
                mc.thePlayer.fallDistance = 0.0f;
            }
        }
    }

    public static Block getBlock(BlockPos block) {
        return Minecraft.getMinecraft().theWorld.getBlockState(block).getBlock();
    }


    private void placeWater(BlockPos pos, EnumFacing facing) {
        ItemStack heldItem = mc.thePlayer.inventory.getCurrentItem();
        Minecraft.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), pos, facing, new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5));
        if (heldItem != null) {
            Minecraft.playerController.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld, heldItem);
            mc.entityRenderer.itemRenderer.resetEquippedProgress2();
        }
    }

    private boolean isMLGNeeded() {
        if (Minecraft.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE || Minecraft.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR || mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.allowFlying) {
            return false;
        }
        for (double y = Minecraft.getMinecraft().thePlayer.posY; y > 0.0; y -= 1.0) {
            Block block = getBlock(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, y, Minecraft.getMinecraft().thePlayer.posZ));
            if (block.getMaterial() == Material.water) {
                return false;
            }
            if (block.getMaterial() != Material.air) {
                return true;
            }
            if (y < 0.0) break;
        }
        return true;
    }

    public static double getDistanceToFall() {
        double distance = 0.0;
        for (double i = mc.thePlayer.posY; i > 0.0; i -= 1.0) {
            Block block = getBlock(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ));
            if (block.getMaterial() != Material.air && block.isBlockNormalCube() && block.isCollidable()) {
                distance = i;
                break;
            }
            if (i < 0.0) break;
        }
        double distancetofall = mc.thePlayer.posY - distance - 1.0;
        return distancetofall;
    }

    private int getSlotWaterBucket() {
        for (int i = 0; i < 8; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] == null || !mc.thePlayer.inventory.mainInventory[i].getItem().getUnlocalizedName().contains("bucketWater")) continue;
            return i;
        }
        return -1;
    }

    private void swapToWaterBucket(int blockSlot) {
        mc.thePlayer.inventory.currentItem = blockSlot;
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket((Packet)new C09PacketHeldItemChange(blockSlot));
    }
}

