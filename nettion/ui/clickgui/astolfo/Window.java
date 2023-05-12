package nettion.ui.clickgui.astolfo;

import com.google.common.collect.Lists;
import nettion.Nettion;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.clickgui.neverlose.ClickUI;
import nettion.ui.fonts.old.Fonts;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;

public class Window {
    public final ModuleType category;
    public final ArrayList<Button> buttons = Lists.newArrayList();
    public boolean drag;
    public boolean extended;
    public int x;
    public int y;
    public int expand;
    public int dragX;
    public int dragY;
    public int scroll;
    public int scrollTo;
    public double angel;
    int staticColor;
    public int totalY;
    int offset;
    final ClickUI.TranslateUtil translate = new ClickUI.TranslateUtil(0F, 0F);
    int allX;

    public Window(ModuleType category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        int y2 = y + 25;
        for (Module c : Nettion.instance.getModuleManager().getModules()) {
            if (c.getType() != category) continue;
            buttons.add(new Button(c, x + 5, y2));
            y2 += 15;
        }
        for (Button b2 : buttons) {
            b2.setParent(this);
        }
    }

    public void render(int mouseX, int mouseY) {
        boolean isOnPanel;
        int current = 0;
        int iY = y + 25;
        totalY = 16;
        for (Button b3 : buttons) {
            b3.y = (int) (iY - translate.getY());
            iY += 15;
            totalY += 15;
            if (b3.expand) {
                for (ValueButton ignored : b3.buttons) {
                    current += 15;
                    totalY += 15;
                }
            }
            current += 15;
        }
        int height = 16 + current;
        if (height > 316) {
            height = 316;
        }
        allX = 10;
        expand = extended ? height : 0;
        angel = extended ? 180 : 0;
        isOnPanel = mouseX > x - 2 && mouseX < x + 92 && mouseY > y - 2 && mouseY < y + expand;
        translate.interpolate(0, offset, 0.15F);
        if (isOnPanel) {
            runWheel(height);
        }
        switch (category.name()) {
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
        ClickUI.RenderUtil.drawRect(x, y, x + 90 + allX, y + 17, new Color(25, 25, 25).getRGB());
        if (expand > 0) {
            ClickUI.RenderUtil.rectangleBordered(x - 0.5, y - 0.5, x + 90.5 + allX, y + 5.5 + expand, 1.0, staticColor, staticColor);
            ClickUI.RenderUtil.rectangleBordered(x, y, x + 90 + allX, y + 5.0 + expand, 1.0, new Color(25, 25, 25).getRGB(), new Color(25, 25, 25).getRGB());
            for (Button b2 : buttons) {
                b2.render(mouseX, mouseY, new Limitation(x, y + 16, x + 90 + allX, y + expand));
            }
        }
        Fonts.R16.drawString(category.name().toLowerCase(), x + 4, y + 6, new Color(233, 233, 233, 233).getRGB());

        if (drag) {
            if (!Mouse.isButtonDown(0)) {
                drag = false;
            }
            x = mouseX - dragX;
            y = mouseY - dragY;
            buttons.get(0).y = (int) (y + 22 - translate.getY());
            for (Button b4 : buttons) {
                b4.x = x + 5;
            }
        }
    }

    protected void runWheel(int height) {
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (totalY - height <= 0) {
                return;
            }
            if (wheel < 0) {
                if (offset < totalY - height) {
                    offset += 30 + Mouse.getDWheel();
                    if (offset < 0) {
                        offset = 0;
                    }
                }
            } else if (wheel > 0) {
                offset -= 30 + Mouse.getDWheel();
                if (offset < 0) {
                    offset = 0;
                }
            }
        }
    }

    public void key(char typedChar, int keyCode) {
        buttons.forEach(b2 -> b2.key(typedChar, keyCode));
    }

    public void mouseScroll(int mouseX, int mouseY, int amount) {
        if (mouseX > x - 2 && mouseX < x + 92 && mouseY > y - 2 && mouseY < y + 17 + expand) {
            scrollTo = (int) ((float) scrollTo - (amount / 120 * 28));
        }
    }

    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > x - 2 && mouseX < x + 92 && mouseY > y - 2 && mouseY < y + 17) {
            if (button == 1) {
                extended = !extended;
            }
            if (button == 0) {
                drag = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
            }
        }
        if (extended) {
            buttons.stream().filter(b2 -> b2.y < y + expand).forEach(b2 -> b2.click(mouseX, mouseY, button));
        }
    }
}

