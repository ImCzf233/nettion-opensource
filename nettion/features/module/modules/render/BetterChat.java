package nettion.features.module.modules.render;

import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Option;

public class BetterChat extends Module {
    public static final Option<Boolean> cn = new Option<>("UniFontRender", false);
    public static final Option<Boolean> bg = new Option<>("Background", false);

    public BetterChat() {
        super("BetterChat", ModuleType.Render);
        addValues(cn, bg);
    }
}
