/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package nettion.features.module.modules.render;

import cc.novoline.ColorProperty;
import cc.novoline.IntProperty;
import cc.novoline.PropertyFactory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import nettion.event.events.render.EventBloom;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.ModuleManager;
import nettion.features.module.modules.combat.AntiBot;
import nettion.features.module.modules.player.Teams;
import nettion.utils.render.Colors;
import nettion.utils.render.RenderUtils;
import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.entity.player.EntityPlayer;
import nettion.utils.time.TimerUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

import static cc.novoline.PropertyFactory.createColor;

public class ESP extends Module {

    private final Color BLACK = new Color(0, 0, 0, 100);

    private TimerUtils tpTimer = new TimerUtils();

    /* properties @off */
    private final List<EntityPlayer> collectedEntities = new ArrayList<>();

    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);

    private final Mode mode = new Mode("Mode", espMode.values(), espMode.Box);

    private final ColorProperty color = createColor(0xFF8A8AFF);
    private final IntProperty transparency = PropertyFactory.createInt(100).minimum(0).maximum(255);
    Vector4d position2;

    public ESP() {
        super("ESP", ModuleType.Render);
        addValues(mode);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(mode.getValue());
    }

    @EventHandler
    private void onRender(EventRender2D event) {
        {
            GL11.glPushMatrix();
            this.collectedEntities.clear();
            collectEntities();

            final double scaling = event.getSR().getScaleFactor() / Math
                    .pow(event.getSR().getScaleFactor(), 2.0);
            GlStateManager.scale(scaling, scaling, scaling);

            for (EntityPlayer entity : this.collectedEntities) {
                //if (onlyTargets.get() && !novoline.getPlayerManager().hasType(entity.getName(), PlayerManager.EnumPlayerType.TARGET))
                //    continue;
                if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0)
                    continue;
                if (isValid(entity) && RenderUtils.isInViewFrustrum(entity)) {
                    final double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, event.getPartialTicks()), // @off
                            y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, event.getPartialTicks()),
                            z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, event.getPartialTicks()),
                            width = entity.width / 1.4,
                            height = entity.height + 0.2; // @on

                    AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                    List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                            new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                            new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                            new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                            new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

                    mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

                    Vector4d position = null;

                    for (Vector3d vector : vectors) {
                        vector = project2D(event.getSR(), vector.x - mc.getRenderManager().viewerPosX,
                                vector.y - mc.getRenderManager().viewerPosY,
                                vector.z - mc.getRenderManager().viewerPosZ);

                        if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                            if (position == null) {
                                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                            }

                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    mc.entityRenderer.setupOverlayRendering();

                    if (position != null) {
                        position2 = position;
                        double posX = position.x, // @off
                                posY = position.y,
                                endPosX = position.z,
                                endPosY = position.w,
                                length = Math.abs(endPosY - posY),
                                difference = posY - endPosY + 0.5; // @on


                        float amp = 1;
                        switch (mc.gameSettings.guiScale) {
                            case 0:
                                amp = 0.5F;
                                break;
                            case 1:
                                amp = 2.0F;
                                break;
                            case 3:
                                amp = 0.6666666666666667F;
                        }

                        double[] positions = PropertyFactory.ScaleUtils.getScaledMouseCoordinates(mc, posX, posY);
                        double[] positionsEnd = PropertyFactory.ScaleUtils.getScaledMouseCoordinates(mc, endPosX, endPosY);
                        double[] scaledPositions = new double[]{positions[0] * 2, positions[1] * 2, positionsEnd[0] * 2, positionsEnd[1] * 2};

                        final ScaledResolution resolution = new ScaledResolution(mc);

                        float width1 = resolution.getScaledWidth() / 2F;
                        float height1 = resolution.getScaledHeight() / 2F;
                        double centreX = posX + (endPosX - posX) / 2;
                        double centreY = posY + (endPosY - posY) / 2;

                        if (centreX <= width1 + (endPosX - posX) / 2 && centreX >= width1 - (endPosX - posX) / 2 &&
                                centreY <= height1 + (endPosY - posY) / 2 && centreY >= height1 - (endPosY - posY) / 2 && mc.gameSettings.thirdPersonView == 0) {
                            GL11.glPushMatrix();
                            GL11.glScalef(0.5f * amp, 0.5f * amp, 0.5f * amp);

                            double _width = Math.abs(scaledPositions[2] - scaledPositions[0]);
                            float v = (float) (mc.fontRenderer.FONT_HEIGHT * 2) - mc.fontRenderer.FONT_HEIGHT / 2;

                            mc.fontRenderer.drawStringWithShadow("Target", (float) (scaledPositions[0] + _width / 2 -
                                    mc.fontRenderer.getStringWidth("Target") / 2), (float) scaledPositions[3] + mc.fontRenderer.FONT_HEIGHT / 2, 0xffffffff);

                            if (Mouse.isButtonDown(2)) {
                                if (tpTimer.delay(150)) {
                                    mc.thePlayer.sendChatMessage(".tar " + entity.getName().toLowerCase());
                                    tpTimer.reset();
                                }
                            }

                            GL11.glPopMatrix();

                        }

                        //if(filter.contains("Held Item")){
                            GL11.glPushMatrix();
                            GL11.glScalef(0.5f * amp, 0.5f * amp, 0.5f * amp);
                            float v = (float) (mc.fontRenderer.FONT_HEIGHT * 2) - mc.fontRenderer.FONT_HEIGHT / 2;
                            double _width = Math.abs(scaledPositions[2] - scaledPositions[0]);

                            if(entity.getHeldItem() != null) {
                                Gui.drawRect(
                                        (float) (scaledPositions[0] + _width / 2 -
                                                mc.fontRenderer.getStringWidth(entity.getHeldItem().getDisplayName()) / 2) - 2,
                                        (float) scaledPositions[3] + v / 3f - 2,
                                        (float) (scaledPositions[0] + _width / 2 -
                                                mc.fontRenderer.getStringWidth(entity.getHeldItem().getDisplayName()) / 2) + mc.fontRenderer.getStringWidth(entity.getHeldItem().getDisplayName()) + 1,
                                        scaledPositions[3] + v / 3f + mc.fontRenderer.FONT_HEIGHT + 1,
                                        new Color(0, 0, 0, 255 - transparency.get()).getRGB());
                                mc.fontRenderer.drawStringWithShadow(entity.getHeldItem().getDisplayName(), (float) (scaledPositions[0] + _width / 2 -
                                        mc.fontRenderer.getStringWidth(entity.getHeldItem().getDisplayName()) / 2), (float) scaledPositions[3] + v / 3f, 0xffffffff);
                                double newY = scaledPositions[3] + v / 3f + v;
                                if(entity.getHeldItem().getTagCompound() != null){
                                    if(entity.getHeldItem().getTagCompound().toString().contains("Lore:")) {
                                        String[] split = entity.getHeldItem().getTagCompound().toString().split("\"");
                                        for (String s : split) {
                                            if(s.startsWith("¡ì9") && !s.contains("+") && !s.contains("Unbreakable")){
                                                s = s.replace("¡ì9","");
                                                Gui.drawRect(
                                                        (float) (scaledPositions[0] + _width / 2 -
                                                                mc.fontRenderer.getStringWidth(s) / 2) - 2,
                                                        (float) newY - 2,
                                                        (float) (scaledPositions[0] + _width / 2 -
                                                                mc.fontRenderer.getStringWidth(s) / 2) + mc.fontRenderer.getStringWidth(s) + 1,
                                                        newY + mc.fontRenderer.FONT_HEIGHT + 1,
                                                        new Color(0, 0, 0, 255 - transparency.get()).getRGB());
                                                mc.fontRenderer.drawStringWithShadow(EnumChatFormatting.AQUA + s, (float) (scaledPositions[0] + _width / 2 -
                                                        mc.fontRenderer.getStringWidth(s) / 2), (float) newY, 0xffffffff);
                                                newY+=v;
                                            }
                                        }
                                    }
                                }
                                if(entity.inventory.armorInventory[1] != null){
                                    if(entity.inventory.armorInventory[1].getTagCompound() != null){
                                        if(entity.inventory.armorInventory[1].getTagCompound().toString().contains("Lore:")){
                                            String[] split = entity.inventory.armorInventory[1].getTagCompound().toString().split("\"");
                                            for (String s : split) {
                                                if (s.length() != 1 && s.startsWith("¡ì9") && !s.contains("Pants") && !s.contains("Also, a fashion") && !s.contains("Used in the") && !s.contains("As strong")) {
                                                    s = s.replace("¡ì9","");
                                                    /* *
                                                    Gui.drawRect(
                                                            (float) (scaledPositions[0] + _width / 2 -
                                                                    mc.fontRenderer.getStringWidth(s) / 2) - 2,
                                                            (float) newY - 2,
                                                            (float) (scaledPositions[0] + _width / 2 -
                                                                    mc.fontRenderer.getStringWidth(s) / 2) + mc.fontRenderer.getStringWidth(s) + 1,
                                                            newY + mc.fontRenderer.FONT_HEIGHT + 1,
                                                            new Color(0, 0, 0, 255 - transparency.get()).getRGB());
                                                    * */
                                                    mc.fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD + s, (float) (scaledPositions[0] + _width / 2 -
                                                            mc.fontRenderer.getStringWidth(s) / 2), (float) newY, 0xffffffff);
                                                    newY+=v;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            GL11.glPopMatrix();
                        //}



                        //if (filter.contains("Name")) {
                            GL11.glPushMatrix();
                            GL11.glScalef(0.5f * amp, 0.5f * amp, 0.5f * amp);
                            double _width2 = Math.abs(scaledPositions[2] - scaledPositions[0]);

                            int color;

                            /* *
                            if (nameColorType.get().equals("Team")) {
                                color = new Color(255, 99, 99).getRGB();
                                //if (onlyTargets.get()) {
                                //    color = WHITE.getRGB();
                                //} else {
                                    if (novoline.playerManager.hasType(entity.getName().toLowerCase(), PlayerManager.EnumPlayerType.FRIEND)) {
                                        color = FRIEND.getRGB();
                                    } else if (novoline.playerManager.hasType(entity.getName().toLowerCase(), PlayerManager.EnumPlayerType.TARGET)) {
                                        color = TARGET.getRGB();
                                    } else if (Teams.isOnSameTeam(entity)) {
                                        color = TEAM.getRGB();
                                    }
                                //}
                            } else {
                                color = nameColor.getAwtColor().getRGB();

                                if (novoline.playerManager.hasType(entity.getName().toLowerCase(), PlayerManager.EnumPlayerType.FRIEND)) {
                                    color = friendColor.getAwtColor().getRGB();
                                } else if (novoline.playerManager.hasType(entity.getName().toLowerCase(), PlayerManager.EnumPlayerType.TARGET)) {
                                    color = targetColor.getAwtColor().getRGB();
                                }
                            }
                            * */

                        color = Colors.WHITE.c;

                            String name = entity.getName();
//                        UserEntity user = novoline.getIRCUser(name);
//
//                        if (!name.isEmpty() && user != null) {
//                            name += " \u00A77(\u00A7b" + user.getUsername() + "\u00A77)\u00A7r";
//                        }
//                        if (entity.getCustomNameTag().startsWith("Desynced - ")) {
//                            name = EnumChatFormatting.YELLOW + "Your position for " + name;
//                        }

                            float v2 = (float) (mc.fontRenderer.FONT_HEIGHT * 2) - mc.fontRenderer.FONT_HEIGHT / 2;

                            Gui.drawRect(
                                    (float) (scaledPositions[0] + _width / 2 -
                                            mc.fontRenderer.getStringWidth(name) / 2) - 2,
                                    (float) scaledPositions[1] - v2 - 2,
                                    (float) (scaledPositions[0] + _width / 2 -
                                            mc.fontRenderer.getStringWidth(name) / 2) + mc.fontRenderer.getStringWidth(name) + 1,
                                    scaledPositions[1] - v2 + mc.fontRenderer.FONT_HEIGHT + 1,
                                    new Color(0, 0, 0, 255 - transparency.get()).getRGB());
                            mc.fontRenderer.drawStringWithShadow(name, (float) (scaledPositions[0] + _width / 2 -
                                    mc.fontRenderer.getStringWidth(name) / 2), (float) scaledPositions[1] - v2, color);
                            GL11.glPopMatrix();
                        //}




                        //if (this.filter.contains("Health")) {
                            RenderUtils.start2D();
                            GL11.glPushMatrix();
                            double maxHealth = entity.getMaxHealth(), amplifier = 100 / maxHealth, space = length / 100;
                            float health = entity.getHealth();

                            if (health > maxHealth) {
                                health *= maxHealth / health;
                            }

                            int percent = (int) (health * amplifier);
                            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
                            RenderUtils.setColor(new Color(RenderUtils.getHealthColor(entity)).getRGB());
                            GL11.glLineWidth(2);
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex2d(posX - 2, endPosY - percent * space);
                            GL11.glVertex2d(posX - 2, endPosY);
                            GL11.glEnd();
                            RenderUtils.setColor(new Color(40, 40, 40).getRGB());
                            GL11.glLineWidth(1);
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex2d(posX - 2.3, endPosY - percent * space - 0.3);
                            GL11.glVertex2d(posX - 2.3, endPosY + 0.3);
                            GL11.glVertex2d(posX - 1.3, endPosY + 0.3);
                            GL11.glVertex2d(posX - 1.3, endPosY - percent * space - 0.3);
                            GL11.glVertex2d(posX - 2.3, endPosY - percent * space - 0.3);
                            GL11.glEnd();
                            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
                            GL11.glPopMatrix();
                            RenderUtils.stop2D();
                            GL11.glPushMatrix();

                            double[] healthPos = PropertyFactory.ScaleUtils.getScaledMouseCoordinates(mc, posX, endPosY - percent * space);
                            double[] newPos = {healthPos[0] * 1 / 0.48, healthPos[1] * 1 / 0.48};

                            String hp = "";
                            hp = (int) entity.getHealth() + " HP";

                            GL11.glScalef(0.48f * amp, 0.48f * amp, 0.48f * amp);
                            if (entity.getHealth() != entity.getMaxHealth())
                                mc.fontRenderer.drawString("" + hp,
                                        (float) newPos[0] - mc.fontRenderer.getStringWidth("a" + hp),
                                        (float) newPos[1],
                                        0xffffffff, true);
                            GL11.glPopMatrix();
                        //}

                        // BOX
                        boolean bordered = true
                                //this.boxMode.get().equalsIgnoreCase("Bordered")
                        ;
                        /* *
                        if (boxColorMode.get().equals("Rainbow")
                                || boxColorMode.get().equals("Astolfo")) {
                            RenderUtils.drawRainbowBox(posX, posY, endPosX, endPosY, 2,
                                    boxStyle.get().equals("Corner"),
                                    boxColorMode.get().equals("Astolfo"), bordered);
                        } else
                        * */
                        {
                            //if (boxStyle.get().equals("Full")) {
                                //RenderUtils.drawBorderedBox(posX, posY, endPosX, endPosY, BLACK, bordered);
                            //} else {
                                if (bordered) {
                                    RenderUtils.drawCornerBox(posX, posY, endPosX, endPosY, 3, BLACK);
                                    RenderUtils.drawCornerBox(posX, posY, endPosX, endPosY, 3, BLACK);
                                    //int color2 = new Color(10, 10, 10, 60).getRGB();;
                                    //Gui.drawRect3(posX + 1, posY + 1, endPosX - 1, endPosY - 1, color2);
                                }

                                //RenderUtils.drawCornerBox(posX, posY, endPosX, endPosY, 1,
                                        //this.hurtTimeCheck.get() &&
                                        //entity.hurtResistantTime >= 10 ?
                                                //Color.RED
                                        //BLACK
                                                //: this.color.getAwtColor()
                                //);
                            //}
                        }
                    }
                }
            }
            GL11.glPopMatrix();
            mc.entityRenderer.setupOverlayRendering();
        }
    }

    private void collectEntities() {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (isValid(entity)) this.collectedEntities.add(entity);
        }
    }

    private boolean isValid(EntityPlayer entityLivingBase) {
        return !entityLivingBase.isDead && !entityLivingBase.isInvisible();
    }

    private Vector3d project2D(ScaledResolution scaledResolution, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelView);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);

        if (GLU.gluProject((float) x, (float) y, (float) z, this.modelView, this.projection, this.viewport,
                this.vector)) {
            return new Vector3d(this.vector.get(0) / scaledResolution.getScaleFactor(),
                    (Display.getHeight() - this.vector.get(1)) / scaledResolution.getScaleFactor(), this.vector.get(2));
        }

        return null;
    }

    enum espMode {
        Box,
    }
}