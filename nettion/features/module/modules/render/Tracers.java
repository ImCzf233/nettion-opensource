/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.other.FriendManager;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.render.Colors;

import nettion.utils.render.RenderUtils;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Tracers
extends Module {
    public Tracers() {
        super("Tracers", ModuleType.Render);
    }

    @EventHandler
    private void on3DRender(EventRender3D e) {
        for (Entity o : mc.theWorld.loadedEntityList) {
            double[] arrd;
            if (!o.isEntityAlive() || !(o instanceof EntityPlayer) || o == mc.thePlayer) continue;
            double posX = o.lastTickPosX + (o.posX - o.lastTickPosX) * (double)e.getPartialTicks() - RenderManager.renderPosX;
            double posY = o.lastTickPosY + (o.posY - o.lastTickPosY) * (double)e.getPartialTicks() - RenderManager.renderPosY;
            double posZ = o.lastTickPosZ + (o.posZ - o.lastTickPosZ) * (double)e.getPartialTicks() - RenderManager.renderPosZ;
            boolean old = mc.gameSettings.viewBobbing;
            RenderUtils.startDrawing();
            mc.gameSettings.viewBobbing = false;
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
            mc.gameSettings.viewBobbing = old;
            float color = Colors.WHITE.c;
            if (FriendManager.isFriend(o.getName())) {
                double[] arrd2 = new double[3];
                arrd2[0] = 0.0;
                arrd2[1] = 1.0;
                arrd = arrd2;
                arrd2[2] = 1.0;
            } else {
                double[] arrd3 = new double[3];
                arrd3[0] = color;
                arrd3[1] = 1.0f - color;
                arrd = arrd3;
                arrd3[2] = 0.0;
            }
            this.drawLine(arrd, posX, posY, posZ);
            RenderUtils.stopDrawing();
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
}

