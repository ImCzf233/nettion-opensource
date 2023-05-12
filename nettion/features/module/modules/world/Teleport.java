/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package nettion.features.module.modules.world;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class Teleport extends Module {
    public Teleport() {
        super("Teleport", ModuleType.World);
    }
    @EventHandler
    private void onUpdate(EventPreUpdate event) {
    }
}

