package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.utils.render.ColorUtils;

public class EnchantEffect extends Module {
    public final Option<Boolean> rw = new Option<>("Rainbow", false);
    public static Numbers<Double> r = new Numbers<Double>("Red", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> g = new Numbers<Double>("Green", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> b = new Numbers<Double>("Blue", 255.0, 0.0, 255.0, 1.0);

    public EnchantEffect() {
        super("EnchantEffect", ModuleType.Render);

        this.addValues(rw, r,g,b);
    }
    @EventHandler
    private void onUpdate(EventPreUpdate e)  {
        if (rw.getValue()) {
            r.setValue((double) ColorUtils.getRainbow().getRed());
            g.setValue((double) ColorUtils.getRainbow().getGreen());
            b.setValue((double) ColorUtils.getRainbow().getBlue());
        }
        //this.setEnabled(false);
    }
}
