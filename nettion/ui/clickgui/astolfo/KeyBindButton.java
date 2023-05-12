package nettion.ui.clickgui.astolfo;


import net.minecraft.client.gui.Gui;
import nettion.features.module.Module;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.fonts.old.TFontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class KeyBindButton
        extends ValueButton {
    public final Module cheat;
    public boolean bind;

    public KeyBindButton(Module cheat, int x, int y) {
        super(null, x, y);
        custom = true;
        bind = false;
        this.cheat = cheat;
    }

    @Override
    public void render(int mouseX, int mouseY, Limitation limitation, Window parent) {
        final TFontRenderer mfont = Fonts.R15;
        GL11.glEnable(3089);
        limitation.cut();
        Gui.drawRect(0.0, 0.0, 0.0, 0.0, 0);
        Gui.drawRect(x - 10, y - 4, x + 80 + parent.x, y + 11, new Color(20, 20, 20).getRGB());
        mfont.drawString("Bind:", x - 7, y + 2, new Color(108, 108, 108).getRGB());
        mfont.drawString(Keyboard.getKeyName(cheat.getKey()), x + 77 - mfont.getStringWidth(Keyboard.getKeyName(cheat.getKey())) + parent.x, y + 2, new Color(108, 108, 108).getRGB());
        GL11.glDisable(3089);
    }

    @Override
    public void key(char typedChar, int keyCode) {
        if (bind) {
            cheat.setKey(keyCode);
            if (keyCode == 1) {
                cheat.setKey(0);
            }
            asClickgui.binding = false;
            bind = false;
        }
        super.key(typedChar, keyCode);
    }

    @Override
    public void click(int mouseX, int mouseY, int button, Window parent) {
        if (mouseX > x - 7 && mouseX < x + 85 + parent.allX && mouseY > y - 6 && mouseY < y + Fonts.R18.getHeight() + 5 && button == 0) {
            bind = !bind;
            asClickgui.binding = bind;
        }
        super.click(mouseX, mouseY, button, parent);
    }
}

