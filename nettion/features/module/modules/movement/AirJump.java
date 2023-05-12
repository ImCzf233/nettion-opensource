package nettion.features.module.modules.movement;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class AirJump
extends Module {
    public AirJump() {
        super("AirJump", ModuleType.Movement);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        mc.thePlayer.onGround = true;
        mc.thePlayer.isAirBorne = false;
        mc.thePlayer.fallDistance = 0;
    }
}

