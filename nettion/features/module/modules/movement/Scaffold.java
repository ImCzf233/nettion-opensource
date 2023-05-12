package nettion.features.module.modules.movement;

import net.minecraft.network.Packet;
import nettion.event.events.world.EventPacketSend;
import nettion.utils.render.RoundedUtils;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.event.events.world.EventPostUpdate;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.fonts.old.Fonts;
import nettion.utils.player.PlayerUtils;
import nettion.utils.render.Colors;
import nettion.utils.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.block.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.Timer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Scaffold extends Module {
   private BlockData data;
   private int slot;
   private static final Rotation rotation = new Rotation(999.0f, 999.0f);
   public final static List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace,
           Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air,
           Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer,
           Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever,
           Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate,
           Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab,
           Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower,
           Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder,
           Blocks.web);
   public static  final Option<Boolean> slow = new Option<>("Slow", true);
   public static  final Numbers<Double> slowSpeed = new Numbers<>("SlowSpeed", 0.11, 0.08, 0.3, 0.01);
   public static  final Numbers<Double> timer = new Numbers<>("Timer", 1.0, 1.0, 3.0, 0.1);
   public static  final Option<Boolean> tower = new Option<>("Tower", false);
   public static final Option<Boolean> safe = new Option<>("SafeWalk", true);
   public static final Option<Boolean> control = new Option<>("Control", true);
   public static  final Mode<Enum> mode = new Mode<>("Mode", mods.values(), mods.Normal);
   public static  final Mode<Enum> placeTime = new Mode<>("PlaceTime", pmod.values(), pmod.Post);
   public static  final Mode<Enum> towerMode = new Mode<>("TowerMode", tMod.values(), tMod.Normal);
   public static  final Mode<Enum> sprintMode = new Mode<>("SprintMode", spmod.values(), spmod.None);
   public static  final Mode<Enum> swing = new Mode<>("SwingMode", sMod.values(), sMod.Normal);
   public static  final Mode<Enum> rotationMod = new Mode<>("Rotation", rMod.values(), rMod.Normal);
   public static  final Numbers<Double> rotYaw = new Numbers<>("RotationYaw", 1.0, -180.0, 180.0, 1.0);
   public static  final Numbers<Double> rotPitch = new Numbers<>("RotationPitch", 1.0, -90.0, 90.0, 1.0);

   private int towerTicks;

   public Scaffold() {
      super("Scaffold", ModuleType.Movement);
      addValues(tower, safe, slow, slowSpeed, timer, mode, placeTime, towerMode, sprintMode, swing, rotationMod, rotYaw, rotPitch, control);
   }

   public enum mods {
      Normal,
   }

   public enum pmod {
      Pre,
      Post,
   }

   public enum spmod {
      Normal,
      None,
   }

   public enum tMod {
      Normal,
      NCP,
   }

   public enum sMod {
      Normal,
      Packet,
      None,
   }

   public enum rMod {
      Normal,
      Custom,
   }

   @EventHandler
   public void onRender(EventRender2D e) {
      if (control.getValue()) {
         final ScaledResolution scaledResolution = new ScaledResolution(mc);
         final String info = getBlockCount() + " blocks";
         int infoWidth = Fonts.R20.getStringWidth(info);
         RoundedUtils.drawRound(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() - 66.55F, (infoWidth) + 8, 16, 4, new Color(0, 0, 0, 100));
         Fonts.R20.drawString(info, (float) (scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 0.4), scaledResolution.getScaledHeight() - 62, Colors.WHITE.c);
      }
   }

   @EventHandler
   public void onPre(EventPreUpdate event) {
      if (getBlockCount() <= 0) {
         return;
      }
      if (sprintMode.getValue() == spmod.None) {
         mc.thePlayer.setSprinting(false);
      }
      if (mc.thePlayer.isMoving()) {
         if (slow.getValue()) {
            mc.thePlayer.setSpeed(slowSpeed.getValue());
         }
         Timer.timerSpeed = timer.getValue().floatValue();
      }
      if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
         mc.thePlayer.motionX = mc.thePlayer.motionX * 0.8F;
         mc.thePlayer.motionZ = mc.thePlayer.motionZ * 0.8F;
      }
      this.data = this.getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) == null ? this.getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ).down(1)) : this.getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ));
      this.slot = getBlockSlot();
      if (this.data == null || this.slot == -1 || this.getBlockCount() <= 0
              || !(PlayerUtils.isMoving() || mc.gameSettings.keyBindJump.isKeyDown())) {
         return;
      }
      // Rotation
      if (rotationMod.getValue() == rMod.Normal) {
         float yaw = -180;
         float pitch = 82.4f;
         if (mc.gameSettings.keyBindBack.isKeyDown() && (!mc.gameSettings.keyBindLeft.isKeyDown() || !mc.gameSettings.keyBindRight.isKeyDown() || !mc.gameSettings.keyBindForward.isKeyDown())) {
            yaw = 0;
         }
         if (mc.gameSettings.keyBindRight.isKeyDown() && (!mc.gameSettings.keyBindForward.isKeyDown() || !mc.gameSettings.keyBindLeft.isKeyDown() || !mc.gameSettings.keyBindBack.isKeyDown())) {
            yaw = -90;
         } else if (mc.gameSettings.keyBindLeft.isKeyDown() && (!mc.gameSettings.keyBindBack.isKeyDown() || !mc.gameSettings.keyBindRight.isKeyDown() || !mc.gameSettings.keyBindForward.isKeyDown())) {
            yaw = 90;
         }
         if (mc.gameSettings.keyBindRight.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown() && (!mc.gameSettings.keyBindLeft.isKeyDown() || !mc.gameSettings.keyBindBack.isKeyDown())) {
            yaw = -135;
         }
         if (mc.gameSettings.keyBindLeft.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown() && (!mc.gameSettings.keyBindRight.isKeyDown() || !mc.gameSettings.keyBindBack.isKeyDown())) {
            yaw = 135;
         }
         if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown() && (!mc.gameSettings.keyBindForward.isKeyDown() || !mc.gameSettings.keyBindLeft.isKeyDown())) {
            yaw = -45;
         }
         if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindLeft.isKeyDown() && (!mc.gameSettings.keyBindForward.isKeyDown() || !mc.gameSettings.keyBindRight.isKeyDown())) {
            yaw = 45;
         }
         mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + yaw;
         mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + yaw;
         event.setYaw(mc.thePlayer.rotationYaw + yaw);
         event.setPitch(pitch);
         rotation.setYaw(mc.thePlayer.rotationYaw + yaw);
         rotation.setPitch(pitch);
         mc.thePlayer.rotationPitchHead = pitch;
      } else if (rotationMod.getValue() == rMod.Custom) {
         mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + rotYaw.getValue().floatValue();
         mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + rotYaw.getValue().floatValue();
         event.setYaw(mc.thePlayer.rotationYaw + rotYaw.getValue().floatValue());
         event.setPitch(rotPitch.getValue().floatValue());
         rotation.setYaw(mc.thePlayer.rotationYaw + rotYaw.getValue().floatValue());
         rotation.setPitch(rotPitch.getValue().floatValue());
         mc.thePlayer.rotationPitchHead = rotPitch.getValue().floatValue();
      }
      // Tower
      if (tower.getValue()) {
         if (mc.gameSettings.keyBindJump.isKeyDown()) {
            if (towerMode.getValue() == tMod.Normal) {
               mc.thePlayer.motionY = 0.4;
            } else if (towerMode.getValue() == tMod.NCP) {
               if(mc.thePlayer.onGround) {
                  towerTicks = 0;
               }
               if(mc.gameSettings.keyBindJump.isKeyDown() && !PlayerUtils.isMoving() && !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                  int IntPosY = (int) mc.thePlayer.posY;
                  if(mc.thePlayer.posY - IntPosY < 0.05) {
                     mc.thePlayer.setPosition(mc.thePlayer.posX, IntPosY, mc.thePlayer.posZ);
                     mc.thePlayer.motionY = 0.42;
                     towerTicks = 1;
                  } else if(towerTicks == 1) {
                     mc.thePlayer.motionY = 0.34;
                     towerTicks++;
                  } else if(towerTicks == 2) {
                     mc.thePlayer.motionY = 0.25;
                     towerTicks++;
                  }
               }
            }
         }
      }

      if (placeTime.getValue() == pmod.Pre) {
         int last = mc.thePlayer.inventory.currentItem;
         mc.thePlayer.inventory.currentItem = this.slot;
         if (data != null) {
            if (Minecraft.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), this.data.getEnumFacing(), getVec3(this.data.getBlockPos(), this.data.getEnumFacing()))) {
               if (swing.getValue() == sMod.Normal) {
                  mc.thePlayer.swingItem();
               } else if (swing.getValue() == sMod.Packet) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
               }
            }
            mc.thePlayer.inventory.currentItem = last;
         }
      }
   }

   @EventHandler
   public void onPacketSend(EventPacketSend event) {
      if (getBlockCount() <= 0) {
         return;
      }
      Packet<?> packet = event.getPacket();
      // Tower
      if (tower.getValue()) {
      }
   }

   @EventHandler
   public void onPost(EventPostUpdate event) {
      if (getBlockCount() <= 0) {
         return;
      }
      if (placeTime.getValue() == pmod.Post) {
         int last = mc.thePlayer.inventory.currentItem;
         mc.thePlayer.inventory.currentItem = this.slot;
         if (data != null) {
            if (Minecraft.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), this.data.getEnumFacing(), getVec3(this.data.getBlockPos(), this.data.getEnumFacing()))) {
               if (swing.getValue() == sMod.Normal) {
                  mc.thePlayer.swingItem();
               } else if (swing.getValue() == sMod.Packet) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
               }
            }
            mc.thePlayer.inventory.currentItem = last;
         }
      }
   }

   @Override
   public void onEnable() {
      this.data = null;
      towerTicks = 0;
      this.slot = -1;
      rotation.setYaw(999.0f);
      rotation.setPitch(999.0f);
   }

   @Override
   public void onDisable() {
      Timer.timerSpeed = 1;
      rotation.setYaw(999.0f);
      rotation.setPitch(999.0f);
   }

   public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
      double x = (double) pos.getX() + 0.5;
      double y = (double) pos.getY() + 0.5;
      double z = (double) pos.getZ() + 0.5;
      if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
         x += randomNumber((double) 0.3, (double) -0.3);
         z += randomNumber((double) 0.3, (double) -0.3);
      } else {
         y += randomNumber((double) 0.3, (double) -0.3);
      }
      if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
         z += randomNumber(0.3, -0.3);
      }
      if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
         x += randomNumber(0.3, -0.3);
      }
      return new Vec3(x, y, z);
   }

   public static int getBlockSlot() {
      for (int i = 0; i < 9; ++i) {
         if (!mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack()
                 || !(mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack()
                 .getItem() instanceof ItemBlock))
            continue;
         return i;
      }
      return -1;
   }

   private BlockData getBlockData(BlockPos pos) {
      if (this.isPosSolid(pos.add(0, -1, 0))) {
         return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos.add(-1, 0, 0))) {
         return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos.add(1, 0, 0))) {
         return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos.add(0, 0, 1))) {
         return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos.add(0, 0, -1))) {
         return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos1 = pos.add(-1, 0, 0);
      if (this.isPosSolid(pos1.add(0, -1, 0))) {
         return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos1.add(-1, 0, 0))) {
         return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos1.add(1, 0, 0))) {
         return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos1.add(0, 0, 1))) {
         return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos1.add(0, 0, -1))) {
         return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos2 = pos.add(1, 0, 0);
      if (this.isPosSolid(pos2.add(0, -1, 0))) {
         return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos2.add(-1, 0, 0))) {
         return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos2.add(1, 0, 0))) {
         return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos2.add(0, 0, 1))) {
         return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos2.add(0, 0, -1))) {
         return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos3 = pos.add(0, 0, 1);
      if (this.isPosSolid(pos3.add(0, -1, 0))) {
         return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos3.add(-1, 0, 0))) {
         return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos3.add(1, 0, 0))) {
         return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos3.add(0, 0, 1))) {
         return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos3.add(0, 0, -1))) {
         return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos4 = pos.add(0, 0, -1);
      if (this.isPosSolid(pos4.add(0, -1, 0))) {
         return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos4.add(-1, 0, 0))) {
         return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos4.add(1, 0, 0))) {
         return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos4.add(0, 0, 1))) {
         return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos4.add(0, 0, -1))) {
         return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
      }
      if (this.isPosSolid(pos1.add(0, -1, 0))) {
         return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos1.add(-1, 0, 0))) {
         return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos1.add(1, 0, 0))) {
         return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos1.add(0, 0, 1))) {
         return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos1.add(0, 0, -1))) {
         return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
      }
      if (this.isPosSolid(pos2.add(0, -1, 0))) {
         return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos2.add(-1, 0, 0))) {
         return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos2.add(1, 0, 0))) {
         return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos2.add(0, 0, 1))) {
         return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos2.add(0, 0, -1))) {
         return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
      }
      if (this.isPosSolid(pos3.add(0, -1, 0))) {
         return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos3.add(-1, 0, 0))) {
         return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos3.add(1, 0, 0))) {
         return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos3.add(0, 0, 1))) {
         return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos3.add(0, 0, -1))) {
         return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
      }
      if (this.isPosSolid(pos4.add(0, -1, 0))) {
         return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos4.add(-1, 0, 0))) {
         return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos4.add(1, 0, 0))) {
         return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos4.add(0, 0, 1))) {
         return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos4.add(0, 0, -1))) {
         return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos5 = pos.add(0, -1, 0);
      if (this.isPosSolid(pos5.add(0, -1, 0))) {
         return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos5.add(-1, 0, 0))) {
         return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos5.add(1, 0, 0))) {
         return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos5.add(0, 0, 1))) {
         return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos5.add(0, 0, -1))) {
         return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos6 = pos5.add(1, 0, 0);
      if (this.isPosSolid(pos6.add(0, -1, 0))) {
         return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos6.add(-1, 0, 0))) {
         return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos6.add(1, 0, 0))) {
         return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos6.add(0, 0, 1))) {
         return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos6.add(0, 0, -1))) {
         return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos7 = pos5.add(-1, 0, 0);
      if (this.isPosSolid(pos7.add(0, -1, 0))) {
         return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos7.add(-1, 0, 0))) {
         return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos7.add(1, 0, 0))) {
         return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos7.add(0, 0, 1))) {
         return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos7.add(0, 0, -1))) {
         return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos8 = pos5.add(0, 0, 1);
      if (this.isPosSolid(pos8.add(0, -1, 0))) {
         return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos8.add(-1, 0, 0))) {
         return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos8.add(1, 0, 0))) {
         return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos8.add(0, 0, 1))) {
         return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos8.add(0, 0, -1))) {
         return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
      }
      BlockPos pos9 = pos5.add(0, 0, -1);
      if (this.isPosSolid(pos9.add(0, -1, 0))) {
         return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
      }
      if (this.isPosSolid(pos9.add(-1, 0, 0))) {
         return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
      }
      if (this.isPosSolid(pos9.add(1, 0, 0))) {
         return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
      }
      if (this.isPosSolid(pos9.add(0, 0, 1))) {
         return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
      }
      if (this.isPosSolid(pos9.add(0, 0, -1))) {
         return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
      }
      return null;
   }

   private boolean isPosSolid(BlockPos pos) {
      Block block = mc.theWorld.getBlockState(pos).getBlock();
      return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque()
              || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow
              || block instanceof BlockSkull) && !block.getMaterial().isLiquid()
              && !(block instanceof BlockContainer);
   }

   public int getBlockCount() {
      int n = 0;
      int i = 36;
      while (i < 45) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            final Item item = stack.getItem();
            if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
               n += stack.stackSize;
            }
         }
         ++i;
      }
      return n;
   }

   private boolean isValid(final Item item) {
      return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) item).getBlock());
   }

   public static double randomNumber(double max, double min) {
      return Math.random() * (max - min) + min;
   }

   private static class BlockData {
      private final BlockPos pos;
      private final EnumFacing facing;

      public BlockData(final BlockPos pos, final EnumFacing facing) {
         this.pos = pos;
         this.facing = facing;
      }

      public BlockPos getBlockPos() {
         return this.pos;
      }

      public EnumFacing getEnumFacing() {
         return this.facing;
      }
   }
}

