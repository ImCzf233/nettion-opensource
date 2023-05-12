package nettion.features.module.modules.combat;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import nettion.event.events.world.*;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.other.FriendManager;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.movement.Scaffold;
import nettion.features.module.modules.player.Teams;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;
import nettion.utils.player.EntityUtils;
import nettion.utils.player.PacketUtils;
import nettion.utils.Rotation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import nettion.utils.time.DelayTimer;
import org.lwjgl.opengl.GL11;
import nettion.Nettion;
import nettion.utils.time.TimerUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import nettion.utils.render.RenderUtils;

import static java.lang.Math.*;

public class Killaura extends Module {
    public static EntityLivingBase target;
    private List targets = new ArrayList(0);
    public static Mode<Enum> mode = new Mode<>("Mode", AuraMode.values(), AuraMode.Switch);
    private final Numbers<Double> auraCps = new Numbers<>("CPS", 11.0, 1.0, 20.0, 1.0);
    private final Numbers<Double> range = new Numbers<>("Range", 4.4, 1.0, 8.0, 0.1);
    private final Numbers<Double> fov = new Numbers<>("Fov", 180.0, 30.0, 180.0, 1.0);
    private final Option<Boolean> players = new Option<>("Players", true);
    private final Option<Boolean> animals = new Option<>("Animals", false);
    private final Option<Boolean> mobs = new Option<>("Mobs", false);
    private final Option<Boolean> invisible = new Option<>("Invisible", false);
    private final Option<Boolean> twall = new Option<>("Through Wall", true);
    public static Mode<Enum> blockmode = new Mode<>("BlockMode", BlockMode.values(), BlockMode.Vanilla);
    public static Mode<Enum> rotationmode = new Mode<>("Rotation", RotMode.values(), RotMode.Normal);
    public static Mode<Enum> atime = new Mode<>("AttackTime", AT.values(), AT.Pre);
    public static Mode<Enum> btime = new Mode<>("BlockTime", BT.values(), BT.Post);
    public static Mode<Enum> swingMod = new Mode<>("Swing", swingMode.values(), swingMode.Normal);
    public static final Option<Boolean> keepSprint = new Option<>("KeepSprint", true);
    private final Option<Boolean> inventoryCheck = new Option<>("InventoryCheck", false);
    private final Option<Boolean> nosa = new Option<>("ScaffoldCheck",  true);
    private final Option<Boolean> autodis = new Option<>("AutoDisabler", true);
    public static Mode<Enum> esp = new Mode<>("ESP", espMode.values(), espMode.Nettion);
    private final Option<Boolean> visRange = new Option<>("Visualize Range", false);
    public static Boolean isBlocking = false;
    private float iyaw;
    private float ipitch;
    private final Comparator<Entity> angleComparator = Comparator.comparingDouble(e2 -> e2.getDistanceToEntity(mc.thePlayer));

    public Killaura() {
        super("KillAura", ModuleType.Combat);
        addValues(mode,
                auraCps,
                range,
                fov,
                players,
                animals,
                mobs,
                invisible,
                twall,
                blockmode,
                rotationmode,
                atime,
                btime,
                swingMod,
                keepSprint,
                inventoryCheck,
                nosa,
                autodis, esp, visRange);
    }

    @Override
    public void onDisable() {
        target = null;
        if (isBlocking) {
            unBlock();
        }
    }

