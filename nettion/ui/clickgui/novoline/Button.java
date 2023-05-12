package nettion.ui.clickgui.novoline;

import net.minecraft.client.Minecraft;
import nettion.features.module.Module;
import nettion.features.module.modules.render.ClickGui;
import nettion.features.value.Value;
import nettion.ui.clickgui.astolfo.AnimationUtil;
import nettion.ui.clickgui.astolfo.Limitation;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.fonts.old.TFontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Button {
    public Module cheat;
    public Window parent;
    public int x;
    public float y;
    public int index;
    public int remander;
    public double opacity;
    public ArrayList<ValueButton> buttons = new ArrayList<>();
    public boolean expand;
    boolean hover;

    public Button(Module cheat, int x, int y) {
        this.cheat = cheat;
        this.x = x;
        this.y = y;
        int y2 = y + 15;
        for (Value v : this.cheat.getValues()) {
            if (!v.isVisitable()) continue;
            buttons.add(new ValueButton(v, this.x + 5, y2));
            y2 += 15;
        }
    }

    int smoothalpha;
    float animationsize;

    public float processFPS(float fps, float defF, float defV) {
        return defV / (fps / defF);
    }

    public void render(int mouseX, int mouseY, Limitation limitation) {
        final TFontRenderer font = Fonts.R16;
        float y2 = y + 15;
        buttons.clear();
        for (Value v : cheat.getValues()) {
            if (ClickGui.Visitable.getValue()) {
                if (!v.isVisitable()) continue;
            }
            buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        if (index != 0) {
            int FPS = Minecraft.getMinecraft().getDebugFPS() == 0 ? 1 : Minecraft.getMinecraft().getDebugFPS();
            Button b2 = parent.buttons.get(index - 1);
            y = b2.y + 15 + animationsize;
            if (b2.expand) {
                parent.buttonanim = true;
                animationsize = AnimationUtil.moveUD(animationsize, 15 * b2.buttons.size(), processFPS(FPS, 1000, 0.013F), processFPS(FPS, 1000, 0.011F));
            } else {
                parent.buttonanim = true;
                animationsize = AnimationUtil.moveUD(animationsize, 0, processFPS(FPS, 1000, 0.013F), processFPS(FPS, 1000, 0.011F));
            }
        }
        if (parent.buttonanim) {
            parent.buttonanim = false;
        }
        int i = 0;
        final float size = buttons.size();
        while (i < size) {
            buttons.get(i).y = y + 17 + 15 * i;
            buttons.get(i).x = x + 5;
            ++i;
        }
        smoothalphas();
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        limitation.cut();
        hover = mouseX > x - 7 && mouseX < x + 85 && mouseY > y - 6 && mouseY < y + font.getHeight() + 4;
        SimpleRender.drawRect(x - 5, y - 5, x + 85 + parent.allX, y + font.getHeight() + 5, new Color(202, 202, 202, 0).getRGB());

        SimpleRender.drawRect(x - 5, y - 5 - 1, x + 85 + parent.allX, y + font.getHeight() + 3 + 1,
                hudcolorwithalpha());

        // SimpleRender.drawRect(x - 5, y - 5 - 1, x + 85 + parent.allX, y + font.getHeight() + 3 + 1, hudcolorwithalpha());
        Fonts.SF17.drawStringWithShadow(cheat.getName(), x - 2, y - 1, -1);//Button Font List
       // Fonts.simp18.drawStringWithMaxShadow(cheat.getName(), x - 1.5f, y - 4.5f, -1);//Button Font List

        ValueButton.valuebackcolor = new Color(130, 100, 180).getRGB();
        if (!expand && size >= 1) {
            Fonts.FLUXICON16.drawString("i", x + 75 + parent.allX, y + 1, -1);
        } else if (size >= 1) {
            Fonts.FLUXICON16.drawString("h", x + 75 + parent.allX, y + 1, -1);
        }
        if (expand) {
            buttons.forEach(b -> b.render(mouseX, mouseY, parent));
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    private int hudcolorwithalpha() {
        return new Color(130, 150, 130, smoothalpha).getRGB();
    }

    private void smoothalphas() {
        final int FPS = Minecraft.getMinecraft().getDebugFPS() == 0 ? 1 : Minecraft.getMinecraft().getDebugFPS();
        if (cheat.isEnabled()) {
            smoothalpha = (int) AnimationUtil.moveUD(smoothalpha, 255, processFPS(FPS, 1000, 0.013F), processFPS(FPS, 1000, 0.011F));
            smoothalpha = 150;

        } else {
            smoothalpha = (int) AnimationUtil.moveUD(smoothalpha, 0, processFPS(FPS, 1000, 0.013F), processFPS(FPS, 1000, 0.011F));
            //smoothalpha = 100;

        }
    }

    public void key(char typedChar, int keyCode) {
        buttons.forEach(b -> b.key(typedChar, keyCode));
    }

    public void click(int mouseX, int mouseY, int button) {
        if (parent.drag) {
            return;
        }
        if (mouseX > x - 7 && mouseX < x + 85 + parent.allX && mouseY > y - 6 && mouseY < y + Fonts.SF17.getHeight()) {
            if (button == 0) {
                cheat.setEnabled(!cheat.isEnabled());
            }
            if (button == 1 && !buttons.isEmpty()) {
                expand = !expand;
            }
        }
        if (expand) {
            buttons.forEach(b -> b.click(mouseX, mouseY, button));
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
        for (int i = 0; i < this.parent.buttons.size(); ++i) {
            if (this.parent.buttons.get(i) != this) continue;
            index = i;
            remander = this.parent.buttons.size() - i;
            break;
        }
    }
}
