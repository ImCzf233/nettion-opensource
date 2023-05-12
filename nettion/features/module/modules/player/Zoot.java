/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.player;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Zoot
extends Module {
    public Zoot() {
        super("Zoot", ModuleType.Player);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        Potion[] arrpotion = Potion.potionTypes;
        int n = arrpotion.length;
        int n2 = 0;
        while (n2 < n) {
            PotionEffect effect;
            Potion potion = arrpotion[n2];
            if (e.getType() == 0 && potion != null && ((effect = this.mc.thePlayer.getActivePotionEffect(potion)) != null && potion.isBadEffect() || this.mc.thePlayer.isBurning() && !this.mc.thePlayer.isInWater() && this.mc.thePlayer.onGround)) {
                int i = 0;
                while (!(this.mc.thePlayer.isBurning() ? i >= 20 : i >= effect.getDuration() / 20)) {
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    ++i;
                }
            }
            ++n2;
        }
    }
}

