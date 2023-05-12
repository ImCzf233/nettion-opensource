/*
 * Decompiled with CFR 0_132.
 */
package nettion.event.events.world;

import nettion.event.Event;
import net.minecraft.network.Packet;

public class EventPacketSend
extends Event {
    public static Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

