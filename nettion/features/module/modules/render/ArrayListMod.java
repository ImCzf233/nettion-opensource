package nettion.features.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import nettion.event.events.render.EventBloom;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.features.module.ModuleType;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.fonts.old.TFontRenderer;
import nettion.utils.render.ColorUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class ArrayListMod extends Module {
    public static Option<Boolean> enabled = new Option<>("Enabled", true);
    public static Mode<Enum> mode = new Mode<>("Mode", mods.values(), mods.Nettion);
    public static Option<Boolean> fontshadow = new Option<>("FontShadow", true);
    public static Option<Boolean> bold = new Option<>("FontBold", true);
    public static Option<Boolean> background = new Option<>("Background", true);
    public static Numbers<Double> alpha = new Numbers<>("BackgroundAlpha", 100.0, 1.0, 255.0, 1.0);
    public static Option<Boolean> rightline = new Option<>("RightLine", false);
    private static final Mode<Enum> cmode = new Mode<>("Color", colors.values(), colors.Custom);
    private static final Numbers<Double> red = new Numbers<>("Red", 255.0, 0.0, 255.0, 1.0);
    private static final Numbers<Double> green = new Numbers<>("Green", 255.0, 0.0, 255.0, 1.0);
    private static final Numbers<Double> blue = new Numbers<>("Blue", 255.0, 0.0, 255.0, 1.0);
    public static Option<Boolean> hideRend = new Option<>("HideRender", false);
    public static Option<Boolean> hideTag = new Option<>("HideTag", false);
    public static int arrayListYT;
    public static Color color;
    public static Color color2;
    public ArrayListMod() {
        super("ArrayList", ModuleType.Render);
        addValues(enabled, mode, bold, fontshadow, background, alpha, rightline, cmode, red, green, blue, hideRend, hideTag);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
    @EventHandler
    public void bloom(EventBloom e){
        if (!enabled.getValue()) {
            return;
        }
        if (background.getValue()) {
            TFontRenderer font;
            if (bold.getValue()) {
                font = Fonts.R16;
            } else {
                font = Fonts.SF16;
            }
            ScaledResolution sr = new ScaledResolution(mc);
            ArrayList<Module> mods = new ArrayList<>();
            float arrayListY = 4;
            for (Module m : ModuleManager.getModules()) {
                if (hideRend.getValue()) {
                    if (m.getType() == ModuleType.Render) {
                        continue;
                    }
                }
                if (!m.isEnabled())
                    continue;
                mods.add(m);
            }
            mods.sort(((o1, o2) -> font.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - font.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix()))));
            for (Module mod : mods) {
                Gui.drawRect3(sr.getScaledWidth() - font.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 8.5, arrayListY , sr.getScaledWidth() - 4, arrayListY + 12, ColorUtils.applyOpacity(new Color(0, 0, 0).getRGB(),1));
                arrayListY += 12;
            }
        }
    }

    @EventHandler
    private void renderHud(EventRender2D event) {
        if (!enabled.getValue()) {
            return;
        }
        if (mode.getValue() == mods.Nettion) {
            ScaledResolution sr = new ScaledResolution(mc);
            TFontRenderer font;
            if (bold.getValue()) {
                font = Fonts.R16;
            } else {
                font = Fonts.SF16;
            }
            ArrayList<Module> mods = new ArrayList<>();
            int count = 0;
            if (!mc.gameSettings.showDebugInfo) {
                float arrayListY = 4;
                int rainbowTick = 0;
                for (Module m : ModuleManager.getModules()) {
                    if (hideRend.getValue()) {
                        if (m.getType() == ModuleType.Render) {
                            continue;
                        }
                    }
                    if (!m.isEnabled())
                        continue;
                    mods.add(m);
                }
                mods.sort(((o1, o2) -> font.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - font.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix()))));
                for (Module mod : mods) {
                    Color textcolor;
                    textcolor = ColorUtils.interpolateColorsBackAndForth(10, count * 30, new Color(140, 40, 255), new Color(46,234,255), false);

                    color2 = ColorUtils.applyOpacityColor(textcolor.getRGB(), 1);

                    color = new Color(Color.HSBtoRGB((float)((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                    GL11.glTranslated(0, -1, 0.0D);
                    GL11.glPushMatrix();
                    if (background.getValue()) {
                        Gui.drawRect3(sr.getScaledWidth() - font.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 8.5, arrayListY + 1, sr.getScaledWidth() - 4, arrayListY + 13, new Color(0,0,0,alpha.getValue().intValue()).getRGB());
                    }
                    if (rightline.getValue()) {
                        Gui.drawRect(sr.getScaledWidth() - 4, arrayListY + 1, sr.getScaledWidth() - 3, arrayListY + 13, getColor().getRGB());
                    }
                    if (!fontshadow.getValue()) {
                        font.drawString(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix(), sr.getScaledWidth() - font.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 7.2F, arrayListY + 5, getColor().getRGB());
                    } else {
                        font.drawStringWithShadow(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix(), sr.getScaledWidth() - font.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 7.2F, arrayListY + 5, getColor().getRGB());
                    }
                    GL11.glPopMatrix();
                    GL11.glTranslated(0, 1, 0.0D);
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    arrayListY += 12;
                    count++;
                }
                arrayListYT = (int) arrayListY;
            }
        }
    }

    public static Color getColor() {
        if (cmode.getValue() == colors.Nettion) {
            return ArrayListMod.color2;
        } else if (cmode.getValue() == colors.Rainbow) {
            return ArrayListMod.color;
        } else if (cmode.getValue() == colors.Custom) {
            return new Color(red.getValue().intValue(), green.getValue().intValue(), blue.getValue().intValue());
        }
        return null;
    }

    enum colors {
        Nettion,
        Rainbow,
        Custom,
    }

    enum mods {
        Nettion,
    }
}
