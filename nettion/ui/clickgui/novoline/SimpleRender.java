package nettion.ui.clickgui.novoline;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import nettion.utils.math.MathUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public enum SimpleRender {
    INSTANCE;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static float processFPS(float defF, float defV) {
        return defV / (Minecraft.getMinecraft().getDebugFPS() == 0 ? 1 : Minecraft.getMinecraft().getDebugFPS() / defF);
    }

    public static String abcdefg() {
        String[] abc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String[] ABC = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String[] aBc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        try {
            int var0 = (int) MathUtil.randomDouble(0, aBc.length - 1);
            return aBc[var0] + var0 + abc[abc.length - 1] + ABC[ABC.length - 1] + aBc[abc.length] + abc[ABC.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double getEntityRenderX(Entity entity) {
        return entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (Minecraft.getMinecraft()).timer.renderPartialTicks - RenderManager.renderPosX;
    }

    public static double getEntityRenderY(Entity entity) {
        return entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (Minecraft.getMinecraft()).timer.renderPartialTicks - RenderManager.renderPosY;
    }

    public static double getEntityRenderZ(Entity entity) {
        return entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (Minecraft.getMinecraft()).timer.renderPartialTicks - RenderManager.renderPosZ;
    }

    public static void drawCircle(float x, float y, float r, float lineWidth, boolean isFull, int color) {
        drawCircle(x, y, r, 10, lineWidth, 360, isFull, color);
    }

    public static void drawCircle(float cx, float cy, double r, int segments, float lineWidth, int part, boolean isFull, int c) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        r *= 2.0D;
        cx *= 2.0F;
        cy *= 2.0F;
        float f2 = (c >> 24 & 0xFF) / 255.0F;
        float f3 = (c >> 16 & 0xFF) / 255.0F;
        float f4 = (c >> 8 & 0xFF) / 255.0F;
        float f5 = (c & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glBegin(3);
        for (int i = segments - part; i <= segments; i++) {
            double x = Math.sin(i * Math.PI / 180.0D) * r;
            double y = Math.cos(i * Math.PI / 180.0D) * r;
            GL11.glVertex2d(cx + x, cy + y);
            if (isFull)
                GL11.glVertex2d(cx, cy);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public static Color getBlendColor(final double current, final double max) {
        final long base = Math.round(max / 5.0);
        if (current >= base * 5L) {
            return new Color(15, 255, 15);
        }
        if (current >= base << 2) {
            return new Color(166, 255, 0);
        }
        if (current >= base * 3L) {
            return new Color(255, 191, 0);
        }
        if (current >= base << 1) {
            return new Color(255, 89, 0);
        }
        return new Color(255, 0, 0);
    }

    public static void drawRect(double left, double top, double right, double bottom, final int color) {
        if (left < right) {
            final double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final double j = top;
            top = bottom;
            bottom = j;
        }
        final float f3 = (color >> 24 & 0xFF) / 255.0f;
        final float f4 = (color >> 16 & 0xFF) / 255.0f;
        final float f5 = (color >> 8 & 0xFF) / 255.0f;
        final float f6 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f4, f5, f6, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(final double left, final double top, final double right, final double bottom, final double borderWidth, final int insideColor, final int borderColor, final boolean borderIncludedInBounds) {
        drawRect(left - (borderIncludedInBounds ? 0.0 : borderWidth), top - (borderIncludedInBounds ? 0.0 : borderWidth), right + (borderIncludedInBounds ? 0.0 : borderWidth), bottom + (borderIncludedInBounds ? 0.0 : borderWidth), borderColor);
        drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0), top + (borderIncludedInBounds ? borderWidth : 0.0), right - (borderIncludedInBounds ? borderWidth : 0.0), bottom - (borderIncludedInBounds ? borderWidth : 0.0), insideColor);
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * c.getRed();
        float g = 0.003921569f * c.getGreen();
        float b = 0.003921569f * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    private static final int colorDelay = 11;
    private static final int colorLength = 110;
}
