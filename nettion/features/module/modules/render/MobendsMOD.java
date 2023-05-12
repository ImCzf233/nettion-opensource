package nettion.features.module.modules.render;

import nettion.other.mobends.AnimatedEntity;
import nettion.other.mobends.client.renderer.entity.RenderBendsPlayer;
import nettion.other.mobends.client.renderer.entity.RenderBendsSpider;
import nettion.other.mobends.client.renderer.entity.RenderBendsZombie;
import nettion.other.mobends.data.Data_Player;
import nettion.other.mobends.data.Data_Spider;
import nettion.other.mobends.data.Data_Zombie;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Option;
import org.lwjgl.util.vector.Vector3f;


public class MobendsMOD extends Module {
    private final Option<Boolean> zombieAnimation = new Option<>("Zombie Animation",  true);
    private final Option<Boolean> spiderAnimation = new Option<>("Spider Animation",   true);
    public final Option<Boolean> swordTrail = new Option<>("Sword Trail",   true);
    public final Option<Boolean> spinAttack = new Option<>("Spin attack",  true);
    public MobendsMOD() {
        super("Mobends", ModuleType.Render);
        addValues(zombieAnimation, spiderAnimation, swordTrail, spinAttack);
        AnimatedEntity.register();
    }

    public static float partialTicks = 0.0f;
    public static float ticks = 0.0f;
    public static float ticksPerFrame = 0.0f;

    public static final ResourceLocation texture_NULL = new ResourceLocation("mobends/textures/white.png");

    @EventHandler
    public void onRender3D(EventRender3D partialTicks) {
        if(!this.isEnabled()){
            return;
        }
        if (mc.theWorld == null) {
            return;
        }

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player.dataList.get(i).update(partialTicks.getPartialTicks());
        }

        for (int i = 0; i < Data_Zombie.dataList.size(); i++) {
            Data_Zombie.dataList.get(i).update(partialTicks.getPartialTicks());
        }

        for (int i = 0; i < Data_Spider.dataList.size(); i++) {
            Data_Spider.dataList.get(i).update(partialTicks.getPartialTicks());
        }
        if (mc.thePlayer != null) {
            float newTicks = mc.thePlayer.ticksExisted + partialTicks.getPartialTicks();
            if (!(mc.theWorld.isRemote && mc.isGamePaused())) {
                ticksPerFrame = Math.min(Math.max(0F, newTicks - ticks), 1F);
                ticks = newTicks;
            } else {
                ticksPerFrame = 0F;
            }
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        if(!this.isEnabled()){
            return;
        }
        if (mc.theWorld == null) {
            return;
        }

        for (int i = 0;i < Data_Player.dataList.size();i++) {
            Data_Player data = Data_Player.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x=(float) entity.posX-data.position.x;
                    data.motion.y=(float) entity.posY-data.position.y;
                    data.motion.z=(float) entity.posZ-data.position.z;

                    data.position = new Vector3f((float)entity.posX,(float)entity.posY,(float)entity.posZ);
                }
            } else {
                Data_Player.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

        for (int i = 0;i < Data_Zombie.dataList.size();i++) {
            Data_Zombie data = Data_Zombie.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Zombie.dataList.remove(data);
                    Data_Zombie.add(new Data_Zombie(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {
                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX-data.position.x;
                    data.motion.y = (float) entity.posY-data.position.y;
                    data.motion.z = (float) entity.posZ-data.position.z;

                    data.position = new Vector3f((float)entity.posX,(float)entity.posY,(float)entity.posZ);
                }
            } else {
                Data_Zombie.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

        for (int i = 0;i < Data_Spider.dataList.size();i++) {
            Data_Spider data = Data_Spider.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Spider.dataList.remove(data);
                    Data_Spider.add(new Data_Spider(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX-data.position.x;
                    data.motion.y = (float) entity.posY-data.position.y;
                    data.motion.z = (float) entity.posZ-data.position.z;

                    data.position = new Vector3f((float)entity.posX,(float)entity.posY,(float)entity.posZ);
                }
            } else {
                Data_Spider.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }
    }

    public boolean onRenderLivingEvent(RendererLivingEntity renderer, EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!this.isEnabled() || renderer instanceof RenderBendsPlayer || renderer instanceof RenderBendsZombie || renderer instanceof RenderBendsSpider) {
            return false;
        }

        AnimatedEntity animatedEntity = AnimatedEntity.getByEntity(entity);

        if (animatedEntity != null && (entity instanceof EntityPlayer || (entity instanceof EntityZombie && zombieAnimation.getValue()) || (entity instanceof EntitySpider && spiderAnimation.getValue()))) {
            if (entity instanceof EntityPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                AnimatedEntity.getPlayerRenderer(player).doRender(player, x, y, z, entityYaw, partialTicks);
            } else if (entity instanceof EntityZombie) {
                EntityZombie zombie = (EntityZombie) entity;
                AnimatedEntity.zombieRenderer.doRender(zombie, x, y, z, entityYaw, partialTicks);
            } else {
                EntitySpider spider = (EntitySpider) entity;
                AnimatedEntity.spiderRenderer.doRender(spider, x, y, z, entityYaw, partialTicks);
            }
            return true;
        }
        return false;
    }
}
