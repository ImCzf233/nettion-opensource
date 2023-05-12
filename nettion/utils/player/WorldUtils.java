package nettion.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.List;

public class WorldUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static List<EntityPlayer> getLivingPlayers() {
        return Arrays.asList(
                ((List<Entity>) mc.theWorld.loadedEntityList).stream()
                        .filter(entity -> entity instanceof EntityPlayer)
                        .filter(entity -> entity != mc.thePlayer)
                        .map(entity -> (EntityPlayer) entity)
                        .toArray(EntityPlayer[]::new)
        );
    }
}
