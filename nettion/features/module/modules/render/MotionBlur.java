package nettion.features.module.modules.render;

import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import nettion.event.EventHandler;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.render.motionblur.MotionBlurResourceManager;
import nettion.features.value.values.Numbers;

import java.util.Map;

public class MotionBlur extends Module {
    public static Numbers<Double> amount = new Numbers("Amount", 1.0, 0.0, 10.0, 0.1);
    private Map domainResourceManagers;
    float lastValue;

    public MotionBlur() {
        super("MotionBlur", ModuleType.Render);
        addValues(amount);
    }

    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
        super.onDisable();
    }

    @EventHandler
    public void onClientTick(EventTick event) {
        try {
            float curValue = amount.getValue().floatValue();

            if (!mc.entityRenderer.isShaderActive() && mc.theWorld != null) {
                mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
            }

            if (domainResourceManagers == null) {
                domainResourceManagers = ((SimpleReloadableResourceManager) mc.mcResourceManager).getDomainResourceManagers();
            }

            if (!domainResourceManagers.containsKey("motionblur")) {
                domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
            }

            if (curValue != lastValue) {
                //ChatUtils.debug("Motion Blur Updated!");
                domainResourceManagers.remove("motionblur");
                domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
                mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
            }

            lastValue = curValue;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
