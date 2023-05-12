package nettion.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import nettion.event.events.world.EventPostUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.MathHelper;
import nettion.event.events.world.EventPreUpdate;
import net.optifine.Vector3d;

public class Rotation {
    private float yaw;
    private static float pitch;
    public static Minecraft mc = Minecraft.getMinecraft();

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        Rotation.pitch = pitch;
    }

    public void add(float yaw, float pitch) {
        this.yaw += yaw;
        Rotation.pitch += pitch;
    }

    public void remove(float yaw, float pitch) {
        this.yaw -= yaw;
        Rotation.pitch -= pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        Rotation.pitch = pitch;
    }

    public static void setRotations(EventPreUpdate e, float yaw, float pitch) {
        e.setYaw(yaw);
        e.setPitch(pitch);
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.rotationPitchHead = pitch;
        mc.thePlayer.renderYawOffset = yaw;
    }

    public static void setRotations(EventPostUpdate e, float yaw, float pitch) {
        e.setYaw(yaw);
        e.setPitch(pitch);
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.rotationPitchHead = pitch;
        mc.thePlayer.renderYawOffset = yaw;
    }

    public static float[] getAngles(Entity e) {
        return new float[]{
                getYawChangeToEntity(e) + mc.thePlayer.rotationYaw,
                getPitchChangeToEntity(e) + mc.thePlayer.rotationPitch
        };
    }

    public static float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity = deltaZ < 0.0 && deltaX < 0.0 ? 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)) : (deltaZ < 0.0 && deltaX > 0.0 ? -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)) : Math.toDegrees(-Math.atan(deltaX / deltaZ)));
        return Double.isNaN((double)mc.thePlayer.rotationYaw - yawToEntity) ? 0.0f : MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float)yawToEntity));
    }

    public static synchronized void faceEntity(EntityLivingBase entity) {
        float[] rotations = getRotationsNeeded(entity);
        if (rotations != null) {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1] + 8.0f;
        }
    }

    public static float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = entity.posY - 1.6 + (double)entity.getEyeHeight() - mc.thePlayer.posY;
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / (double)MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ)));
        return Double.isNaN((double)mc.thePlayer.rotationPitch - pitchToEntity) ? 0.0f : -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float)pitchToEntity);
    }

    public static float[] getNeededRotations(final Vector3d current, final Vector3d target) {
        final double diffX = target.x - current.x;
        final double diffY = target.y - current.y;
        final double diffZ = target.z - current.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static float[] getRotationsNeededBlock(double x, double y, double z) {
        double diffX = x + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
        double diffZ = z + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
        double diffY = y + 0.5 - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    }

    public static float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        double xSize = entity.posX - mc.thePlayer.posX;
        double ySize = entity.posY + (double)(entity.getEyeHeight() / 2.0f) - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double zSize = entity.posZ - mc.thePlayer.posZ;
        double theta = MathHelper.sqrt_double(xSize * xSize + zSize * zSize);
        float yaw = (float)(Math.atan2(zSize, xSize) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(ySize, theta) * 180.0 / Math.PI));
        return new float[]{(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw)) % 360.0f, (mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)) % 360.0f};
    }

    public static float[] getFacingRotations2(int paramInt1, double d, int paramInt3) {
        EntitySnowball localEntityPig = new EntitySnowball(Minecraft.getMinecraft().theWorld);
        localEntityPig.posX = (double)paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = (double)paramInt3 + 0.5;
        return Rotation.getRotationsNeeded(localEntityPig);
    }

    //Skidded from Minecraft
    public static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - mc.thePlayer.posX;
        final double diffZ = entity.posZ - mc.thePlayer.posZ;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase elb = (EntityLivingBase) entity;
            diffY = elb.posY + (elb.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static boolean isVisibleFOV(final Entity e, final float fov) {
        return ((Math.abs(Rotation.getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(Rotation.getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f) : (Math.abs(Rotation.getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f)) <= fov;
    }

    public static Rotation getRotationsEntity(EntityLivingBase entity) {
        return Rotation.getRotations(entity.posX, entity.posY + entity.getEyeHeight() - 0.4, entity.posZ);
    }

    public static Rotation getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = Rotation.mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new Rotation(yaw, pitch);
    }

    /**
     * Calculate difference between two rotations
     *
     * @param a rotation
     * @param b rotation
     * @return difference between rotation
     */
    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    /**
     * Calculate difference between two angle points
     *
     * @param a angle point
     * @param b angle point
     * @return difference between angle points
     */
    public static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

}