    @Override
    public void onEnable() {
        this.iyaw = mc.thePlayer.rotationYaw;
        this.ipitch = mc.thePlayer.rotationPitch;
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        if (this.visRange.getValue()) {
            RenderUtils.pre3D();
            GL11.glLineWidth(1.0f);
            GL11.glBegin(3);
            GL11.glColor4f(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);
            for (double d = 0.0; d < 6.283185307179586; d += 0.12319971190548208) {
                final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.getPartialTicks() + sin(d) * this.range.getValue() - RenderManager.renderPosX;
                final double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.getPartialTicks() - RenderManager.renderPosY;
                final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.getPartialTicks() + cos(d) * this.range.getValue() - RenderManager.renderPosZ;
                GL11.glVertex3d(x, y, z);
            }
            GL11.glEnd();
            RenderUtils.post3D();
        }
        // ESP
        if(target != null) {
            if (esp.getValue() == espMode.Nettion) {
                RenderUtils.drawTargetCapsule(target, 0.5, true);
            } else if (esp.getValue() == espMode.LiquidBounce) {
                Color color = target.hurtTime > 0?new Color(-1618884):new Color(-13330213);
                double x;
                double y;
                double z;
                x = Killaura.target.lastTickPosX + (Killaura.target.posX - Killaura.target.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
                mc.getRenderManager();
                y = Killaura.target.lastTickPosY + (Killaura.target.posY - Killaura.target.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
                mc.getRenderManager();
                z = Killaura.target.lastTickPosZ + (Killaura.target.posZ - Killaura.target.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                x -= 0.5;
                z -= 0.5;
                y += Killaura.target.getEyeHeight() + 0.35 - (Killaura.target.isSneaking() ? 0.25 : 0.0);
                final double mid = 0.5;
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glTranslated(x + mid, y + mid, z + mid);
                GL11.glRotated(-Killaura.target.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
                GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
                GL11.glLineWidth(2.0f);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
                RenderUtils.drawBoundingBox(new AxisAlignedBB(x + 0.2, y - 0.04, z + 0.2, x + 0.8, y + 0.01, z + 0.8));
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }
    }

    private TimerUtils SwitchTimer = new TimerUtils();

    @EventHandler
    private void onPacket(EventPacketReceive e) {
        Packet<?> packet = e.getPacket();
    }

    private final DelayTimer cps = new DelayTimer();

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix("Switch");
        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
            return;
        }
        if (this.nosa.getValue() && Nettion.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
            return;
        }
        this.targets = this.getTargets();
        if (target instanceof EntityPlayer || target instanceof EntityMob || target instanceof EntityAnimal) {
            target = null;
            if (isBlocking) {
                unBlock();
            }
        }
        targets.sort(this.angleComparator);
        if (targets.size() > 0) {
            if (mode.getValue() == AuraMode.Switch) {
                if (SwitchTimer.delay(100L)) {
                    SwitchTimer.reset();
                }
                if (target != null) {
                    target = null;
                }
            }
            target = (EntityLivingBase) this.targets.get(0);
            if (target != null) {
                if (rotationmode.getValue() == RotMode.Normal) {
                    float[] rotations = Rotation.getAngles(target);
                    float rotation1 = (float)((double)rotations[0]);
                    float rotation2 = rotations[1] + 1;
                    Rotation.setRotations(event, rotation1, rotation2);
                } else if (rotationmode.getValue() == RotMode.Smooth) {
                    float frac = MathHelper.clamp_float(1.0f - 50 / 100.0f, 0.1f, 1.0f);
                    float[] rotations = Rotation.getAngles(target);
                    this.iyaw += (rotations[0] - this.iyaw) * frac;
                    this.ipitch += (rotations[1] - this.ipitch) * frac;
                    event.setYaw(iyaw);
                    event.setPitch(ipitch);
                    Rotation.setRotations(event, iyaw, ipitch);
                }
                if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
                    if (target.isEntityAlive()) {
                        if (atime.getValue() == AT.Pre) {
                            int delayValue = (20 / ((Number) auraCps.getValue()).intValue()) * 50;
                            if (cps.check(delayValue)) {
                                Attack(target);
                                cps.reset();
                            }
                        }
                        if (!(blockmode.getValue() == BlockMode.None)) {
                            if (btime.getValue() == BT.Pre) {
                                block();
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onWorldChanged(EventWorldLoad e) {
        if (this.autodis.getValue()) {
            NotificationManager.post(NotificationType.INFO, "Info", "KillAura has been disabled.", 2.5F);
            this.setEnabled(false);
        }
    }

    @EventHandler
    private void onPost(EventPostUpdate event) {
        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
            return;
        }
        if (targets.size() > 0) {
            if (mode.getValue() == AuraMode.Switch) {
                if (SwitchTimer.delay(100L)) {
                    SwitchTimer.reset();
                }
                if (target != null) {
                    target = null;
                }
            }
            target = (EntityLivingBase) this.targets.get(0);
            if (target != null) {
                if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
                    if (target.isEntityAlive()) {
                        if (atime.getValue() == AT.Post) {
                            int delayValue = (20 / ((Number) auraCps.getValue()).intValue()) * 50;
                            if (cps.check(delayValue)) {
                                Attack(target);
                                cps.reset();
                            }
                        }
                        if (!(blockmode.getValue() == BlockMode.None)) {
                            if (btime.getValue() == BT.Post) {
                                block();
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onUpdateAttack(EventUpdate e) {
        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
            return;
        }
        if (targets.size() > 0) {
            if (mode.getValue() == AuraMode.Switch) {
                if (SwitchTimer.delay(100L)) {
                    SwitchTimer.reset();
                }
                if (target != null) {
                    target = null;
                }
            }
            target = (EntityLivingBase) this.targets.get(0);
            if (target != null) {
                if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
                    if (target.isEntityAlive()) {
                        if (atime.getValue() == AT.All) {
                            int delayValue = (20 / ((Number) auraCps.getValue()).intValue()) * 50;
                            if (cps.check(delayValue)) {
                                Attack(target);
                                cps.reset();
                            }
                        }
                        if (!(blockmode.getValue() == BlockMode.None)) {
                            if (btime.getValue() == BT.All) {
                                block();
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onTickAttack(EventTick e) {
        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
            return;
        }
        if (this.nosa.getValue() && Nettion.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
            return;
        }
        if (targets.size() > 0) {
            if (mode.getValue() == AuraMode.Switch) {
                if (SwitchTimer.delay(100L)) {
                    SwitchTimer.reset();
                }
                if (target != null) {
                    target = null;
                }
            }
            target = (EntityLivingBase) this.targets.get(0);
            if (target != null) {
                if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
                    if (target.isEntityAlive()) {
                        if (atime.getValue() == AT.Tick) {
                            int delayValue = (20 / ((Number) auraCps.getValue()).intValue()) * 50;
                            if (cps.check(delayValue)) {
                                Attack(target);
                                cps.reset();
                            }
                        }
                        if (!(blockmode.getValue() == BlockMode.None)) {
                            if (btime.getValue() == BT.Tick) {
                                block();
                            }
                        }
                    }
                }
            }
        }
    }

    private void Attack(EntityLivingBase e) {
        if (swingMod.getValue() == swingMode.Normal) {
            mc.thePlayer.swingItem();
        } else if (swingMod.getValue() == swingMode.Packet) {
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }
        if (!this.keepSprint.getValue()) {
            Minecraft.playerController.attackEntity(mc.thePlayer, e);
        } else {
            PacketUtils.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        }
    }

    private void unBlock() {
        if (isBlocking && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && (!(blockmode.getValue() == BlockMode.None))) {
            if (blockmode.getValue() != BlockMode.Vanilla) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            if (blockmode.getValue() == BlockMode.Vanilla) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }
            isBlocking = false;
        }
    }

    private void block() {
        if (target != null) {
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                if (blockmode.getValue() == BlockMode.Normal) {
                    PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    isBlocking = true;
                } else if (blockmode.getValue() == BlockMode.Vanilla) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    isBlocking = true;
                }
            }
        }
    }

    public List<Entity> getTargets() {
        return mc.theWorld.loadedEntityList.stream().filter(e -> (double) mc.thePlayer.getDistanceToEntity((Entity) e) <= range.getValue() && CanAttack((Entity) e)).collect(Collectors.toList());
    }

    private boolean CanAttack(Entity entity) {
        if (entity == mc.thePlayer) {
            return false;
        }
        if(!EntityUtils.canEntityBeSeen(entity) && !twall.getValue()) {
            return false;
        }
        AntiBot ab = (AntiBot) Nettion.instance.getModuleManager().getModuleByClass(AntiBot.class);
        if (entity != null && !Rotation.isVisibleFOV(entity, fov.getValue().intValue())) {
            return false;
        }
        if (ab.isServerBot(entity) && ab.isEnabled()) {
            return false;
        }
        if (!entity.isEntityAlive()) {
            return false;
        }
        if (FriendManager.isFriend(entity.getName())) {
            return false;
        }
        if (entity instanceof EntityPlayer && players.getValue().booleanValue() && !Teams.isOnSameTeam(entity)) {
            return true;
        }
        if (entity instanceof EntityMob && mobs.getValue().booleanValue()) {
            return true;
        }
        if (entity instanceof EntityAnimal && animals.getValue().booleanValue()) {
            return true;
        }
        if (entity.isInvisible() && invisible.getValue().booleanValue() && entity instanceof EntityPlayer) {
            return true;
        }
        return false;
    }

    public enum AuraMode {
        Switch,
        Legit,
    }

    public enum BlockMode {
        Normal,
        Vanilla,
        Fake,
        None,
    }

    public enum RotMode {
        Normal,
        Smooth,
        Test
    }

    public enum AT {
        All,
        Pre,
        Post,
        Tick,
    }

    public enum BT {
        All,
        Pre,
        Post,
        Tick,
    }

    public enum swingMode {
        Normal,
        Packet,
        None,
    }

    public enum espMode {
        Nettion,
        LiquidBounce,
        None
    }
}