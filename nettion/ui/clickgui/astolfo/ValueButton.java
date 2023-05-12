package nettion.ui.clickgui.astolfo;

import net.minecraft.client.gui.Gui;
import nettion.features.value.Value;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.fonts.old.TFontRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ValueButton {
    public final Value value;
    public String name;
    public boolean custom;
    public int x;
    public int y;
    int staticColor;

    public ValueButton(Value value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
        name = "";
        if (this.value instanceof Mode) {
            name = String.valueOf(this.value.getValue());
        } else if (value instanceof Numbers) {
            Numbers v = (Numbers) value;
            name = name + (v.isInteger() ? ((Number) v.getValue()).intValue() : ((Number) v.getValue()).doubleValue());
        }
    }

    public void render(int mouseX, int mouseY, Limitation limitation, Window parent) {
        final TFontRenderer mfont = Fonts.R15;
        if (!custom) {
            if (value instanceof Mode) {
                name = String.valueOf(value.getValue());
            } else if (value instanceof Numbers) {
                Numbers v = (Numbers) value;
                name = String.valueOf(v.isInteger() ? ((Number) v.getValue()).intValue() : ((Number) v.getValue()).doubleValue());
                if (mouseX > x - 7 && mouseX < x + 85 + parent.allX && mouseY > y + Fonts.R15.getHeight() - 10 && mouseY < y + mfont.getHeight() + 2 && Mouse.isButtonDown(0)) {
                    double min = v.getMin().doubleValue();
                    double max = v.getMax().doubleValue();
                    double inc = v.getIncrement().doubleValue();
                    double valAbs = mouseX - (x + 1.0);
                    double perc = valAbs / 68.0;
                    perc = Math.min(Math.max(0.0, perc), 1.0);
                    double valRel = (max - min) * perc;
                    double val = min + valRel;
                    val = Math.round(val * (1.0 / inc)) / (1.0 / inc);
                    v.setValue(val);
                }
            }
            switch (parent.category.name()) {
                case "Combat": {
                    staticColor = new Color(231, 76, 60).getRGB();
                    break;
                }
                case "Render": {
                    staticColor = new Color(54, 1, 205).getRGB();
                    break;
                }
                case "Movement": {
                    staticColor = new Color(45, 203, 113).getRGB();
                    break;
                }
                case "Player": {
                    staticColor = new Color(141, 68, 173).getRGB();
                    break;
                }
                case "World": {
                    staticColor = new Color(38, 154, 255).getRGB();
                    break;
                }
                case "Misc": {
                    staticColor = new Color(102, 101, 101).getRGB();
                    break;
                }
            }
            GL11.glEnable(3089);
            limitation.cut();
            Gui.drawRect(x - 10, y - 4, x + 80 + parent.allX, y + 11, new Color(20, 20, 20).getRGB());
            if (value instanceof Option) {
                mfont.drawString(value.getName(), x - 7, y + 2, (Boolean) value.getValue() ? new Color(255, 255, 255).getRGB() : new Color(108, 108, 108).getRGB());
            }
            if (value instanceof Mode) {
                mfont.drawString(value.getName(), x - 7, y + 3, new Color(255, 255, 255).getRGB());
                mfont.drawString(name, x + 77 + parent.allX - mfont.getStringWidth(name), y + 3, new Color(182, 182, 182).getRGB());
            }
            if (value instanceof Numbers) {
                Numbers v = (Numbers) value;
                double render = (82.0f + parent.allX) * AnimationUtil.getAnimationState((((Number) v.getValue()).floatValue() - v.getMin().floatValue()) / (v.getMax().floatValue() - v.getMin().floatValue()), 0, 0.1f);
                Gui.drawRect(x - 8, y + mfont.getHeight() + 2, (float) ((x - 4) + render), y + mfont.getHeight() - 9, staticColor);
                mfont.drawString(value.getName(), x - 7, y, new Color(255, 255, 255).getRGB());
                mfont.drawString(name, x + mfont.getStringWidth(value.getName()), y, -1);
            }
            GL11.glDisable(3089);
        }
    }

    public void key(char typedChar, int keyCode) {
    }

    public void click(int mouseX, int mouseY, int button, Window parent) {
        if ((!custom) && (mouseX > x - 7) && (mouseX < x + 85) && (mouseY > y - 6) && (mouseY < y + Fonts.R15.getHeight())) {
            if ((value instanceof Option)) {
                Option v = (Option) value;
                v.setValue(!(Boolean) v.getValue());
                return;
            }
            if (value instanceof Mode) {
                Mode m = (Mode) value;
                if ((button == 0 || button == 1)) {
                    List<String> options = Arrays.asList(m.getModes().toString());
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

