package nettion.features.module.modules.world;

import net.minecraft.util.Timer;
import nettion.features.value.values.Numbers;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class GameSpeed extends Module {
    private final Numbers<Double> speed = new Numbers<Double>("Speed", 1.0, 0.1, 5.0, 0.1);

    public GameSpeed() {
        super("GameSpeed", ModuleType.World);
        addValues(speed);
    }

    @Override
    public void onEnable() {
        Timer.timerSpeed = 1.0f;
    }

    @Override
    public void onDisable() {
        Timer.timerSpeed = 1.0f;
    }

    @EventHandler
    public void onEvent(EventTick e) {
        Timer.timerSpeed = speed.getValue().floatValue();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        this.setSuffix(speed.getValue());
    }
}
