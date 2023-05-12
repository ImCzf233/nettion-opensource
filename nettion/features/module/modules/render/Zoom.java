package nettion.features.module.modules.render;

import net.minecraft.client.renderer.EntityRenderer;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.render.RenderUtils;

public class Zoom extends Module {
    public Zoom() {
        super("Zoom", ModuleType.Render);
    }

    @EventHandler
    private void onRender2D(EventRender2D event) {
        if (EntityRenderer.ftemp > EntityRenderer.fto) {
            EntityRenderer.ftemp = RenderUtils.toanim(EntityRenderer.ftemp, EntityRenderer.fto, 4, 0.01f);
        } else {
            EntityRenderer.ftemp = RenderUtils.toanim(EntityRenderer.ftemp, EntityRenderer.fto, 8, 0.01f);
        }
    }
}
