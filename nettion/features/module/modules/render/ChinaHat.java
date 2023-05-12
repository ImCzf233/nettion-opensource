package nettion.features.module.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.EntityUtils;
import nettion.utils.render.ColorUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ChinaHat extends Module {


    public ChinaHat() {
        super("ChinaHat", ModuleType.Render);
    }

    @EventHandler
    private void onRender3D(EventRender3D event) {
        mc.theWorld.playerEntities.forEach(player -> {
            if (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) {
                return;
            }
            final Color c1 = ColorUtils.interpolateColorsBackAndForth(15, 3, new Color(140, 40, 255), new Color(46,234,255), false);
            final Color c2 = ColorUtils.interpolateColorsBackAndForth(15, 1, new Color(46,234,255), new Color(140, 40, 255), false);
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glEnable(2832);
            GL11.glEnable(3042);
            GL11.glShadeModel(7425);
            GlStateManager.disableCull();
            GL11.glBegin(5);
            Vec3 vec = EntityUtils.getInterpolatedPosition(player).add(new Vec3(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY + (double)player.getEyeHeight() + 0.41 + (player.isSneaking() ? -0.2 : 0.0), -mc.getRenderManager().viewerPosZ));
            for (double i = 0.0; i < Math.PI * 2; i += 0.09817477042468103) {
                ColorUtils.glColor(c1.getRGB());
                GL11.glVertex3d(vec.xCoord + 0.65 * Math.cos(i), vec.yCoord - 0.25, vec.zCoord + 0.65 * Math.sin(i));
                ColorUtils.glColor(c2.getRGB());
                GL11.glVertex3d(vec.xCoord, vec.yCoord, vec.zCoord);
            }
            GL11.glEnd();
            GL11.glShadeModel(7424);
            GL11.glDepthMask(true);
            GL11.glEnable(2848);
            GlStateManager.enableCull();
            GL11.glDisable(3553);
            GL11.glEnable(2832);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
        });
    }
}
