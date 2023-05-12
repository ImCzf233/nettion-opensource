package nettion.features.module.modules.render;

import net.minecraft.util.ResourceLocation;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Mode;

public class Cape extends Module {
    public final Mode<Enum> mode = new Mode<>("Mode", mods.values(), mods.White);
    public ResourceLocation oldLocation;
    public Cape() {
        super("Cape", ModuleType.Render);
        addValues(mode);
    }

    @Override
    public void onEnable() {
        this.oldLocation = mc.thePlayer.getLocationCape();
        super.onEnable();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (mode.getValue() == mods.White) {
            mc.thePlayer.setLocationOfCape(new ResourceLocation("nettion/cape.png"));
        } else if (mode.getValue() == mods.Black) {
            mc.thePlayer.setLocationOfCape(new ResourceLocation("nettion/cape_black.png"));
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setLocationOfCape(this.oldLocation);
        super.onDisable();
    }

    enum mods {
        White,
        Black
    }
}
