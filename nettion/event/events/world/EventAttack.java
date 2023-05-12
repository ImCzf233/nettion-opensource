package nettion.event.events.world;

import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;
import nettion.event.Event;

@AllArgsConstructor
public class EventAttack extends Event {

    private final EntityLivingBase targetEntity;

    public EntityLivingBase getTargetEntity() {
        return targetEntity;
    }

}
