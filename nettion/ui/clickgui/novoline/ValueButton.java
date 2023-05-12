package nettion.ui.clickgui.novoline;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import nettion.features.value.Value;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.ui.clickgui.neverlose.ClickUI;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.fonts.old.TFontRenderer;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ValueButton {
    public final Value value;
    public String name;
    public boolean custom;
    public boolean change;
    public int x;
    public float y;
    public static int valuebackcolor;


    public ValueButton(Value value, int x, float y) {

        this.value = value;
        this.x = x;
        this.y = y;

        name = "";
        if (this.value instanceof Option) {
            change = (boolean) this.value.getValue();
        } else if (this.value instanceof Mode) {
            name = String.valueOf(this.value.getValue());
        } else if (value instanceof Numbers) {
            Numbers v = (Numbers) value;
            name = name + (v.isInteger() ? ((Number) v.getValue()).intValue() : ((Number) v.getValue()).doubleValue());
        }
    }

    public void render(int mouseX, int mouseY, Window parent) {
        final TFontRenderer font = Fonts.R16;
        Gui.drawRect(x - 10, y - 7, x + 80 + parent.allX, y + 11, new Color(255, 255, 255, 0).getRGB());
        if (value instanceof Option) {
            change = (boolean) value.getValue();
        } else if (value instanceof Mode) {
            name = String.valueOf(value.getValue()).toUpperCase();
        } else if (value instanceof Numbers) {
            Numbers v = (Numbers) value;
            name = String.valueOf(((Number) v.getValue()).doubleValue());
            if (mouseX > x - 9 && mouseX < x + 87 && mouseY > y - 4 && mouseY < y + font.getHeight() + 4 && Mouse.isButtonDown(0)) {
                final double min = v.getMin().doubleValue();
                final double max = v.getMax().doubleValue();
                final double inc = v.getIncrement().doubleValue();
                final double valAbs = mouseX - (x + 1);
                double perc = valAbs / 68;
                perc = Math.min(Math.max(0, perc), 1);
                final double valRel = (max - min) * perc;
                double val = min + valRel;
                val = Math.round(val * (1 / inc)) / (1 / inc);
                v.setValue(val);
            }
            double number = 86 * (((Number) v.getValue()).floatValue() - v.getMin().floatValue()) / (v.getMax().floatValue() - v.getMin().floatValue());
            GlStateManager.pushMatrix();
            GlStateManager.translate(-9.0f, 1.0f, 0.0f);
            Gui.drawRect(x + 1, y - 6, (x + 87.0f + parent.allX), y + font.getHeight() + 6, new Color(255, 255, 255, 68).getRGB());
            Gui.drawRect(x + 1, y - 6, (x + number + 1.0 + parent.allX), y + font.getHeight() + 6, valuebackcolor);
            GlStateManager.popMatrix();
        }
        if (value instanceof Option) {
            final int size = 2;
            if (change) {
                ClickUI.RenderUtil.drawRect(x + 62 + size + parent.allX + 4, y - 4 + size - 1, x + 76 - size + parent.allX + 4, y + 9 - size, new Color(0, 0, 0, 56).getRGB());
                Fonts.novoicons18.drawString("H", x + 64.5f + parent.allX + 4, y + 1, valuebackcolor);
            } else {
                ClickUI.RenderUtil.drawRect(x + 62 + size + parent.allX + 4, y - 4 + size - 1, x + 76 - size + parent.allX + 4, y + 9 - size, new Color(255, 255, 255, 81).getRGB());
            }
        }

        if (!(value instanceof Numbers)) {
            font.drawString(value.getName(), x - 7, y, -1);
        }
        if (value instanceof Option) {
            font.drawString(name, x + font.getStringWidth(value.getName()), y, -1);
        }
        if (value instanceof Numbers) {
            font.drawString(value.getName(), x - 7, y - 1, -1);
            font.drawString(name, x + font.getStringWidth(value.getName()), y - 1, -1);
        }
        if (value instanceof Mode) {
            font.drawString(name, x + 90 - font.getStringWidth(name), y, -1);
        }
    }


    public void key(char typedChar, int keyCode) {
    }

    public void click(int mouseX, int mouseY, int button) {
        if (!custom && mouseX > x - 9 && mouseX < x + 87 && mouseY > y - 4 && mouseY < y + Fonts.R18.getHeight() + 4) {
            if (value instanceof Option) {
                Option m1 = (Option) value;
                m1.setValue(!(Boolean) m1.getValue());
                return;
            }
            if (value instanceof Mode) {
                Mode m = (Mode) value;
                if ((button == 0 || button == 1)) {
                    List<String> options = Arrays.asList(m.getModes().toString());
                    //noinspection SuspiciousMethodCalls
                    int index = options.indexOf(m.getValue());
                    if (button == 0) {
                        index++;
                    } else {
                        index--;
                    }
                    if (index >= options.size()) {
                        index = 0;
                    } else if (index < 0) {
                        index = options.size() - 1;
                    }
                    m.setValue(m.getModes()[index]);
                }
            }
        }
    }
}
