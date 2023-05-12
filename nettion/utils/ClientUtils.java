package nettion.utils;

import nettion.utils.render.RenderUtils;
import nettion.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class ClientUtils {

    public static void drawBackground() {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final Color gradientColor1 = ColorUtils.interpolateColorsBackAndForth(15, 1, new Color(140, 40, 255), new Color(46,234,255), false);
        final Color gradientColor2 = ColorUtils.interpolateColorsBackAndForth(15, 1, new Color(46,234,255), new Color(46,234,255), false);
        final Color gradientColor3 = ColorUtils.interpolateColorsBackAndForth(50, 1, new Color(140, 40, 255), new Color(140, 40, 255), false);
        final Color gradientColor4 = ColorUtils.interpolateColorsBackAndForth(50, 1, new Color(46,234,255), new Color(140, 40, 255), false);
        RenderUtils.drawGradient(0.0f, 0.0f, (float)sr.getScaledWidth(), (float)sr.getScaledHeight(), 1.0f, gradientColor2, gradientColor4, gradientColor3, gradientColor1);
    }
}
