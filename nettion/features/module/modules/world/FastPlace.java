/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.world;

import nettion.event.EventHandler;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class FastPlace
extends Module {
    public FastPlace() {
        super("FastPlace", ModuleType.World);
    }

    @EventHandler
    private void onTick(EventTick e) {
        this.mc.rightClickDelayTimer = 0;
    }
}

