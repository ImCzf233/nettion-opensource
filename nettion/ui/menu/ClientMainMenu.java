package nettion.ui.menu;

import java.awt.*;
import java.io.IOException;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import nettion.other.GLSLSandboxShader;
import nettion.ui.alt.GuiAltManager;
import nettion.ui.fonts.FontLoaders;
import nettion.utils.render.RoundedUtils;
import nettion.utils.render.Colors;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ClientMainMenu extends GuiScreen implements GuiYesNoCallback {
    private long initTime = System.currentTimeMillis();
    private GLSLSandboxShader shader;

    public ClientMainMenu() {
        initTime = System.currentTimeMillis();
        try {
            shader = new GLSLSandboxShader("wave.frag");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
    }

    public void updateScreen() {

    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        super.initGui();
        initTime = System.currentTimeMillis();
        try {
            shader = new GLSLSandboxShader("wave.frag");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontLoaders.F16.drawString("1", -114, -114, -1);
        GlStateManager.disableCull();
        drawDefaultBackground();
        try {
            shader.useShader(
                    width * 2,
                    height * 2,
                    mouseX,
                    mouseY,
                    (System.currentTimeMillis() - initTime) / 1000f
            );
            GL11.glBegin(GL11.GL_QUADS);

            GL11.glVertex2f(-1f, -1f);
            GL11.glVertex2f(-1f, 1f);
            GL11.glVertex2f(1f, 1f);
            GL11.glVertex2f(1f, -1f);

            GL11.glEnd();

            GL20.glUseProgram(0);

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            ScaledResolution sr = new ScaledResolution(mc);
            width = sr.getScaledWidth();
            height = sr.getScaledHeight();
            RoundedUtils.drawRound((int) (sr.getScaledWidth_double() / 2 - 75), (int) (sr.getScaledHeight_double() / 2 - 75), 150, 148, 5, new Color(0, 0, 0, 40));
            String[] S = new String[]{"SinglePlayer", "MultiPlayer", "AltManager", "Settings", "Exit"};
            for (int i = 0; i < 5; i++) {
                FontLoaders.F20.drawCenteredStringWithShadow(S[i], (float) sr.getScaledWidth_double() / 2,1 + (float) sr.getScaledHeight_double() / 2 - 66 + i * 30, Colors.WHITE.c);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean isHovered(double x, double y, float x2, float y2, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
            return true;
        }
        return false;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);
        for (int i = 0; i < 5; i++) {
            if (isHovered((int) (sr.getScaledWidth_double() / 2 - 75), (int) (sr.getScaledHeight_double() / 2 - 75 + i * 25 + i * 5), (int) (sr.getScaledWidth_double() / 2) + 75, (int) (sr.getScaledHeight_double() / 2 - 50 + i * 25 + i * 5) + 2, mouseX, mouseY)) {
                switch (i) {
                    case 0:
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case 1:
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case 2:
                        mc.displayGuiScreen(new GuiAltManager(this));
                        break;
                    case 3:
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case 4:
                        mc.shutdown();
                        break;
                    default:
                        break;

                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onGuiClosed() {

    }
}