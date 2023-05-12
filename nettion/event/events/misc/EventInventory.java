/*
 * Decompiled with CFR 0_132.
 */
package nettion.event.events.misc;

import nettion.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventInventory
extends Event {
    private final EntityPlayer player;

    public EventInventory(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}

