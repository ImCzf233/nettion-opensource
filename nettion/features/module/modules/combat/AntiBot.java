package nettion.features.module.modules.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.entity.Entity;
import nettion.features.value.values.Option;

public class AntiBot
extends Module {
    public final static Mode<Enum> mode = new Mode("mode", BotMod.values(), BotMod.Watchdog);
    public final static Option<Boolean> remove = new Option<>("Remove", true);

    public AntiBot() {
        super("AntiBot", ModuleType.Combat);
        addValues(mode, remove);
    }

    enum BotMod {
        Watchdog,
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(mode.getValue().name());
        if (mode.getValue() == BotMod.Watchdog) {
            if (remove.getValue()) {
                for (int i = 0; i < mc.theWorld.getLoadedEntityList().size(); i++) {
                    Entity ent = mc.theWorld.getLoadedEntityList().get(i);
                    if (!(ent instanceof EntityPlayer)) continue;
                    if (ent.getName().contains("\u00A7") || (ent.hasCustomName() && ent.getCustomNameTag().contains(ent.getName())) || (ent.getName() == mc.thePlayer.getName() && ent != mc.thePlayer)) {
                        mc.theWorld.removeEntity(ent);
                    }
                }
            }
        }
    }

    @EventHandler
    public boolean onUpdate(Entity entity) {
        return false;
    }

    public static boolean isServerBot(Entity entity) {
        if (!mc.isSingleplayer()) {
            if (mode.getValue() == BotMod.Watchdog) {
                if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}

