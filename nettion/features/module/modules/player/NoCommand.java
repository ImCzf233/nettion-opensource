package nettion.features.module.modules.player;

import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class NoCommand extends Module {
    public NoCommand() {
        super("NoCommand", ModuleType.Player);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
    @Override
    public void onDisable() {
        super.onDisable();
    }
}
