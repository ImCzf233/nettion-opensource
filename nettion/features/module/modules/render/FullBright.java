package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import nettion.features.value.values.Mode;

public class FullBright extends Module {
    public final Mode<Enum> mode = new Mode<>("Mode", mod.values(), mod.GameSetting);
    private float old;
    public FullBright() {
        super("FullBright", ModuleType.Render);
        addValues(mode);
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == mod.GameSetting) {
            this.old = mc.gameSettings.gammaSetting;
        }
        super.onEnable();
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        if (mode.getValue() == mod.GameSetting) {
            mc.gameSettings.gammaSetting = 1.5999999E7f;
            mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
        } else if (mode.getValue() == mod.Potion) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), Integer.MAX_VALUE));
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == mod.Potion) {
            mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
        } else if (mode.getValue() == mod.GameSetting) {
            mc.gameSettings.gammaSetting = this.old;
        }
        super.onDisable();
    }

    enum mod {
        GameSetting,
        Potion,
    }
}

