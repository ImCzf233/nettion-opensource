package nettion.features.module.modules.movement;

import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.PlayerUtils;

public class Strafe extends Module {
    private Mode mode = new Mode("Mode", mods.values(), mods.Normal);

    public Strafe() {
        super("Strafe", ModuleType.Movement);
        addValues(mode);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (mode.getValue() == mods.Normal) {
            if (!mc.thePlayer.onGround) {
                PlayerUtils.strafe(mc.thePlayer.getSpeed());
            }
        }
    }

    enum mods {
        Normal,
    }
}
