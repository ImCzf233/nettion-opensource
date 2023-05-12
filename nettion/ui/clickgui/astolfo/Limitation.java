package nettion.ui.clickgui.astolfo;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class Limitation {
    public final int startX;
    public final int startY;
    public final int endX;
    public final int endY;

    public Limitation(int x1, int y1, int x2, int y2) {
        startX = x1;
        startY = y1;
        endX = x2;
        endY = y2;
    }

    public void cut() {
        doGlScissor(startX, startY, endX - startX, endY - startY);
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }
}

