package nettion.features.module.modules.ghost;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.value.values.Numbers;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.Nettion;

public class HitBox extends Module {
    private static final Numbers<Number> hitbox = new Numbers<>("Size", 0.25, 0.1, 1.0, 0.01);
    public HitBox() {
        super("HitBox", ModuleType.Ghost);
        addValues(hitbox);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(hitbox.getValue().floatValue());
    }

    public static float getSize() {
        double min = Math.min((Double)hitbox.getValue(), (Double)hitbox.getValue());
        double max = Math.max((Double)hitbox.getValue(), (Double)hitbox.getValue());
        return (float)(Nettion.instance.getModuleManager().getModuleByClass(HitBox.class).isEnabled() ? Math.random() * (max - min) + min : 0.10000000149011612);
    }
}
