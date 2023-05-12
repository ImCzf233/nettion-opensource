/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.combat;

import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.event.events.world.EventPreUpdate;
import nettion.other.FriendManager;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Option;

import java.util.ArrayList;

import nettion.utils.Rotation;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class BowAimBot
extends Module {
    private Option<Boolean> lockView = new Option<>("lockview", false);
    private Option<Boolean> rota = new Option<>("Rotation", true);
    private Option<Boolean> visual = new Option<>("Visual", true);
    public static ArrayList<Entity> attackList = new ArrayList();
    public static ArrayList<Entity> targets = new ArrayList();
    public static int currentTarget;
    private MovingObjectPosition blockCollision;

    public BowAimBot() {
        super("BowAimbot", ModuleType.Combat);
        this.addValues(this.lockView, rota, visual);
    }

    public boolean isValidTarget(Entity entity) {

        boolean valid = false;
        if (entity == this.mc.thePlayer.ridingEntity) {
            return false;
        }
        if (entity.isInvisible()) {
            valid = true;
        }
        if (FriendManager.isFriend(entity.getName()) && entity instanceof EntityPlayer || !this.mc.thePlayer.canEntityBeSeen(entity)) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            valid = entity != null && this.mc.thePlayer.getDistanceToEntity(entity) <= 50.0f && entity != this.mc.thePlayer && entity.isEntityAlive() && !FriendManager.isFriend(entity.getName());
        }
        return valid;
    }

    public static void func_181561_a(AxisAlignedBB p_181561_0_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }

    private boolean isThrowable(Item item) {
        return item instanceof ItemBow;
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (mc.thePlayer != null && attackList.size() != 0 && attackList.get(currentTarget) != null && this.isValidTarget(attackList.get(currentTarget)) && mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem().getItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
            if (visual.getValue()) {
                int bowCurrentCharge = this.mc.thePlayer.getItemInUseDuration();
                float bowVelocity = (float)bowCurrentCharge / 20.0f;
                bowVelocity = (bowVelocity * bowVelocity + bowVelocity * 2.0f) / 3.0f;
                bowVelocity = MathHelper.clamp_float(bowVelocity, 0.0f, 1.0f);
                double v = bowVelocity * 3.0f;
                double g = 0.05000000074505806;
                if ((double)bowVelocity < 0.1) {
                    return;
                }
                if (bowVelocity > 1.0f) {
                    bowVelocity = 1.0f;
                }
                double xDistance = BowAimBot.attackList.get((int)BowAimBot.currentTarget).posX - this.mc.thePlayer.posX + (BowAimBot.attackList.get((int)BowAimBot.currentTarget).posX - BowAimBot.attackList.get((int)BowAimBot.currentTarget).lastTickPosX) * (double)(bowVelocity * 10.0f);
                double zDistance = BowAimBot.attackList.get((int)BowAimBot.currentTarget).posZ - this.mc.thePlayer.posZ + (BowAimBot.attackList.get((int)BowAimBot.currentTarget).posZ - BowAimBot.attackList.get((int)BowAimBot.currentTarget).lastTickPosZ) * (double)(bowVelocity * 10.0f);
                float trajectoryTheta90 = (float)(Math.atan2(zDistance, xDistance) * 180.0 / 3.141592653589793) - 90.0f;
                float bowTrajectory = (float)((double)((float)(- Math.toDegrees(this.getLaunchAngle((EntityLivingBase)attackList.get(currentTarget), v, g)))) - 3.8);
                if (trajectoryTheta90 <= 360.0f && bowTrajectory <= 360.0f) {
                    if(mc.thePlayer.inventory.getCurrentItem() != null) {
                        EntityPlayerSP player = mc.thePlayer;
                        ItemStack stack = player.inventory.getCurrentItem();
                        if (isThrowable(mc.thePlayer.getHeldItem().getItem())) {
                            double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks - Math.cos(Math.toRadians(player.rotationYaw)) * 0.16F;
                            double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks + player.getEyeHeight() - 0.1D;
                            double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks - Math.sin(Math.toRadians(player.rotationYaw)) * 0.16F;
                            double itemBow = stack.getItem() instanceof ItemBow ? 1.0F : 0.4F;

                            double trajectoryX = -Math.sin(trajectoryTheta90) * Math.cos(bowTrajectory) * itemBow;
                            double trajectoryY = -Math.sin(bowTrajectory) * itemBow;
                            double trajectoryZ =  Math.cos(trajectoryTheta90) * Math.cos(bowTrajectory) * itemBow;
                            double trajectory = Math.sqrt(trajectoryX * trajectoryX + trajectoryY * trajectoryY + trajectoryZ * trajectoryZ);

                            trajectoryX /= trajectory;
                            trajectoryY /= trajectory;
                            trajectoryZ /= trajectory;

                            if (stack.getItem() instanceof ItemBow) {
                                float bowPower = (72000 - player.getItemInUseCount()) / 20.0F;
                                bowPower = (bowPower * bowPower + bowPower * 2.0F) / 3.0F;
                                if (bowPower > 1.0F)
                                {
                                    bowPower = 1.0F;
                                }
                                bowPower *= 3.0F;
                                trajectoryX *= bowPower;
                                trajectoryY *= bowPower;
                                trajectoryZ *= bowPower;
                            } else {
                                trajectoryX *= 1.5D;
                                trajectoryY *= 1.5D;
                                trajectoryZ *= 1.5D;
                            }

                            GL11.glPushMatrix();
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glDisable(GL11.GL_DEPTH_TEST);
                            GL11.glDepthMask(false);
                            GL11.glEnable(GL11.GL_LINE_SMOOTH);
                            GL11.glLineWidth(2.0F);
                            double gravity = stack.getItem() instanceof ItemBow ? 0.05D : 0.03D;
                            GL11.glColor4f(0.3F, 0.6F, 1.0F, 0.5F);
                            GL11.glBegin(GL11.GL_LINE_STRIP);

                            for (int i = 0; i < 2000; i++) {
                                GL11.glVertex3d(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ);

                                posX += trajectoryX * 0.1D;
                                posY += trajectoryY * 0.1D;
                                posZ += trajectoryZ * 0.1D;

                                trajectoryX *= 0.999D;
                                trajectoryY *= 0.999D;
                                trajectoryZ *= 0.999D;

                                trajectoryY = (trajectoryY - gravity * 0.1D);
                                Vec3 vec = new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                                blockCollision = mc.theWorld.rayTraceBlocks(vec, new Vec3(posX, posY, posZ));

                                for (Entity o : mc.theWorld.getLoadedEntityList()) {
                                    if (o instanceof EntityLivingBase && !(o instanceof EntityPlayerSP)) {
                                        EntityLivingBase entity = (EntityLivingBase) o;
                                        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(0.2D, 0.2D, 0.2D);
                                        MovingObjectPosition entityCollision = entityBoundingBox.calculateIntercept(vec, new Vec3(posX, posY, posZ));

                                        if (entityCollision != null) {
                                            blockCollision = entityCollision;
                                        }

                                        if (entityCollision != null) {
                                            GL11.glColor4f(1.0F, 0.4F, 0.4F, 0.5F);
                                        }

                                        if (entityCollision != null) {
                                            blockCollision = entityCollision;
                                        }
                                    }
                                }
                                if (blockCollision != null) {
                                    break;
                                }
                            }
                            GL11.glEnd();
                            double renderX = posX - mc.getRenderManager().renderPosX;
                            double renderY = posY - mc.getRenderManager().renderPosY;
                            double renderZ = posZ - mc.getRenderManager().renderPosZ;
                            GL11.glPushMatrix();
                            GL11.glTranslated(renderX - 0.5D, renderY - 0.5D, renderZ - 0.5D);
                            AxisAlignedBB aim;
                            switch (blockCollision.sideHit.getIndex()) {
                                case 2:
                                case 3:
                                    GlStateManager.rotate(90, 1, 0, 0);
                                    aim = new AxisAlignedBB(0.0D, 0.5D, -1.0D, 1.0D, 0.45D, 0.0D);
                                    break;

                                case 4:
                                case 5:
                                    GlStateManager.rotate(90, 0, 0, 1);
                                    aim = new AxisAlignedBB(0.0D, -0.5D, 0.0D, 1.0D, -0.45D, 1.0D);
                                    break;

                                default:
                                    aim = new AxisAlignedBB(0.0D, 0.5, 0.0D, 1.0D, 0.45D, 1.0D);
                                    break;
                            }
                            //drawBox(aim);
                            func_181561_a(aim);
                            GL11.glPopMatrix();
                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glEnable(GL11.GL_DEPTH_TEST);
                            GL11.glDepthMask(true);
                            GL11.glDisable(GL11.GL_LINE_SMOOTH);
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPre(EventPreUpdate pre) {
        Entity e;
        for (Object o : this.mc.theWorld.loadedEntityList) {
            e = (Entity)o;
            if (e instanceof EntityPlayer && !targets.contains(e)) {
                targets.add(e);
            }
            if (!targets.contains(e) || !(e instanceof EntityPlayer)) continue;
            targets.remove(e);
        }
        if (currentTarget >= attackList.size()) {
            currentTarget = 0;
        }
        for (Object o : this.mc.theWorld.loadedEntityList) {
            e = (Entity)o;
            if (this.isValidTarget(e) && !attackList.contains(e)) {
                attackList.add(e);
            }
            if (this.isValidTarget(e) || !attackList.contains(e)) continue;
            attackList.remove(e);
        }
        this.sortTargets();
        if (mc.thePlayer != null && attackList.size() != 0 && attackList.get(currentTarget) != null && this.isValidTarget(attackList.get(currentTarget)) && mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem().getItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
            int bowCurrentCharge = this.mc.thePlayer.getItemInUseDuration();
            float bowVelocity = (float)bowCurrentCharge / 20.0f;
            bowVelocity = (bowVelocity * bowVelocity + bowVelocity * 2.0f) / 3.0f;
            bowVelocity = MathHelper.clamp_float(bowVelocity, 0.0f, 1.0f);
            double v = bowVelocity * 3.0f;
            double g = 0.05000000074505806;
            if ((double)bowVelocity < 0.1) {
                return;
            }
            if (bowVelocity > 1.0f) {
                bowVelocity = 1.0f;
            }
            double xDistance = BowAimBot.attackList.get((int)BowAimBot.currentTarget).posX - this.mc.thePlayer.posX + (BowAimBot.attackList.get((int)BowAimBot.currentTarget).posX - BowAimBot.attackList.get((int)BowAimBot.currentTarget).lastTickPosX) * (double)(bowVelocity * 10.0f);
            double zDistance = BowAimBot.attackList.get((int)BowAimBot.currentTarget).posZ - this.mc.thePlayer.posZ + (BowAimBot.attackList.get((int)BowAimBot.currentTarget).posZ - BowAimBot.attackList.get((int)BowAimBot.currentTarget).lastTickPosZ) * (double)(bowVelocity * 10.0f);
            float trajectoryTheta90 = (float)(Math.atan2(zDistance, xDistance) * 180.0 / 3.141592653589793) - 90.0f;
            float bowTrajectory = (float)((double)((float)(- Math.toDegrees(this.getLaunchAngle((EntityLivingBase)attackList.get(currentTarget), v, g)))) - 3.8);
            if (trajectoryTheta90 <= 360.0f && bowTrajectory <= 360.0f) {
                if (this.lockView.getValue().booleanValue()) {
                    this.mc.thePlayer.rotationYaw = trajectoryTheta90;
                    this.mc.thePlayer.rotationPitch = bowTrajectory;
                } else {
                    if (rota.getValue()) {
                        Rotation.setRotations(pre, trajectoryTheta90, bowTrajectory);
                    }
                    pre.setYaw(trajectoryTheta90);
                    pre.setPitch(bowTrajectory);
                }
            }
        }
    }

    public void sortTargets() {
        attackList.sort((ent1, ent2) -> {
            double d2;
            double d1 = this.mc.thePlayer.getDistanceToEntity((Entity)ent1);
            return d1 < (d2 = (double)this.mc.thePlayer.getDistanceToEntity((Entity)ent2)) ? -1 : (d1 == d2 ? 0 : 1);
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targets.clear();
        attackList.clear();
        currentTarget = 0;
    }

    private float getLaunchAngle(EntityLivingBase targetEntity, double v, double g) {
        double yDif = targetEntity.posY + (double)(targetEntity.getEyeHeight() / 2.0f) - (this.mc.thePlayer.posY + (double)this.mc.thePlayer.getEyeHeight());
        double xDif = targetEntity.posX - this.mc.thePlayer.posX;
        double zDif = targetEntity.posZ - this.mc.thePlayer.posZ;
        double xCoord = Math.sqrt(xDif * xDif + zDif * zDif);
        return this.theta(v + 2.0, g, xCoord, yDif);
    }

    private float theta(double v, double g, double x, double y) {
        double yv = 2.0 * y * (v * v);
        double gx = g * (x * x);
        double g2 = g * (gx + yv);
        double insqrt = v * v * v * v - g2;
        double sqrt = Math.sqrt(insqrt);
        double numerator = v * v + sqrt;
        double numerator2 = v * v - sqrt;
        double atan1 = Math.atan2(numerator, g * x);
        double atan2 = Math.atan2(numerator2, g * x);
        return (float)Math.min(atan1, atan2);
    }
}

