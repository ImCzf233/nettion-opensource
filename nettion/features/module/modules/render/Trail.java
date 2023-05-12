package nettion.features.module.modules.render;

import net.minecraft.util.EnumParticleTypes;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Mode;

public class Trail extends Module {
    public final static Mode<Enum> mod = new Mode<>("Mode", mode.values(), mode.Flame);

    public Trail() {
        super("Trail", ModuleType.Render);
        addValues(mod);
    }
    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        this.setSuffix(mod.getValue().toString());
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        if (mod.getValue() == mode.Flame) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.Cloud) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.CLOUD.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.Fireworksspark) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.Reddust) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.LargeSmoke) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.NormalSmoke) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.Heart) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.HEART.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.HugeExplode) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.NormalExplode) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.LargeExplode) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.Lava) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.LAVA.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
        } else if (mod.getValue() == mode.Special) {
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX +0.5, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX +1, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX +1.5, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX -0.5, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX -1, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX -1.5, mc.thePlayer.posY, mc.thePlayer.posZ,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 0.5,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1.5,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 0.5,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1.5,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), mc.thePlayer.posX-1, mc.thePlayer.posY, mc.thePlayer.posZ +1,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), mc.thePlayer.posX+1, mc.thePlayer.posY, mc.thePlayer.posZ +1,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), mc.thePlayer.posX-1, mc.thePlayer.posY, mc.thePlayer.posZ - 1,0,0,0);
            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), mc.thePlayer.posX+1, mc.thePlayer.posY, mc.thePlayer.posZ - 1,0,0,0);
        }
    }

    enum mode {
        Flame,
        Cloud,
        Fireworksspark,
        Reddust,
        LargeSmoke,
        NormalSmoke,
        HugeExplode,
        LargeExplode,
        NormalExplode,
        Heart,
        Lava,
        Special
    }
}
