package nettion.features.module.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import nettion.event.EventHandler;
import nettion.event.events.render.EventBloom;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Option;

public class Scoreboard extends Module {
    public static final Option<Boolean> ena = new Option<>("Enable", true);
    public static final Option<Boolean> bloom = new Option<>("Bloom", false);
    public static final Option<Boolean> noPoint = new Option<>("NoPoints", true);

    public Scoreboard() {
        super("Scoreboard", ModuleType.Render);
        addValues(ena, bloom, noPoint);
    }

    @EventHandler
    private void onBloom(EventBloom e) {
        if (bloom.getValue()) {
            new GuiIngame(Minecraft.getMinecraft()).bloom();
            GlStateManager.resetColor();
        }
    }
}
