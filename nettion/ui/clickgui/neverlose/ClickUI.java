package nettion.ui.clickgui.neverlose;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import nettion.Nettion;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.render.ClickGui;
import nettion.features.value.Value;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.ui.fonts.old.Fonts;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;

public class ClickUI extends GuiScreen {

    private static ModuleType currentCategory = ModuleType.Combat;
    private static float startX = ClickGui.startX;
    private static float startY = ClickGui.startY;
    private float moveX, moveY;
    private boolean previousMouse = true;
    private float currentCateRectY = 22, endCateY;
    private final TranslateUtil translate = new TranslateUtil(0, 0);
    private static int wheel = ClickGui.tempWheel;

    @Override
    public void initGui() {
        super.initGui();
        currentCategory = ClickGui.currentModuleType;
        translate.setX(0);
        translate.setY(0);
    }

    public float processFPS(float fps, float defF, float defV) {
        return defV / (fps / defF);
    }

    boolean useLeft;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        int FPS = mc.getDebugFPS() == 0 ? 1 : mc.getDebugFPS();

        if (RenderUtil.isHovered(startX, startY, startX + 520, startY + 50, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (moveX == 0 && moveY == 0) {
                moveX = mouseX - startX;
                moveY = mouseY - startY;
            } else {
                startX = mouseX - moveX;
                startY = mouseY - moveY;
            }
            previousMouse = true;
        } else if (moveX != 0 || moveY != 0) {
            moveX = 0;
            moveY = 0;
        }
        double sizes = 1;

        GL11.glScaled(sizes, sizes, sizes);
        /**
         * Left background
         */
        RenderUtil.drawRect(startX + 10, startY, startX + 520, startY + 460 - 180 + 100,
                new Color(0, 0, 0, 200).getRGB());//220
        RenderUtil.drawRect(startX + 10, startY, startX + 520, startY + 460 - 180 + 100,
                new Color(1, 20, 40, 180).getRGB());//220

        RenderUtil.drawRect(startX + 120, startY + 40, startX + 520, startY + 460 - 180 + 100,
                new Color(8, 8, 13).getRGB());
        /**
         * ��� - dont change
         */
        RenderUtil.drawRect(startX + 120, startY, startX + 520, startY + 38,
                new Color(8, 8, 13).getRGB());
        /**
         *  ���� - dont change
         */
        RenderUtil.drawRect(startX + 120, startY + 38, startX + 520, startY + 40,
                new Color(5, 26, 38).getRGB());
        RenderUtil.drawRect(startX + 118, startY, startX + 120, startY + 460 - 180 + 100,
                new Color(5, 26, 38).getRGB());
        /**
         * Client Name
         */
        Fonts.R30.drawString(Nettion.instance.name.toUpperCase(),
                startX + 65 - Fonts.R30.getStringWidth(Nettion.instance.name.toUpperCase()) / 2 - 0.5f, startY + 17 - 0.5f,
                Color.CYAN.getRGB());
        Fonts.R30.drawString(Nettion.instance.name.toUpperCase(),
                startX + 65 - Fonts.R30.getStringWidth(Nettion.instance.name.toUpperCase()) / 2, startY + 17,
                new Color(255, 255, 255).getRGB());

        RenderUtil.startGlScissor((int) startX, (int) startY + 40 + 15, 600, 460 - 180 - 41 - 15 + 100);
        int cateY = 0;

        String oldPref = "";
        currentCateRectY = AnimationUtil.moveUD(currentCateRectY, endCateY, processFPS(FPS, 1000, 0.01F), processFPS(FPS, 1000, 0.008F));

        RenderUtil.drawFastRoundedRect((int) (startX + 8 + 10), startY + 18 + currentCateRectY - 2, (int) (startX + 110), startY + 36 + currentCateRectY - 3, 4F, new Color(8, 50, 74).getRGB());

        float rightY;
        float leftY;
        for (int i = 0; i < ModuleType.values().length; i++) {
            ModuleType category = ModuleType.values()[i];
            cateY += 20;

            if (!category.name().split("_")[0].equals(oldPref)) {
                cateY += 5.5f;
                oldPref = category.name().split("_")[0];
            }
            if (category == currentCategory) {
                endCateY = cateY;
            }
            RenderUtil.drawRect(0, 0, 0, 0, 0);
            Fonts.R18.drawString(category.name().split("_")[0],
                    startX + 9 + 60 - Fonts.R30.getStringWidth(Nettion.instance.name) / 2f,
                    startY + 23 + cateY,
                    new Color(255, 255, 255).getRGB());
            float strX = startX + 70 - Fonts.R30.getStringWidth(Nettion.instance.name) / 2f,
                    y = startY + 21 + cateY;
            final int Blue = new Color(3, 168, 245).getRGB();
            if (category.name().equals("Combat")) {
                Fonts.icon24.drawString("1", strX - 15, y + 4, Blue);
            }
            if (category.name().equals("Movement")) {
                Fonts.icon26.drawString("5", strX - 15, y + 4, Blue);
            }
            if (category.name().equals("Render")) {
                Fonts.guiicons22.drawString("F", strX - 15, y + 3, Blue);
            }
            if (category.name().equals("Player")) {
                Fonts.guiicons28.drawString("C", strX - 15, y + 4, Blue);
            }
            if (category.name().equals("World")) {
                Fonts.guiicons30.drawString("E", strX - 15, y + 4, Blue);
            }
            if (category.name().equals("Ghost")) {
                Fonts.guiicons28.drawString("G", strX - 15, y + 4, Blue);
            }
            if (category.name().equals("Globals")) {
                Fonts.guiicons28.drawString("J", strX - 15, y + 4, -1);
            }
            if (category.name().equals("Script")) {
                Fonts.novoicons25.drawString("G", strX - 15, y, -1);
            }
            if (RenderUtil.isHovered(startX + 8, startY + 18 + cateY, startX + 110, startY + 36 + cateY, mouseX, mouseY)
                    && Mouse.isButtonDown(0) && !previousMouse) {
                currentCategory = category;
                wheel = 0;
                previousMouse = true;
            }
        }

        leftY = rightY = translate.getY();

        int size = 0;
        int valueY;
        for (Module m : Nettion.instance.getModuleManager().getModules()) {
            if (m.getType() != currentCategory)
                continue;
            for (Value v : m.getValues()) {
                if (ClickGui.Visitable.getValue()) {
                    if (!v.isVisitable()) continue;
                }
                size += 1;
            }
            float preY = (size + 1) * 20;
            size = 0;
            /**
             * auto compass and dont change it
             */
            useLeft = !(leftY + preY > rightY + preY);

            if (leftY + preY > rightY + preY) {
                int listY = 0;
                for (Value v : m.getValues()) {
                    if (ClickGui.Visitable.getValue()) {
                        if (!v.isVisitable()) continue;
                    }
                    size += v.isDownopen() ? v.listModes().size() + 1 : 1;
                }
                /**
                 * break not use render, that can fix FPS
                 */
                if (rightY > startY + 185 + 100) {
                    break;
                }
                RenderUtil.drawFastRoundedRect((int) (startX + 325), startY + 50 + rightY, (int) (startX + 510), startY + 62 + rightY + (size + 1) * 20 + listY, 3, new Color(3, 13, 26).getRGB());
                RenderUtil.drawRect(startX + 328, startY + 50 + rightY + 14, startX + 507, startY + 52 + rightY + 14, new Color(5, 26, 38).getRGB());
                /**
                 *  mod name of right
                 */
                Fonts.PF19.drawString(m.getName(), startX + 329, startY + 51 + rightY + 2, -1);

                RenderUtil.drawRect(startX + 484, startY + 58 + rightY + 13 - 18, startX + 501, startY + 64 + rightY + 15 - 18, new Color(3, 23, 46).getRGB());
                RenderUtil.drawCircle(startX + 485, startY + 61 + rightY + 14 - 18, 4, new Color(3, 23, 46));
                RenderUtil.drawCircle(startX + 500, startY + 61 + rightY + 14 - 18, 4, new Color(3, 23, 46));
                RenderUtil.drawRect(startX + 485, startY + 58 + rightY + 14 - 18, startX + 500, startY + 64 + rightY + 14 - 18, m.isEnabled() ? new Color(0, 102, 148).getRGB() : new Color(3, 6, 14).getRGB());
                RenderUtil.drawCircle(startX + 485, startY + 61 + rightY + 14 - 18, 3, m.isEnabled() ? new Color(0, 102, 148) : new Color(3, 6, 14));
                RenderUtil.drawCircle(startX + 500, startY + 61 + rightY + 14 - 18, 3, m.isEnabled() ? new Color(0, 102, 148) : new Color(3, 6, 14));

                RenderUtil.drawCircle(startX + 487 + (m.isEnabled() ? 11 : 0), startY + 61 + rightY + 14 - 18, 5, m.isEnabled() ? new Color(3, 168, 245) : new Color(120, 139, 151));

                if (RenderUtil.isHovered(startX + 485, startY + 58 + rightY + 14 - 18, startX + 500, startY + 64 + rightY + 14 - 18, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                    m.setEnabled(!m.isEnabled());
                    previousMouse = true;
                }

                valueY = 5;
                for (Value value : m.getValues()) {
                    if (ClickGui.Visitable.getValue()) {
                        if (!value.isVisitable()) continue;
                    }
                    if (value instanceof Option) {
                        Fonts.PF16.drawString(value.getName(), startX + 328, startY + 55 + rightY + 14 + valueY, new Color(156, 178, 191).getRGB());

                        RenderUtil.drawRect(startX + 484, startY + 58 + rightY + 11 + valueY, startX + 501, startY + 64 + rightY + 13 + valueY, new Color(3, 23, 46).getRGB());
                        RenderUtil.drawCircle(startX + 485, startY + 61 + rightY + 12 + valueY, 4, new Color(3, 23, 46));
                        RenderUtil.drawCircle(startX + 500, startY + 61 + rightY + 12 + valueY, 4, new Color(3, 23, 46));
                        RenderUtil.drawRect(startX + 485, startY + 57 + rightY + 12 + valueY, startX + 500, startY + 65 + rightY + 12 + valueY, (boolean) value.getValue() ? new Color(0, 102, 148).getRGB() : new Color(3, 6, 14).getRGB());
                        RenderUtil.drawCircle(startX + 485, startY + 61 + rightY + 12 + valueY, 3, (boolean) value.getValue() ? new Color(0, 102, 148) : new Color(3, 6, 14));
                        RenderUtil.drawCircle(startX + 500, startY + 61 + rightY + 12 + valueY, 3, (boolean) value.getValue() ? new Color(0, 102, 148) : new Color(3, 6, 14));

                        RenderUtil.drawCircle(startX + 487 + ((boolean) value.getValue() ? 11 : 0), startY + 61 + rightY + 12 + valueY, 5, (boolean) value.getValue() ? new Color(3, 168, 245) : new Color(120, 139, 151));

                        if (RenderUtil.isHovered(startX + 485, startY + 58 + rightY + 10 + valueY, startX + 500, startY + 64 + rightY + 14 + valueY, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                            value.setValue(!(boolean) value.getValue());
                            mc.thePlayer.playSound("random.click", 1, 1);
                            previousMouse = true;
                        }
                        valueY += 20;
                    }

                    if (value instanceof Numbers) {
                        Fonts.PF18.drawString(value.getName(), startX + 328, startY + 55 + rightY + 14 + valueY, new Color(156, 178, 191).getRGB());

                        RenderUtil.drawRect(startX + 415, startY + 54 + rightY + 19 + valueY, startX + 480, startY + 54 + rightY + 21 + valueY, new Color(3, 23, 46).getRGB());

                        Numbers<Number> s = (Numbers<Number>) value;
                        double state = (double) value.getValue();
                        double min = s.getMin().doubleValue();
                        double max = s.getMax().doubleValue();
                        double render = (68.0F * ((state - min) / (max - min)));
                        RenderUtil.drawRect(startX + 415, startY + 54 + rightY + 19 + valueY, startX + 415 + render, startY + 54 + rightY + 21 + valueY, new Color(0, 102, 148).getRGB());

                        RenderUtil.drawCircle(startX + 416 + render, startY + 54 + rightY + 20 + valueY, 3, new Color(61, 133, 224));

                        Fonts.PF14.drawCenteredString(String.valueOf(value.getValue()), startX + 498, startY + 55 + rightY + 14 + valueY, -1);

                        if (RenderUtil.isHovered(startX + 415, startY + 54 + rightY + 19 + valueY, startX + 483, startY + 54 + rightY + 21 + valueY, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                            render = (double) s.getMin();
                            max = (double) s.getMax();
                            double inc = s.getIncrement().doubleValue();
                            //	double val = (mouseX - (startX + 415)) * ((Double)value.getMaxValue() - (Double)value.getMinValue()) / 65.0F + (Double)value.getMinValue();
                            double valAbs = mouseX - (startX + 415);
                            double perc = valAbs / 68.0D;
                            perc = Math.min(Math.max(0.0D, perc), 1.0D);
                            double valRel = (max - render) * perc;
                            double val = render + valRel;
                            val = Math.round(val * (1.0D / inc)) / (1.0D / inc);
                            double num = val;
                            value.setValue(num);
                        }
                        valueY += 20;
                    }
                    //  if (value instanceof Mode) {
                    //   Mode mode = (Mode) value;
                    if (!value.listModes().isEmpty()) {
                        Fonts.PF18.drawString(value.getName(), startX + 328, startY + 55 + rightY + 14 + valueY, new Color(156, 178, 191).getRGB());

                        RenderUtil.drawFastRoundedRect((int) (startX + 439), startY + 57 + rightY + 9 + valueY, (int) (startX + 502), startY + 65 + rightY + 15 + valueY, 3, new Color(3, 23, 46).getRGB());
                        RenderUtil.drawFastRoundedRect((int) (startX + 440), startY + 58 + rightY + 9 + valueY, (int) (startX + 501), startY + 64 + rightY + 15 + valueY, 3, new Color(3, 5, 13).getRGB());

                        Fonts.PF16.drawCenteredString(value.isDownopen() ? "...." : value.getValue().toString(),
                                startX + 470, startY + 57 + rightY + 10 + valueY + 3,
                                new Color(200, 200, 200).getRGB());

                        String msg = value.getValue().toString();

                        if (RenderUtil.isHovered(startX + 440, startY + 58 + rightY + 9 + valueY, startX + 501, startY + 64 + rightY + 15 + valueY, mouseX, mouseY) && !previousMouse && Mouse.isButtonDown(0)) {
                            value.setDownopen(!value.isDownopen());
                            previousMouse = true;
                            mc.thePlayer.playSound("random.click", 1, 1);
                        }
                        if (value.isDownopen()) {
                            RenderUtil.drawFastRoundedRect((int) (startX + 439), startY + 65 + rightY + 17 + valueY, (int) (startX + 502), startY + 65 + rightY + 17 + valueY + 12 * value.listModes().size(), 3, new Color(3, 23, 46).getRGB());
                            RenderUtil.drawFastRoundedRect((int) (startX + 440), startY + 66 + rightY + 17 + valueY, (int) (startX + 501), startY + 65 + rightY + 16 + valueY + 12 * value.listModes().size(), 3, new Color(3, 5, 13).getRGB());

                            int downY = 0;
                            for (int v = 0; v < value.listModes().size(); v++) {
                                Fonts.PF14.drawCenteredString(value.getModeAt(v),
                                        startX + 470, startY + 60 + rightY + 24 + valueY + downY + 2,
                                        msg.equals(value.getModeAt(v)) ? new Color(57, 124, 210).getRGB() : new Color(114, 132, 144).getRGB());
                                if (RenderUtil.isHovered(startX + 440, startY + 66 + rightY + 17 + valueY + downY, startX + 501, startY + 66 + rightY + 17 + valueY + downY + 12, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                                    if (value instanceof Mode) {
                                        Mode vs = (Mode) value;
                                        vs.setCurrentMode(v);
                                        value.setDownopen(!value.isDownopen());
                                    }
                                    mc.thePlayer.playSound("random.click", 1, 1);
                                    previousMouse = true;
                                }
                                downY += 12;
                            }
                        }
                        valueY += value.isDownopen() ? 20 + 12 * value.listModes().size() : 20;
                    }
                    // }
                }
                rightY += 16 + (size + 1) * 20;
            } else {
                GL11.glScaled(sizes, sizes, sizes);

                int listY = 0;
                for (Value v : m.getValues()) {
                    if (ClickGui.Visitable.getValue()) {
                        if (!v.isVisitable()) continue;
                    }
                    size += v.isDownopen() ? v.listModes().size() + 1 : 1;
                }
                /**
                 * break not use render, that can fix FPS
                 */
                if (leftY > startY + 185 + 100) {
                    break;
                }
                RenderUtil.drawFastRoundedRect((int) (startX + 130), startY + 50 + leftY, (int) (startX + 315), startY + 62 + leftY + (size + 1) * 20 + listY, 3, new Color(3, 13, 26).getRGB());
                RenderUtil.drawRect(startX + 133, startY + 50 + leftY + 14, startX + 312, startY + 52 + leftY + 14, new Color(5, 26, 38).getRGB());
                /**
                 * mod name of left
                 */

                Fonts.PF19.drawString(m.getName(), startX + 134, startY + 51 + leftY + 2, -1);

                RenderUtil.drawRect(startX + 289, startY + 58 + leftY + 13 - 18, startX + 306, startY + 64 + leftY + 15 - 18, new Color(3, 23, 46).getRGB());
                RenderUtil.drawCircle(startX + 290, startY + 61 + leftY + 14 - 18, 4, new Color(3, 23, 46));
                RenderUtil.drawCircle(startX + 305, startY + 61 + leftY + 14 - 18, 4, new Color(3, 23, 46));
                RenderUtil.drawRect(startX + 290, startY + 58 + leftY + 14 - 18, startX + 305, startY + 64 + leftY + 14 - 18, m.isEnabled() ? new Color(0, 102, 148).getRGB() : new Color(3, 6, 14).getRGB());
                RenderUtil.drawCircle(startX + 290, startY + 61 + leftY + 14 - 18, 3, m.isEnabled() ? new Color(0, 102, 148) : new Color(3, 6, 14));
                RenderUtil.drawCircle(startX + 305, startY + 61 + leftY + 14 - 18, 3, m.isEnabled() ? new Color(0, 102, 148) : new Color(3, 6, 14));

                RenderUtil.drawCircle(startX + 292 + (m.isEnabled() ? 11 : 0), startY + 61 - 18 + leftY + 14, 5, m.isEnabled() ? new Color(3, 168, 245) : new Color(120, 139, 151));

                if (RenderUtil.isHovered(startX + 290, startY + 58 + leftY + 14 - 18, startX + 305, startY + 64 + leftY + 14 - 18, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                    m.setEnabled(!m.isEnabled());
                    previousMouse = true;
                }

                valueY = 5;
                for (Value value : m.getValues()) {
                    if (ClickGui.Visitable.getValue()) {
                        if (!value.isVisitable()) continue;
                    }
                    if (value instanceof Option) {
                        Fonts.PF18.drawString(value.getName(), startX + 133, startY + 55 + leftY + 14 + valueY, new Color(156, 178, 191).getRGB());

                        RenderUtil.drawRect(startX + 289, startY + 58 + leftY + 11 + valueY, startX + 306, startY + 64 + leftY + 13 + valueY, new Color(3, 23, 46).getRGB());
                        RenderUtil.drawCircle(startX + 290, startY + 61 + leftY + 12 + valueY, 4, new Color(3, 23, 46));
                        RenderUtil.drawCircle(startX + 305, startY + 61 + leftY + 12 + valueY, 4, new Color(3, 23, 46));

                        RenderUtil.drawRect(startX + 290, startY + 57 + leftY + 12 + valueY, startX + 305, startY + 65 + leftY + 12 + valueY, (boolean) value.getValue() ? new Color(0, 102, 148).getRGB() : new Color(3, 6, 14).getRGB());

                        RenderUtil.drawCircle(startX + 290, startY + 61 + leftY + 12 + valueY, 3, (boolean) value.getValue() ? new Color(0, 102, 148) : new Color(3, 6, 14));
                        RenderUtil.drawCircle(startX + 305, startY + 61 + leftY + 12 + valueY, 3, (boolean) value.getValue() ? new Color(0, 102, 148) : new Color(3, 6, 14));

                        RenderUtil.drawCircle(startX + 292 + ((boolean) value.getValue() ? 11 : 0), startY + 61 + leftY + 12 + valueY, 5, (boolean) value.getValue() ? new Color(3, 168, 245) : new Color(120, 139, 151));

                        if (RenderUtil.isHovered(startX + 290, startY + 58 + leftY + 10 + valueY, startX + 305, startY + 64 + leftY + 14 + valueY, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                            value.setValue(!(boolean) value.getValue());
                            mc.thePlayer.playSound("random.click", 1, 1);
                            previousMouse = true;
                        }
                        valueY += 20;
                    }

                    if (value instanceof Numbers) {
                        Fonts.PF18.drawString(value.getName(), startX + 328 - 195, startY + 55 + leftY + 14 + valueY, new Color(156, 178, 191).getRGB());

                        RenderUtil.drawRect(startX + 415 - 195, startY + 54 + leftY + 19 + valueY, startX + 480 - 195, startY + 54 + leftY + 21 + valueY, new Color(3, 23, 46).getRGB());
                        Numbers<Number> s = (Numbers<Number>) value;
                        double max;
                        RenderUtil.drawRect(startX + 415 - 195, startY + 54 + leftY + 19 + valueY, startX + 480 - 195, startY + 54 + leftY + 21 + valueY, new Color(3, 23, 46).getRGB());

                        double render = (68.0F * (s.getValue().doubleValue() - (Double) s.getMin()) / ((Double) s.getMax() - (Double) s.getMin()));

                        RenderUtil.drawRect(startX + 415 - 195, startY + 54 + leftY + 19 + valueY, startX + 415 + render - 195, startY + 54 + leftY + 21 + valueY, new Color(0, 102, 148).getRGB());

                        RenderUtil.drawCircle(startX + 416 + render - 195, startY + 54 + leftY + 20 + valueY, 3, new Color(61, 133, 224));

                        Fonts.PF14.drawCenteredString(String.valueOf(value.getValue()), startX + 498 - 195, startY + 55 + leftY + 14 + valueY, -1);

                        if (RenderUtil.isHovered(startX + 415 - 195, startY + 54 + leftY + 19 + valueY, startX + 483 - 195, startY + 54 + leftY + 21 + valueY, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                            render = (double) s.getMin();
                            max = (double) s.getMax();
                            double inc = (double) s.getIncrement();
                            //	double val = (mouseX - (startX + 415 - 195)) * ((Double)value.getMaxValue() - (Double)value.getMinValue()) / 65.0F + (Double)value.getMinValue();
                            double valAbs = mouseX - (startX + 415 - 195);
                            double perc = valAbs / 68.0D;
                            perc = Math.min(Math.max(0.0D, perc), 1.0D);
                            double valRel = (max - render) * perc;
                            double val = render + valRel;
                            val = Math.round(val * (1.0D / inc)) / (1.0D / inc);
                            double num = val;
                            value.setValue(num);
                        }

                        valueY += 20;
                    }

                    if (value instanceof Mode) {
                        // Mode mode = (Mode) value;
                        try {
                            if (!value.listModes().isEmpty()) {
                                Fonts.PF18.drawString(value.getName(), startX + 328 - 195, startY + 55 + leftY + 14 + valueY, new Color(156, 178, 191).getRGB());

                                RenderUtil.drawFastRoundedRect((int) (startX + 439 - 195), startY + 57 + leftY + 9 + valueY, (int) (startX + 502 - 195), startY + 65 + leftY + 15 + valueY, 3, new Color(3, 23, 46).getRGB());
                                RenderUtil.drawFastRoundedRect((int) (startX + 440 - 195), startY + 58 + leftY + 9 + valueY, (int) (startX + 501 - 195), startY + 64 + leftY + 15 + valueY, 3, new Color(3, 5, 13).getRGB());

                                Fonts.PF16.drawCenteredString(value.isDownopen() ? "...." : ((Mode<?>) value).getValue().toString(),
                                        startX + 470 - 195, startY + 57 + leftY + 10 + valueY + 3,
                                        new Color(200, 200, 200).getRGB());
                                String msg = ((Mode<?>) value).getValue().toString();

                                if (RenderUtil.isHovered(startX + 440 - 195, startY + 58 + leftY + 9 + valueY, startX + 501 - 195, startY + 64 + leftY + 15 + valueY, mouseX, mouseY) && !previousMouse && Mouse.isButtonDown(0)) {
                                    value.setDownopen(!value.isDownopen());
                                    previousMouse = true;
                                    mc.thePlayer.playSound("random.click", 1, 1);
                                }

                                if (value.isDownopen()) {
                                    RenderUtil.drawFastRoundedRect((int) (startX + 439 - 195), startY + 65 + leftY + 17 + valueY, (int) (startX + 502 - 195), startY + 65 + leftY + 17 + valueY + 12 * value.listModes().size(), 3, new Color(3, 23, 46).getRGB());
                                    RenderUtil.drawFastRoundedRect((int) (startX + 440 - 195), startY + 66 + leftY + 17 + valueY, (int) (startX + 501 - 195), startY + 65 + leftY + 16 + valueY + 12 * value.listModes().size(), 3, new Color(3, 5, 13).getRGB());
                                    int downY = 0;
                                    for (int v = 0; v < value.listModes().size(); v++) {
                                        Fonts.PF14.drawCenteredString(value.getModeAt(v),
                                                startX + 470 - 195, startY + 60 + leftY + 24 + valueY + downY + 2,
                                                value.getModeAt(v).equals(msg) ? new Color(57, 124, 210).getRGB() : new Color(114, 132, 144).getRGB());
                                        if (RenderUtil.isHovered(startX + 440 - 195, startY + 66 + leftY + 17 + valueY + downY, startX + 501 - 195, startY + 66 + leftY + 17 + valueY + downY + 12, mouseX, mouseY) && Mouse.isButtonDown(0) && !previousMouse) {
                                            Mode vs = (Mode) value;
                                            vs.setCurrentMode(v);
                                            value.setDownopen(!value.isDownopen());
                                            mc.thePlayer.playSound("random.click", 1, 1);
                                            previousMouse = true;
                                        }
                                        downY += 12;
                                    }
                                }
                                valueY += value.isDownopen() ? 20 + 12 * value.listModes().size() : 20;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                leftY += 16 + (size + 1) * 20;
            }
        }
        RenderUtil.stopGlScissor();

        int real = Mouse.getDWheel();
        /**
         * is bad but i dont will fix it XD
         */
        //  float moduleHeight = !useLeft ? leftY - translate.getY() : rightY - translate.getY();
        float moduleHeight = Math.max(leftY - translate.getY(), rightY - translate.getY());

        if (Mouse.hasWheel() && mouseX > startX + 120 && mouseY > startY && mouseX < startX + 520 && mouseY < startY + 40 + 420) {
            if (real > 0 && wheel < 0) {
                for (int i = 0; i < 5; i++) {
                    if (!(wheel < 0))
                        break;
                    wheel += 5;
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (!(real < 0 && moduleHeight > 240 && Math.abs(wheel) < (moduleHeight - (236))))
                        break;
                    wheel -= 5;
                }
            }
        }
        /**
         * make wheel smooth
         */
        translate.interpolate(0, wheel, 0.25F);

        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            previousMouse = false;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static void setX(int state) {
        startX = state;
    }

    public static void setY(int state) {
        startY = state;
    }

    public static void setCategory(ModuleType state) {
        currentCategory = state;
    }

    public static void setWheel(int state) {
        wheel = state;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ClickGui.currentModuleType = currentCategory;
        ClickGui.startX = startX;
        ClickGui.startY = startY;
        ClickGui.tempWheel = wheel;
    }

    public static class TranslateUtil {
        private float x;
        private float y;
        private long lastMS;

        public TranslateUtil(float x, float y) {
            this.x = x;
            this.y = y;
            lastMS = System.currentTimeMillis();
        }

        public void interpolate(float targetX, float targetY, float smoothing) {
            long currentMS = System.currentTimeMillis();
            long delta = currentMS - lastMS;
            lastMS = currentMS;
            int deltaX = (int) (Math.abs(targetX - x) * smoothing);
            int deltaY = (int) (Math.abs(targetY - y) * smoothing);
            x = AnimationUtil.calculateCompensation(targetX, x, delta, deltaX);
            y = AnimationUtil.calculateCompensation(targetY, y, delta, deltaY);
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public enum RenderUtil {
        ;
        public static final Minecraft mc = Minecraft.getMinecraft();

        public static void drawRect(float left, float top, float right, float bottom, int color) {
            float f3;
            if (left < right) {
                f3 = left;
                left = right;
                right = f3;
            }

            if (top < bottom) {
                f3 = top;
                top = bottom;
                bottom = f3;
            }

            f3 = (color >> 24 & 255) / 255.0F;
            float f = (color >> 16 & 255) / 255.0F;
            float f1 = (color >> 8 & 255) / 255.0F;
            float f2 = (color & 255) / 255.0F;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer WorldRenderer = tessellator.getWorldRenderer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(f, f1, f2, f3);
            WorldRenderer.begin(7, DefaultVertexFormats.POSITION);
            WorldRenderer.pos(left, bottom, 0.0D).endVertex();
            WorldRenderer.pos(right, bottom, 0.0D).endVertex();
            WorldRenderer.pos(right, top, 0.0D).endVertex();
            WorldRenderer.pos(left, top, 0.0D).endVertex();
            tessellator.draw();
            enableTexture2D();
            disableBlend();
        }

        public static void rectangleBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
            rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            rectangle(x + width, y, x1 - width, y + width, borderColor);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            rectangle(x, y, x + width, y1, borderColor);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            rectangle(x1 - width, y, x1, y1, borderColor);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public static void rectangle(double left, double top, double right, double bottom, final int color) {
            double var5;
            if (left < right) {
                var5 = left;
                left = right;
                right = var5;
            }
            if (top < bottom) {
                var5 = top;
                top = bottom;
                bottom = var5;
            }
            final float var11 = (color >> 24 & 255) / 255.0f;
            final float var6 = (color >> 16 & 255) / 255.0f;
            final float var7 = (color >> 8 & 255) / 255.0f;
            final float var8 = (color & 255) / 255.0f;
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(var6, var7, var8, var11);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(left, bottom, 0.0).endVertex();
            worldRenderer.pos(right, bottom, 0.0).endVertex();
            worldRenderer.pos(right, top, 0.0).endVertex();
            worldRenderer.pos(left, top, 0.0).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public static void drawRect(double d, double e, double g, double h, int color) {
            int f3;
            if (d < g) {
                f3 = (int) d;
                d = g;
                g = f3;
            }

            if (e < h) {
                f3 = (int) e;
                e = h;
                h = f3;
            }

            float f31 = (color >> 24 & 255) / 255.0F;
            float f = (color >> 16 & 255) / 255.0F;
            float f1 = (color >> 8 & 255) / 255.0F;
            float f2 = (color & 255) / 255.0F;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(f, f1, f2, f31);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION);
            worldrenderer.pos(d, h, 0.0D).endVertex();
            worldrenderer.pos(g, h, 0.0D).endVertex();
            worldrenderer.pos(g, e, 0.0D).endVertex();
            worldrenderer.pos(d, e, 0.0D).endVertex();
            tessellator.draw();
            enableTexture2D();
            disableBlend();
        }

        public static void drawFastRoundedRect(int x0, float y0, int x1, float y1, float radius, int color) {
            float f2 = (color >> 24 & 0xFF) / 255.0f;
            float f3 = (color >> 16 & 0xFF) / 255.0f;
            float f4 = (color >> 8 & 0xFF) / 255.0f;
            float f5 = (color & 0xFF) / 255.0f;
            glDisable(2884);
            glDisable(3553);
            glEnable(3042);
            glBlendFunc(770, 771);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            glColor4f(f3, f4, f5, f2);
            glBegin(5);
            glVertex2f(x0 + radius, y0);
            glVertex2f(x0 + radius, y1);
            glVertex2f(x1 - radius, y0);
            glVertex2f(x1 - radius, y1);
            glEnd();
            glBegin(5);
            glVertex2f(x0, y0 + radius);
            glVertex2f(x0 + radius, y0 + radius);
            glVertex2f(x0, y1 - radius);
            glVertex2f(x0 + radius, y1 - radius);
            glEnd();
            glBegin(5);
            glVertex2f(x1, y0 + radius);
            glVertex2f(x1 - radius, y0 + radius);
            glVertex2f(x1, y1 - radius);
            glVertex2f(x1 - radius, y1 - radius);
            glEnd();
            glBegin(6);
            float f6 = x1 - radius;
            float f7 = y0 + radius;
            glVertex2f(f6, f7);
            int j;
            for (j = 0; j <= 18; ++j) {
                float f8 = j * 5.0f;
                glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(f8))), (float) (f7 - radius * Math.sin(Math.toRadians(f8))));
            }
            glEnd();
            glBegin(6);
            f6 = x0 + radius;
            f7 = y0 + radius;
            glVertex2f(f6, f7);
            for (j = 0; j <= 18; ++j) {
                float f9 = j * 5.0f;
                glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(f9))), (float) (f7 - radius * Math.sin(Math.toRadians(f9))));
            }
            glEnd();
            glBegin(6);
            f6 = x0 + radius;
            f7 = y1 - radius;
            glVertex2f(f6, f7);
            for (j = 0; j <= 18; ++j) {
                float f10 = j * 5.0f;
                glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(f10))), (float) (f7 + radius * Math.sin(Math.toRadians(f10))));
            }
            glEnd();
            glBegin(6);
            f6 = x1 - radius;
            f7 = y1 - radius;
            glVertex2f(f6, f7);
            for (j = 0; j <= 18; ++j) {
                float f11 = j * 5.0f;
                glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(f11))), (float) (f7 + radius * Math.sin(Math.toRadians(f11))));
            }
            glEnd();
            glEnable(3553);
            glEnable(2884);
            glDisable(3042);
            enableTexture2D();
            disableBlend();
        }

        public static void startGlScissor(int x, int y, int width, int height) {
            int scaleFactor = new ScaledResolution(mc).getScaleFactor();
            glPushMatrix();
            glEnable(3089);
            glScissor(x * scaleFactor, Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor, width * scaleFactor, (height + 14) * scaleFactor);
        }

        public static void stopGlScissor() {
            glDisable(3089);
            glPopMatrix();
        }

        public static void resetColor() {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            glColor4f(1, 1, 1, 1);
        }

        public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
        }

        private static void drawCircle(final double xPos, final double yPos, final double radius) {
            final double theta = (2 * Math.PI / 360.0);
            final double tangetial_factor = Math.tan(theta);//calculate the tangential factor
            final double radial_factor = Math.cos(theta);//calculate the radial factor
            double x = radius;//we start at angle = 0
            double y = 0;
            for (int i = 0; i < 360; i++) {
                glVertex2d(x + xPos, y + yPos);

                //calculate the tangential vector
                //remember, the radial vector is (x, y)
                //to get the tangential vector we flip those coordinates and negate one of them
                double tx = -y;
                double ty = x;

                //add the tangential vector
                x += tx * tangetial_factor;
                y += ty * tangetial_factor;

                //correct using the radial factor
                x *= radial_factor;
                y *= radial_factor;
            }
        }

        public static void drawCircle(final double xPos, final double yPos, final double radius, final Color color) {
            NLRenderUtil.startRender();
            NLRenderUtil.color(color);
            glBegin(GL_POLYGON);
            {
                drawCircle(xPos, yPos, radius);
            }
            glEnd();

            glEnable(GL_LINE_SMOOTH);
            glLineWidth(2);
            glBegin(GL_LINE_LOOP);
            {
                drawCircle(xPos, yPos, radius);
            }
            glEnd();
            NLRenderUtil.stopRender();
        }
    }

    public static class NLRenderUtil {
        /**
         * Sets up basic rendering parameters
         */
        public static final void startRender() {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_ALPHA_TEST);
            glDisable(GL_CULL_FACE);
        }

        /**
         * Resets the rendering parameters
         */
        public static final void stopRender() {
            glEnable(GL_CULL_FACE);
            glEnable(GL_ALPHA_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            color(Color.white);
        }

        /**
         * Sets the current color using rgb values
         *
         * @param color
         */
        public static final void color(final Color color) {
            glColor4d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0);
        }
    }

    public static class AnimationUtil {
        public static float moveUD(final float current, final float end, final float smoothSpeed, final float minSpeed) {
            float movement = (end - current) * smoothSpeed;
            if (movement > 0.0f) {
                movement = Math.max(minSpeed, movement);
                movement = Math.min(end - current, movement);
            } else if (movement < 0.0f) {
                movement = Math.min(-minSpeed, movement);
                movement = Math.max(end - current, movement);
            }
            return current + movement;
        }

        public static float calculateCompensation(final float target, float current, long delta, final int speed) {
            final float diff = current - target;
            if (delta < 1L) {
                delta = 1L;
            }
            if (diff > speed) {
                final double xD = (speed * delta / 16L < 0.25) ? 0.5 : ((double) (speed * delta / 16L));
                current -= (float) xD;
                if (current < target) {
                    current = target;
                }
            } else if (diff < -speed) {
                final double xD = (speed * delta / 16L < 0.25) ? 0.5 : ((double) (speed * delta / 16L));
                current += (float) xD;
                if (current > target) {
                    current = target;
                }
            } else {
                current = target;
            }
            return current;
        }
    }
}
