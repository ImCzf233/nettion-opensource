package nettion.features.module.modules.combat;

import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.other.FriendManager;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.player.Teams;
import nettion.utils.Rotation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import nettion.Nettion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoPVP extends Module {
    private Numbers<Double> cps = new Numbers<Double>("CPS", 13.0, 1.0, 20.0, 1.0);
    private Numbers<Double> reach = new Numbers<Double>("AimReach", 4.5, 1.0, 200.0, 0.1);
    private Option<Boolean> sumo = new Option<Boolean>("Sumo", true);
    private Option<Boolean> py = new Option<Boolean>("Players", true);
    private Option<Boolean> am = new Option<Boolean>("Animals", false);
    private Option<Boolean> mb = new Option<Boolean>("Mobs", false);
    private Option<Boolean> iv = new Option<Boolean>("Invisible", false);
    private int ticks = 0;
    EntityLivingBase entity;
    private List targets = new ArrayList(0);
    int FuckYou;
    int tick;
    public AutoPVP() {
        super("AutoPVP", ModuleType.Combat);
        addValues(cps, reach, sumo);
    }

    @Override
    public void onEnable() {
        if (sumo.getValue()) {
            mc.thePlayer.setSneaking(false);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.gameSettings.keyBindForward.getKeyCode() < 0) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Mouse.isButtonDown(mc.gameSettings.keyBindForward.getKeyCode()+100));
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
        }
        if (sumo.getValue()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
        super.onDisable();
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        this.targets = this.loadTargets();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        ++this.ticks;
        if (this.ticks >= 1 - this.speed()) {
            this.ticks = 0;
            if (!this.targets.isEmpty()) {
                for (Object theObject : mc.theWorld.loadedEntityList) {
                    if (!(theObject instanceof EntityLivingBase) || (entity = (EntityLivingBase)theObject) instanceof EntityPlayerSP || mc.thePlayer.getDistanceToEntity(entity) > reach.getValue()) continue;
                    if (entity.isInvisible()) break;
                    if (!entity.isEntityAlive()) continue;
                    faceEntity(entity);
                }
            }
        }
        FuckYou += cps.getValue();
        if (FuckYou >= 20) {
            try {
                if (mc.thePlayer.getDistanceToEntity(entity) < 10) {
                    mc.thePlayer.swingItem();
                    tick ++;
                    Minecraft.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                }
            } catch (Exception ignored) {

            }
        }
        if (entity.isDead || entity.getHealth() <= 0) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
            this.onDisable();
        }
        if (entity == null) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
        }
        if (sumo.getValue()) {
            if(getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
                if(mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else {
                if(mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
            }
        }
    }

    @EventHandler
    private void onJump(EventPreUpdate e) {
        if (mc.thePlayer.onGround) {
            if (mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.jump();
            }
        }
    }

    public static synchronized void faceEntity(EntityLivingBase entity) {
        float[] rotations = getRotationsNeeded(entity);
        float[] rotations2 = Rotation.getAngles(entity);
        if (rotations != null) {
            mc.thePlayer.rotationYaw = rotations[0];
            if (entity.fallDistance >= 0.4) {
                mc.thePlayer.rotationPitch = rotations[1] + 8.0f;
            } else {
                mc.thePlayer.rotationPitch = rotations2[1] + 1;
            }
        }
    }

    private static float[] getRotationsNeeded(EntityLivingBase entity) {
        double diffY;
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = entity;
            diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        }
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }

    private List<Entity> loadTargets() {
        return mc.theWorld.loadedEntityList.stream().filter(e -> (double)this.mc.thePlayer.getDistanceToEntity(e) <= this.reach.getValue() && this.theTarget((Entity)e)).collect(Collectors.toList());
    }

    private boolean theTarget(Entity e) {
        if (e == mc.thePlayer) {
            return false;
        }
        AntiBot ab = (AntiBot) Nettion.instance.getModuleManager().getModuleByClass(AntiBot.class);
        if (ab.isEnabled() && ab.isServerBot(e)) {
            return false;
        }
        if (FriendManager.isFriend(e.getName())) {
            return false;
        }
        if (e instanceof EntityPlayer && this.py.getValue().booleanValue() && !Teams.isOnSameTeam(e)) {
            return true;
        }
        if (e instanceof EntityMob && this.mb.getValue().booleanValue()) {
            return true;
        }
        if (e instanceof EntityAnimal && this.am.getValue().booleanValue()) {
            return true;
        }
        if (e.isInvisible() && this.iv.getValue().booleanValue()) {
            return true;
        }
        return false;
    }

    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return getBlock(new BlockPos(player.posX , player.posY - 1.0d, player.posZ));
    }

    private int speed() {
        return 5;
    }
}
