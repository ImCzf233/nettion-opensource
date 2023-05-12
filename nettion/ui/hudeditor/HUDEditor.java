package nettion.ui.hudeditor;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import nettion.other.FileManager;
import nettion.ui.fonts.FontLoaders;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HUDEditor extends GuiScreen {
    public static ArrayList<Window> windows = Lists.newArrayList();
    public static boolean binding = false;
    public double opacity = 0.0;
    public int scrollVelocity;
    public static boolean HudEditor = false;
    public static Window inv = new Window("Inventory", 5, 77, (20 * 9) + 2);
    public static Window targethud = new Window("TargetHud", 528, 279, 80);
    public static Window session = new Window("SessionInfo", 8, 24, 115);
    public static Window armor = new Window("ArmorStatus", 869, 444, FontLoaders.F14.getStringWidth("100%") + 24);
    public static Window serh = new Window("ServerHUD", 5, 84, 32);
    public static Window speedometer = new Window("Speedometer", 5, 77, 94);

    public HUDEditor() {
        if (windows.isEmpty()) {
            windows.add(inv);
            windows.add(targethud);
            windows.add(session);
            windows.add(armor);
            windows.add(serh);
            windows.add(speedometer);
        }
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.opacity = this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0;
        GlStateManager.pushMatrix();
        windows.forEach(w -> w.render(mouseX, mouseY));
        GlStateManager.popMatrix();
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            this.scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 120 : 0);
        }
        windows.forEach(w -> w.mouseScroll(mouseX, mouseY, this.scrollVelocity));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(w -> w.click(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        HudEditor = true;
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
    }

    public static void init() {
        List<String> winpos = FileManager.read("HUDEditor.txt");
        for (String v : winpos) {
            String name = v.split(":")[0];
            Window w = null;
            for (Window win : windows) {
                if (win.title.equals(name))
                    w = win;
            }
            if (w == null) continue;
            w.x = Integer.parseInt(v.split(":")[1]);
            w.y = Integer.parseInt(v.split(":")[2]);
        }
    }

    @Override
    public void onGuiClosed() {
        StringBuilder windowss = new StringBuilder();
        for (Window w : windows) {
            windowss.append(String.format("%s:%s:%s%s", w.title, w.x, w.y, System.lineSeparator()));
        }
        FileManager.save("HUDEditor.txt", windowss.toString(), false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 && !binding) {
            HudEditor = false;
            this.mc.displayGuiScreen(null);
            return;
        }
        windows.forEach(w -> w.key(typedChar, keyCode));
    }
}
