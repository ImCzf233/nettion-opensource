package nettion.features.module.modules.render;

import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;

public class Animations extends Module {
	public static Numbers<Double> speed = new Numbers<>("SwingSpeed", 1.0, 0.1, 1.5, 0.1);
    public static Mode<Enum> mode = new Mode("mode", renderMode.values(), renderMode.External);
	public static Option<Boolean> smooth = new Option<>("Smooth", false);
	public Animations() {
		super("Animations", ModuleType.Render);
		this.addValues(speed, mode, smooth);
	}
	
	@EventHandler
	private void onUpdate(EventPreUpdate event) {
		this.setSuffix(mode.getValue().name());
	}
	
	public enum renderMode {
		Old,
		ETB,
		Jello,
		Autumn,
		Jigsaw,
		External,
		Exhibition,
		LiquidBounce,
		Crazy,
	}
}
