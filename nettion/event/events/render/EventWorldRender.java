package nettion.event.events.render;

import nettion.event.Event;

public class EventWorldRender extends Event {
	private float partialTicks;
	public EventWorldRender(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public float getPartialTicks() {
		return partialTicks;
	}
}
