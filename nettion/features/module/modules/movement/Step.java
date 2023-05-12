/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.movement;

import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.PlayerUtils;

public class Step
extends Module {
    private Mode<Enum> mode = new Mode("mode", StepMode.values(), StepMode.Legit);
    public Step() {
        super("Step", ModuleType.Movement);
        addValues(mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onPreUpdate(EventPreUpdate e) {
        if (!PlayerUtils.isInWater()) {
            if (mode.getValue() == StepMode.Legit) {
                if(mc.thePlayer.onGround) {
                    if(mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.jump();
                    }
                }
            } else if (mode.getValue() == StepMode.AAC) {
                if (mc.thePlayer.isCollidedHorizontally) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.4322;
                    } else {
                        mc.thePlayer.motionY += 0.0122;
                    }
                }
            }
        }

    }

    enum StepMode {
        Legit,
        AAC,
    }
}

