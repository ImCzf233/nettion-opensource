package nettion.features.module.modules.movement;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", ModuleType.Movement);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.gameSettings.keyBindForward.getKeyCode() < 0) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Mouse.isButtonDown(mc.gameSettings.keyBindForward.getKeyCode()+100));
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
        }
    }

    @EventHandler
    public void onPlayerPreUpdate(EventPreUpdate e) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    }
}
