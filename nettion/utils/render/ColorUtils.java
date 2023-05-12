package nettion.utils.render;

import net.minecraft.util.MathHelper;
import nettion.event.events.misc.EventProtocol;
import nettion.features.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import nettion.utils.math.MathUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtils {
    private static final EventProtocol<Event> eventProtocol = new EventProtocol();

    public static EventProtocol<Event> getEventProtocol() {
        return eventProtocol;
    }

    private static final double startTime = System.currentTimeMillis();

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    public static Color interpolate(Color from, Color to, double value) {
        double progress = value > 1.0 ? 1.0 : (value < 0.0 ? 0.0 : value);
        int redDiff = to.getRed() - from.getRed();
        int greenDiff = to.getGreen() - from.getGreen();
        int blueDiff = to.getBlue() - from.getBlue();
        int alphaDiff = to.getAlpha() - from.getAlpha();
        int newRed = (int)((double)from.getRed() + (double)redDiff * progress);
        int newGreen = (int)((double)from.getGreen() + (double)greenDiff * progress);
        int newBlue = (int)((double)from.getBlue() + (double)blueDiff * progress);
        int newAlpha = (int)((double)from.getAlpha() + (double)alphaDiff * progress);
        return new Color(newRed, newGreen, newBlue, newAlpha);
    }

    public static Color[] getClientAccentTheme() {
        return new Color[]{new Color(91, 206, 250), new Color(245, 169, 184)};
    }

    public static int fadeBetween(int startColor, int endColor, float progress) {
        if (progress > 1.0F) {
            progress = 1.0F - progress % 1.0F;
        }

        return fadeTo(startColor, endColor, progress);
    }

    public static Color fadeBetween(int speed, int index, Color start, Color end) {
        int tick = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        tick = (tick >= 180 ? 360 - tick : tick) * 2;
        return interpolate(start, end, (double)((float)tick / 360.0F));
    }

    public static int fadeTo(int startColor, int endColor, float progress) {
        float invert = 1.0F - progress;
        int r = (int)((float)(startColor >> 16 & 255) * invert + (float)(endColor >> 16 & 255) * progress);
        int g = (int)((float)(startColor >> 8 & 255) * invert + (float)(endColor >> 8 & 255) * progress);
        int b = (int)((float)(startColor & 255) * invert + (float)(endColor & 255) * progress);
        int a = (int)((float)(startColor >> 24 & 255) * invert + (float)(endColor >> 24 & 255) * progress);
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
    }

    //Opacity value ranges from 0-1
    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static void glColor(int color) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        int a = color >> 24 & 255;
        GL11.glColor4f((float)r / 255.0F, (float)g / 255.0F, (float)b / 255.0F, (float)a / 255.0F);
    }

    public static Color hslRainbow(Integer index,Float lowest,Float bigest, Integer indexOffset, Integer timeSplit) {
        return Color.getHSBColor((float) ((MathHelper.abs((((((System.currentTimeMillis()-startTime) + index * indexOffset)/timeSplit)%2)-1))*(bigest-lowest))+lowest),0.7f,1f);
    }

    public static Color tripleColor(int rgbValue) {
        return tripleColor(rgbValue, 1);
    }

    public static Color tripleColor(int rgbValue, float alpha) {
        alpha = Math.min(1, Math.max(0, alpha));
        return new Color(rgbValue, rgbValue, rgbValue, (int) (255 * alpha));
    }

    public static int rainbow(int delay) {
        double rainbow = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 10.0);
        return Color.getHSBColor((float)(rainbow % 360.0 / 360.0), 0.5f, 1.0f).getRGB();
    }

    public static Color rainbow(long time, float count, float fade) {
        float hue = ((float)time + (1.0f + count) * 2.0E8f) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int)color);
        return new Color((float)c.getRed() / 255.0f * fade, (float)c.getGreen() / 255.0f * fade, (float)c.getBlue() / 255.0f * fade, (float)c.getAlpha() / 255.0f);
    }

    public static Color getRainbow() {
        return new Color(Color.HSBtoRGB((float)((double) Module.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)1 / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
    }

    public static void color(int color) {
        float alpha = (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        GlStateManager.color(red, green, blue, alpha);
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public static int getAstolfo(int delay, float offset, float hueSetting) {
        float hue;
        float speed = 4000;
        for (hue = (float)(System.currentTimeMillis() % (long)delay) + offset; hue > speed; hue -= speed) {
        }
        if ((double)(hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue + hueSetting, 0.5f, 1.0f);
    }

    public static final Color getClientColor() {
        return new Color(250, 248, 190);
    }
    public static final Color getAlternateClientColor() {
        return new Color(46,234,255);
    }

    public static final Color getTenacityColor() {
        return new Color(236, 133, 209);
    }
    public static final Color getAlternateTenacityColor() {
        return new Color(28, 167, 222);
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static Color rainbow(final int speed, final int index, final float saturation, final float brightness, final float opacity) {
        final int angle = (int)((System.currentTimeMillis() / speed + index) % 360L);
        final float hue = angle / 360.0f;
        final Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(opacity * 255.0f))));
    }

    public static Color rainbow(final int speed, final int index, final float saturation, final float brightness, final float opacity, final int alp) {
        final int angle = (int)((System.currentTimeMillis() / speed + index) % 360L);
        final float hue = angle / 360.0f;
        final Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alp);
    }

    public static int darker(int hexColor, int factor) {
        float alpha = (float) (hexColor >> 24 & 255);
        float red = Math.max((float) (hexColor >> 16 & 255) - (float) (hexColor >> 16 & 255) / (100.0F / (float) factor), 0.0F);
        float green = Math.max((float) (hexColor >> 8 & 255) - (float) (hexColor >> 8 & 255) / (100.0F / (float) factor), 0.0F);
        float blue = Math.max((float) (hexColor & 255) - (float) (hexColor & 255) / (100.0F / (float) factor), 0.0F);
        return (int) ((float) (((int) alpha << 24) + ((int) red << 16) + ((int) green << 8)) + blue);
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtils.interpolateColorHue(start, end, angle / 360f) : ColorUtils.interpolateColorC(start, end, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathUtil.interpolateInt(color1.getRed(), color2.getRed(), amount),
                MathUtil.interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                MathUtil.interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static int applyOpacityInt(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacityInt(old, opacity).getRGB();
    }

    public static Color applyOpacityColor(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacityInt(old, opacity);
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        Color resultColor = Color.getHSBColor(MathUtil.interpolateFloat(color1HSB[0], color2HSB[0], amount),
                MathUtil.interpolateFloat(color1HSB[1], color2HSB[1], amount), MathUtil.interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(),
                MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    //Opacity value ranges from 0-1
    public static Color applyOpacityInt(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }
}
