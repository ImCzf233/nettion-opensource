package nettion.features.module.modules.ghost;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;

public class Reach extends Module {
    public static final Numbers<Double> reach = new Numbers<>("Reach", 3.17, -3.0, 6.0, 0.01);
    public Reach() {
        super("Reach", ModuleType.Ghost);
        addValues(reach);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(reach.getValue().floatValue());
    }
}
