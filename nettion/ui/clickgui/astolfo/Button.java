package nettion.ui.clickgui.astolfo;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import nettion.features.module.Module;
import nettion.features.value.Value;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.fonts.old.TFontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Button {
    public final Module cheat;
    public Window parent;
    public int x;
    public int y;
    public int arrow;
    public int index;
    public int remander;
    public double opacity;
    public final ArrayList<ValueButton> buttons = Lists.newArrayList();
    public boolean expand;
    int staticColor;

    public Button(Module cheat, int x, int y) {
        this.cheat = cheat;
        this.x = x;
        this.y = y;
        int y2 = y + 15;
        for (Value<?> v : cheat.getValues()) {
            if (!v.isVisitable()) continue;
            buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        buttons.add(new KeyBindButton(cheat, x + 5, y2 - 5));
    }

    @SuppressWarnings("unlikely-arg-type")
    public void render(int mouseX, int mouseY, Limitation limitation) {
        final TFontRenderer font = Fonts.R17;
        final TFontRenderer mfont = Fonts.R15;
        int y2 = y + 15;
        buttons.clear();
        for (Value<?> v : cheat.getValues()) {
            if (!v.isVisitable()) continue;
            buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        if (index != 0) {
            Button b2 = parent.buttons.get(index - 1);
            y = b2.y + 15 + (b2.expand ? 15 * b2.buttons.size() : 0);
        }
        int i = 0;
        while (i < this.buttons.size()) {
            this.buttons.get(i).y = this.y + 14 + 15 * i;
            this.buttons.get(i).x = this.x + 5;
            ++i;
        }
        switch (parent.category.name()) {
            case "Combat": {
                staticColor = new Color(231, 76, 60).getRGB();
                break;
            }
            case "Ghost": {
                staticColor = new Color(102, 101, 101).getRGB();
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
            case "Render": {
                staticColor = new Color(54, 1, 205).getRGB();
                break;
            }
            case "World": {
                staticColor = new Color(38, 154, 255).getRGB();
                break;
            }

        }

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        limitation.doGlScissor(x - 5, y - 5, 90 + parent.allX, font.getHeight() + 5);
        limitation.cut();
        Gui.drawRect(x - 5, y - 5, x + 85 + parent.allX, y + 5 + font.getHeight(), new Color(39, 39, 39).getRGB());
        if (cheat.isEnabled()) {
            limitation.cut();
            Gui.drawRect(x - 4, y - 5, x + 84 + parent.allX, y + 10, staticColor);
        }
        limitation.cut();
        mfont.drawString(cheat.getName().toLowerCase(), x + 81 + parent.allX - mfont.getStringWidth(cheat.getName().toLowerCase()), y, new Color(220, 220, 220).getRGB());
        if (mouseX > x - 7 && mouseX < x + 85 + parent.allX && mouseY > y - 6 && mouseY < y + mfont.getHeight()) {
            Gui.drawRect(x - 4, y - 5, x + 84 + parent.allX, y + 10, new Color(233, 233, 233, 30).getRGB());
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
        if (expand) {
            buttons.forEach(component -> component.render(mouseX, mouseY, limitation, parent));
        }
    }

    public void key(char typedChar, int keyCode) {
        buttons.forEach(b -> b.key(typedChar, keyCode));
    }

    public void click(int mouseX, int mouseY, int button) {
        if (parent.drag) {
            return;
        }
        if (mouseX > x - 7 && mouseX < x + 85 + parent.allX && mouseY > y - 6 && mouseY < y + Fonts.R16.getHeight()) {
            if (button == 0) {
                cheat.setEnabled(!cheat.isEnabled());
            }
            if (button == 1 && !buttons.isEmpty()) {
                expand = !expand;
            }
        }
        if (expand) {
            buttons.forEach(b -> b.click(mouseX, mouseY, button, parent));
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
        int i2 = 0;
        while (i2 < this.parent.buttons.size()) {
            if (this.parent.buttons.get(i2) == this) {
                this.index = i2;
                this.remander = this.parent.buttons.size() - i2;
                break;
            }
            ++i2;
        }
    }
}

