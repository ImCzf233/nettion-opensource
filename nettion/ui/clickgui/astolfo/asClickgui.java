package nettion.ui.clickgui.astolfo;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.render.ClickGui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class asClickgui extends GuiScreen {
    public static final ArrayList<Window> windows = Lists.newArrayList();
    public int scrollVelocity;
    public static boolean binding;

    public asClickgui() {
        if (windows.isEmpty()) {
            int x = 5;
            ModuleType[] arrmoduleType = ModuleType.values();
            int n = arrmoduleType.length;
            int n2 = 0;
            while (n2 < n) {
                ModuleType c = arrmoduleType[n2];
                windows.add(new Window(c, x, 5));
                x += 105 + 10;
                ++n2;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.Streamer.getValue()) {
            drawGradientRect(0, 0, width, height, new Color(0, 0, 0, 0).getRGB(), ColorCreator.createRainbowFromOffset(-6000, 5));
        }
        Gui.drawRect(0, 0, Display.getWidth(), Display.getHeight(), new Color(0, 0, 0, 150).getRGB());//Shadow
        GlStateManager.pushMatrix();
        windows.forEach(w -> w.render(mouseX, mouseY));
        GlStateManager.popMatrix();
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 120 : 0);
        }
        windows.forEach(w -> w.mouseScroll(mouseX, mouseY, scrollVelocity));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(w -> w.click(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 && !binding) {
            mc.displayGuiScreen(null);
            return;
        }
        windows.forEach(w -> w.key(typedChar, keyCode));
    }

    @Override
    public void initGui() {
    }

    @Override
    public void onGuiClosed() {
        mc.entityRenderer.stopUseShader();
        mc.entityRenderer.isShaderActive();
    }
}

