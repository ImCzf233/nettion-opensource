/*
 * Decompiled with CFR 0_132.
 */
package nettion.ui.fonts;

import java.awt.*;
import java.io.InputStream;

public abstract class FontLoaders {

    public static CFontRenderer F14 = new CFontRenderer(FontLoaders.getFont(14), true, true);
    public static CFontRenderer F16 = new CFontRenderer(FontLoaders.getFont(16), true, true);
    public static CFontRenderer F18 = new CFontRenderer(FontLoaders.getFont(18), true, true);
    public static CFontRenderer F20 = new CFontRenderer(FontLoaders.getFont(20), true, true);
    public static CFontRenderer F22 = new CFontRenderer(FontLoaders.getFont(22), true, true);

    public static Font getFont(int size) {
        return new Font("default", 0, size);
    }
}

