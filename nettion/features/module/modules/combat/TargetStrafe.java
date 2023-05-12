package nettion.features.module.modules.combat;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.optifine.Vector3d;
import nettion.event.EventHandler;
import nettion.event.events.world.EventMove;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.ModuleManager;
import nettion.features.module.modules.movement.Flight;
import nettion.features.module.modules.movement.Speed;
import nettion.features.value.values.Numbers;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Option;
import nettion.utils.Rotation;
import nettion.utils.player.PlayerUtils;

public class TargetStrafe extends Module {
    private final Numbers<Double> range = new Numbers<>("Range", 1.0, 0.0, 6.0, 0.1);
    private final Option<Boolean> jumpkey = new Option<>("OnlyJump", false);
    private final Option<Boolean> lockPersonView = new Option<>("LockPersonView", false);
    public static boolean direction = true;

    public TargetStrafe() {
        super("TargetStrafe", ModuleType.Combat);
        addValues(range, jumpkey, lockPersonView);
    }

    private static boolean isBlockUnder(Entity entity) {
        for (int i = (int) (entity.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(entity.posX,
                    i, entity.posZ);
            if (Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return false;
        }
        return true;
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (lockPersonView.getValue() && ModuleManager.getModuleByClass(Killaura.class).isEnabled()) {
            if ((ModuleManager.getModuleByClass(Speed.class).isEnabled() || ModuleManager.getModuleByClass(Flight.class).isEnabled())) {
                if (Killaura.target != null) {
                    mc.gameSettings.thirdPersonView = 1;
                } else {
                    mc.gameSettings.thirdPersonView = 0;
                }
            }
        }
    }

    @EventHandler
    private void onMove(EventMove em) {
        if (PlayerUtils.isMoving() && ModuleManager.getModuleByClass(Killaura.class).isEnabled()) {
            if (Killaura.target != null) {
                if (ModuleManager.getModuleByClass(Speed.class).isEnabled()) {
                    if (jumpkey.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
                        move(em, PlayerUtils.getBaseMoveSpeed(), Killaura.target);
                    } else if (!jumpkey.getValue()) {
                        move(em, PlayerUtils.getBaseMoveSpeed(), Killaura.target);
                    }
                }
            }
        }
    }

    public void move(EventMove event, double speed, Entity entity) {
        if (isBlockUnder(entity)) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
            if (event != null) {
                event.setX(0);
                event.setZ(0);
            }
            return;
        }
        if (isBlockUnder(mc.thePlayer) && !ModuleManager.getModuleByClass(Flight.class).isEnabled())
            direction = !direction;

        if (mc.thePlayer.isCollidedHorizontally)
            direction = !direction;

        float strafe = direction ? 1 : -1;
        float diff = (float) (speed / (range.getValue() * Math.PI * 2)) * 360 * strafe;
        float[] rotation = Rotation.getNeededRotations(new Vector3d(entity.posX, entity.posY, entity.posZ), new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

        rotation[0] += diff;
        float dir = rotation[0] * (float) (Math.PI / 180F);

        double x = entity.posX - Math.sin(dir) * range.getValue();
        double z = entity.posZ + Math.cos(dir) * range.getValue();

        float yaw = Rotation.getNeededRotations(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3d(x, entity.posY, z))[0] * (float) (Math.PI / 180F);

        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
        if (event != null) {
            event.setX(mc.thePlayer.motionX);
            event.setZ(mc.thePlayer.motionZ);
        }
    }
}
