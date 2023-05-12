/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.combat;

import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.event.EventHandler;
import nettion.features.value.values.Numbers;

import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen
extends Module {
    private Numbers<Double> packets = new Numbers<Double>("Packets", 10.0, 10.0, 1000.0, 10.0);

    public Regen() {
        super("Regen", ModuleType.Combat);
        addValues(packets);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(packets.getValue());
        if (mc.thePlayer.getHealth() < 20) {
            this.packetRegen(packets.getValue().intValue());
        }
    }

    private void packetRegen(int packets) {
        for(int i = 0; i < packets; ++i) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
        }
    }
}

