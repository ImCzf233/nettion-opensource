package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.render.EventBloom;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags", ModuleType.Render);
    }

    @EventHandler
    private void bloom(EventBloom event) {

    }

    @EventHandler
    private void onRender(EventRender2D event) {
    }
}
