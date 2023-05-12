package nettion.event.events.world;

import net.minecraft.entity.EntityLivingBase;
import nettion.event.Event;

public class EventLivingUpdate extends Event {
	
	private EntityLivingBase entity;
	
	public EventLivingUpdate(EntityLivingBase entity) {
		this.entity = entity;
	}

	public EntityLivingBase getEntity() {
		return entity;
	}
}