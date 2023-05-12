package nettion.features.module.modules.render;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import org.lwjgl.opengl.GL11;

public class FPSHurtCam extends Module {
    public FPSHurtCam() {
        super("FPSHurtCam", ModuleType.Render);
    }

    @EventHandler
    private void onRender2D(EventRender2D e) {
        drawVignette();
    }

    private void drawVignette() {
        if (mc.gameSettings.thirdPersonView != 0)
            return;

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);

        int healthPersent = (int) ((mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth()) * 100);

        if (healthPersent <= 10) {
            GL11.glColor4f(0, 1.0F, 1.0F, 1.0F);
        } else if (healthPersent <= 20) {
            GL11.glColor4f(0, 0.8F, 0.8F, 1.0F);
        } else if (healthPersent <= 50) {
            GL11.glColor4f(0, 0.5F, 0.5F, 1.0F);
        } else {
            GL11.glColor4f(0, 0.0F, 0.0F, 0.0F);
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        mc.getTextureManager().bindTexture(GuiIngame.vignetteTexPath);
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        var10.begin(7, DefaultVertexFormats.POSITION_TEX);
        var10.pos(0.0D, (double) scaledResolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        var10.pos((double) scaledResolution.getScaledWidth(), (double) scaledResolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        var10.pos((double) scaledResolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        var10.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        var9.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }
}
