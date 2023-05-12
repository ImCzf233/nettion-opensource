package nettion.features.module.modules.render;

import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class HUDEditor extends Module {
    public HUDEditor() {
        super("HUDEditor", ModuleType.Render);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new nettion.ui.hudeditor.HUDEditor());
        this.setEnabled(false);
    }
}
