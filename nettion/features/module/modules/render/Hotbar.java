package nettion.features.module.modules.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import nettion.event.EventHandler;
import nettion.event.events.render.EventBloom;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;

import java.awt.*;

public class Hotbar extends Module {
    public static final Option<Boolean> animation = new Option<>("Animation", true);
    public static final Numbers<Double> speed = new Numbers<>("Speed", 17.0, 2.0, 25.0, 1.0);
    public static final Option<Boolean> bloom = new Option<>("Bloom", false);

    public Hotbar() {
        super("Hotbar", ModuleType.Render);
        addValues(animation, speed, bloom);
    }

    @EventHandler
    public void onBloom(EventBloom event) {
        ScaledResolution sr = new ScaledResolution(mc);
        int i = sr.getScaledWidth() / 2;
        if (bloom.getValue()) {
            Gui.drawRect3(i - 91, sr.getScaledHeight() - 22, i + 91, sr.getScaledHeight() + 22, new Color(20, 20, 20, 255).getRGB());
        }
    }
}
