/*
 * Decompiled with CFR 0_132.
 */
package nettion.event.events.world;

import nettion.event.Event;
import net.minecraft.network.Packet;

public class EventPacketReceive
extends Event {
    private Packet packet;

    public EventPacketReceive(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

