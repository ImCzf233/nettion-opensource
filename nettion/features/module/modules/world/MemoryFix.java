package nettion.features.module.modules.world;

import nettion.event.EventHandler;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.time.TimerUtils;

public class MemoryFix extends Module {
    public static final Mode<Enum> mode = new Mode("ClearMode", modes.values(), modes.Simple);
    public static final Option<Boolean> disableMinecraftGC = new Option<Boolean>("DisableMinecraftGC", true);
    public static final Option<Boolean> onlyUseSystemGC = new Option<Boolean>("OnlyUseSystemGC", true);
    private final Numbers<Double> delay = new Numbers<>("AdvancedDelay", 120.0, 10.0, 600.0, 5.0);
    private final Numbers<Double> limit = new Numbers<>("AdvancedLimit", 80.0, 20.0, 95.0, 1.0);
    private final TimerUtils timer = new TimerUtils();

    public MemoryFix() {
        super("MemoryFix", ModuleType.World);
        this.addValues(mode, this.delay, this.limit, disableMinecraftGC, onlyUseSystemGC);
        this.setEnabled(true);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (mode.getValue() == modes.Advanced && !onlyUseSystemGC.getValue()) {
            long maxmem = Runtime.getRuntime().maxMemory();
            long totalmem = Runtime.getRuntime().totalMemory();
            long freemem = Runtime.getRuntime().freeMemory();
            long usemem = totalmem - freemem;
            float pct = usemem * 100L / maxmem;
            if (this.timer.hasReached((Double)this.delay.getValue() * 1000.0) && this.limit.getValue() <= (double)pct) {
                Runtime.getRuntime().gc();
                this.timer.reset();
            }
        }
    }

    public static enum modes {
        Simple,
        Advanced
    }
}
