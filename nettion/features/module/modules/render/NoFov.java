package nettion.features.module.modules.render;

import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;

public class NoFov extends Module {
    public static final Numbers<Double> fov = new Numbers<>("Fov", 1.0, 0.1, 1.5, 0.01);
    public NoFov() {
        super("NoFov", ModuleType.Render);
        addValues(fov);
    }
}
