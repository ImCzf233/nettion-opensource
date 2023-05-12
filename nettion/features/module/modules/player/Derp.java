package nettion.features.module.modules.player;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.utils.Rotation;

public class Derp extends Module {
    public static float yaw, pitch;
    private static final Rotation rotation = new Rotation(999.0f, 999.0f);
    private final Mode<Enum> mod = new Mode<>("Mode", mode.values(), mode.Normal);
    private final Option<Boolean> headless = new Option<>("Headless", false);
    private final Option<Boolean> random = new Option<>("Random", false);
    private final Numbers<Double> constantSpeed = new Numbers<>("Constant Speed", 10.0, 10.0, 180.0, 0.1);
    private final Option<Boolean> twerk = new Option<>("Twerk", false);

    public Derp() {
        super("Derp", ModuleType.Player);
        addValues(mod, headless, random, constantSpeed, twerk);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        if (mod.getValue() == mode.Normal) {
            this.setSuffix(constantSpeed.getValue());
            if (random.getValue()) {
                pitch = (float) (Math.random() * 180 - 90);
                yaw = (float) Math.random() * 360;
            } else {
                pitch = mc.thePlayer.rotationPitch;
                yaw += constantSpeed.getValue();
            }

            if (headless.getValue())
                pitch = (float) (180 + Math.random() / 100);

            mc.thePlayer.renderYawOffset = yaw;
            mc.thePlayer.rotationYawHead = yaw;
            event.setYaw(yaw);
            event.setPitch(90);
            rotation.setYaw(yaw);
            rotation.setPitch(90);
            mc.thePlayer.rotationPitchHead = 90;

            if (twerk.getValue()) {
                mc.gameSettings.keyBindSneak.setPressed(mc.thePlayer.ticksExisted % 2 == 0);
            }
        } else if (mod.getValue() == mode.Awareline) {
            this.setSuffix(mod.getValue().name());
            if (!mc.thePlayer.isMoving()) {
                mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + 60;
                mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 30;
                mc.thePlayer.rotationPitchHead = 90;
                event.setYaw(180);
                event.setPitch(90);
                rotation.setYaw(180);
                rotation.setPitch(90);
            } else {
                pitch = (float) (Math.random() * 180 - 90);
                yaw = (float) Math.random() * 360;
                mc.thePlayer.renderYawOffset = yaw;
                mc.thePlayer.rotationYawHead = yaw;
                event.setYaw(yaw);
                event.setPitch(90);
                rotation.setYaw(yaw);
                rotation.setPitch(90);
                mc.thePlayer.rotationPitchHead = 90;
                if (twerk.getValue()) {
                    mc.gameSettings.keyBindSneak.setPressed(mc.thePlayer.ticksExisted % 2 == 0);
                }
            }
        }

    }

    enum mode {
        Normal,
        Awareline
    }
}
