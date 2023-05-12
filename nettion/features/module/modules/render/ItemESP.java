package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.render.WorldRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ItemESP extends Module {
    public ItemESP() {
        super("ItemESP", ModuleType.Render);
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        for (Entity entity : WorldRender.getEntities()) {
            if (!(entity instanceof EntityItem))
                continue;

            double posX = (entity.lastTickPosX - 0.2) + ((entity.posX - 0.2) - (entity.lastTickPosX - 0.2)) * (double) event.getPartialTicks() - mc.getRenderManager().getRenderPosX();
            double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) event.getPartialTicks() - mc.getRenderManager().getRenderPosY();
            double posZ = (entity.lastTickPosZ - 0.2) + ((entity.posZ - 0.2) - (entity.lastTickPosZ - 0.2)) * (double) event.getPartialTicks() - mc.getRenderManager().getRenderPosZ();

            GL11.glPushMatrix();

            GL11.glColor4d(1.0, 1.0, 1.0, 0.0);

            WorldRender.drawBoundingBox(new AxisAlignedBB(posX, posY, posZ, posX + 0.4, posY + 0.4, posZ + 0.4));
            WorldRender.renderOne();
            WorldRender.drawBoundingBox(new AxisAlignedBB(posX, posY, posZ, posX + 0.4, posY + 0.4, posZ + 0.4));
            WorldRender.renderTwo();
            WorldRender.drawBoundingBox(new AxisAlignedBB(posX, posY, posZ, posX + 0.4, posY + 0.4, posZ + 0.4));
            WorldRender.renderThree();
            WorldRender.renderFour(new Color(0xFFFFFF).getRGB());
            WorldRender.drawBoundingBox(new AxisAlignedBB(posX, posY, posZ, posX + 0.4, posY + 0.4, posZ + 0.4));
            WorldRender.renderFive();

            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

            GL11.glPopMatrix();
        }
    }
}
