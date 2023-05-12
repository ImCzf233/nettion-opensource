package nettion.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import nettion.features.module.modules.player.Teams;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.other.FriendManager;
import nettion.utils.math.AcpF;
import nettion.utils.Vec3;
import nettion.utils.render.ColorUtils;
import nettion.utils.render.RenderUtils;
import nettion.utils.time.DelayTimer;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TPAura extends Module {
    public static final Option<Boolean> PLAYERS = new Option<>("Players", true);
    public static final Option<Boolean> ANIMALS = new Option<>("Animals", false);
    public static final Option<Boolean> TEAMS = new Option<>("Teams", true);
    public static final Option<Boolean> INVISIBLES = new Option<>("Invisibles", false);
    public static final Option<Boolean> ESP = new Option<>("ESP", true);
    public static final Numbers<Double> RANGE = new Numbers<>("Range", 30.0, 10.0, 100.0, 5.0);
    public static final Numbers<Double> CPS = new Numbers<>("CPS", 7.0, 1.0, 20.0, 1.0);
    public static final Numbers<Double> MAXT = new Numbers<>("MaxTarget", 5.0, 1.0, 50.0, 1.0);
    private final double dashDistance = 5;
    private ArrayList<Vec3> path = new ArrayList<>();
    private List<Vec3>[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private final DelayTimer cps = new DelayTimer();
    public static DelayTimer delayTimer = new DelayTimer();

    public TPAura() {
        super("TPAura", ModuleType.Combat);
        addValues(PLAYERS, ANIMALS, TEAMS, INVISIBLES, ESP, RANGE, CPS, MAXT);
    }

    @Override
    public void onEnable() {
        delayTimer.reset();
        targets.clear();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        this.setSuffix(RANGE.getValue());
        int maxtTargets = ((Number) MAXT.getValue()).intValue();
        int delayValue = (20 / ((Number) CPS.getValue()).intValue()) * 50;
        targets = getTargets();
        if (cps.check(delayValue)) {
            if (targets.size() > 0) {
                test = new ArrayList[50];
                for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                    EntityLivingBase T = targets.get(i);
                    Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                    path = computePath(topFrom, to);
                    test[i] = path;
                    for (Vec3 pathElm : path) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                    }
                    mc.thePlayer.swingItem();
                    mc.playerController.attackEntity(mc.thePlayer, T);
                    Collections.reverse(path);
                    for (Vec3 pathElm : path) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                    }
                }
                cps.reset();
            }
        }
    }



    @EventHandler
    private void onPacket(EventPacketSend e) {
        Packet packet = e.getPacket();
    }

    @EventHandler
    private void onRender3D(EventRender3D event) {
        int maxtTargets = ((Number) MAXT.getValue()).intValue();
        if (!targets.isEmpty() && ESP.getValue()) {
            if (targets.size() > 0) {
                for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                    int color = targets.get(i).hurtResistantTime > 15 ? ColorUtils.getColor(new Color(255, 70, 70, 80)) : ColorUtils.getColor(new Color(255, 255, 255, 80));
                    drawESP(targets.get(i), color);
                }
            }
        }
    }

    private void drawLine(double[] color, double x, double y, double z) {
        GL11.glEnable(2848);
        if (color.length >= 4) {
            if (color[3] <= 0.1) {
                return;
            }
            GL11.glColor4d(color[0], color[1], color[2], color[3]);
        } else {
            GL11.glColor3d(color[0], color[1], color[2]);
        }
        GL11.glLineWidth(1.0f);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(2848);
    }

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        AcpF pathfinder = new AcpF(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AcpF.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }


    boolean validEntity(EntityLivingBase entity) {
        float range = ((Number) RANGE.getValue()).floatValue();
        boolean players = PLAYERS.getValue();
        boolean animals = ANIMALS.getValue();

        if ((mc.thePlayer.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {
                if (entity.isPlayerSleeping()) {
                    return false;
                }
                if (FriendManager.isFriend(entity.getName())) {
                    return false;
                }

                if (entity instanceof EntityPlayer) {
                    if (players) {
                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive()
                                && player.getHealth() == 0.0) {
                            return false;
                        } else if (Teams.isOnSameTeam(player)
                                && (Boolean) TEAMS.getValue()) {
                            return false;
                        } else if (player.isInvisible()
                                && !(Boolean) INVISIBLES.getValue()) {
                            return false;
                        } else if (FriendManager.isFriend(player.getName())) {
                            return false;
                        } else
                            return true;
                    }
                } else {
                    if (!entity.isEntityAlive()) {
                        return false;
                    }
                }
                if (entity instanceof EntityMob && animals) {
                    return true;
                }
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager) && animals) {
                    if (entity.getName().equals("Villager")) {
                        return false;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (validEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }

    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX);
        double height = Math.abs(entity.boundingBox.maxY - entity.boundingBox.minY);
        Vec3 vec = new Vec3(x - width / 2, y, z - width / 2);
        Vec3 vec2 = new Vec3(x + width / 2, y + height, z + width / 2);
        RenderUtils.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderUtils.glColor(color);
        RenderUtils.drawBoundingBox(new AxisAlignedBB(vec.getX() - RenderManager.renderPosX, vec.getY() - RenderManager.renderPosY, vec.getZ() - RenderManager.renderPosZ, vec2.getX() - RenderManager.renderPosX, vec2.getY() - RenderManager.renderPosY, vec2.getZ() - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderUtils.post3D();
    }

    public void drawPath(Vec3 vec) {
        double x = vec.getX() - RenderManager.renderPosX;
        double y = vec.getY() - RenderManager.renderPosY;
        double z = vec.getZ() - RenderManager.renderPosZ;
        double width = 0.3;
        double height = mc.thePlayer.getEyeHeight() - 0.5;
        RenderUtils.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        int colors[] = {ColorUtils.getColor(Color.black), ColorUtils.getColor(Color.white)};
        for (int i = 0; i < 2; i++) {
            RenderUtils.glColor(colors[i]);
            //GL11.glLineWidth(3 - i * 2);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            //GL11.glVertex3d(x - width, y, z - width);
            //GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glVertex3d(x + width, y + height, z - width);
            //GL11.glVertex3d(x + width, y, z - width);
            //GL11.glVertex3d(x - width, y, z - width);
            //GL11.glVertex3d(x - width, y, z + width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            //GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z + width);
            //GL11.glVertex3d(x - width, y, z + width);
            //GL11.glVertex3d(x + width, y, z + width);
            //GL11.glVertex3d(x + width, y, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glEnd();
            /* *

            * */
        }
        RenderUtils.post3D();
    }
}
