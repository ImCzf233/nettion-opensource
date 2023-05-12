package nettion.ui.fonts.old;

import java.awt.Font;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class Fonts {
    public static TFontRenderer R15 = new TFontRenderer(Fonts.getRoboto(15), true, true);
    public static TFontRenderer R16 = new TFontRenderer(Fonts.getRoboto(16), true, true);
    public static TFontRenderer R17 = new TFontRenderer(Fonts.getRoboto(17), true, true);
    public static TFontRenderer R18 = new TFontRenderer(Fonts.getRoboto(18), true, true);
    public static TFontRenderer R19 = new TFontRenderer(Fonts.getRoboto(19), true, true);
    public static TFontRenderer R20 = new TFontRenderer(Fonts.getRoboto(20), true, true);
    public static TFontRenderer R21 = new TFontRenderer(Fonts.getRoboto(21), true, true);
    public static TFontRenderer R22 = new TFontRenderer(Fonts.getRoboto(22), true, true);
    public static TFontRenderer R30 = new TFontRenderer(Fonts.getRoboto(30), true, true);
    public static TFontRenderer SF16 = new TFontRenderer(Fonts.getSF(16), true, true);
    public static TFontRenderer SF17 = new TFontRenderer(Fonts.getSF(17), true, true);
    public static CNFontRenderer PF14 = getFont(14, "regular.ttf", true);
    public static CNFontRenderer PF16 = getFont(16, "regular.ttf", true);
    public static CNFontRenderer PF18 = getFont(18, "regular.ttf", true);
    public static CNFontRenderer PF19 = getFont(19, "regular.ttf", true);
    public static CNFontRenderer PF20 = getFont(20, "regular.ttf", true);

    public static TFontRenderer ICON40 = new TFontRenderer(Fonts.getIcon(40), true, true);
    public static TFontRenderer FLUXICON16 = new TFontRenderer(Fonts.getIcon(16), true, true);
    public static TFontRenderer novoicons18 = new TFontRenderer(Fonts.getIcon(18), true, true);
    public static TFontRenderer novoicons25 = new TFontRenderer(Fonts.getIcon(25), true, true);

    public static CNFontRenderer icon24 = getFont(24, "GuiICONS2.ttf", true);
    public static CNFontRenderer icon26 = getFont(26, "GuiICONS2.ttf", true);
    public static CNFontRenderer guiicons22 = getFont(22, "GuiICONS.ttf", true);
    public static CNFontRenderer guiicons28 = getFont(28, "GuiICONS.ttf", true);
    public static CNFontRenderer guiicons30 = getFont(30, "GuiICONS.ttf", true);

    public static CNFontRenderer getFont(int size, String fontname, Boolean antiAlias) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("nettion/font/" + fontname)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Nettion", 0, size);
        }
        return new CNFontRenderer(font, size, antiAlias);
    }

    private static Font getRoboto(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("nettion/font/Roboto.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getIcon(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("nettion/font/icon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getSF(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("nettion/font/Flux.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
}

