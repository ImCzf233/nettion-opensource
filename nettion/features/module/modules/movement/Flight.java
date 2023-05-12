package nettion.features.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import nettion.event.events.world.*;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.fonts.FontLoaders;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;
import nettion.utils.render.Colors;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.math.BigDecimal;
import java.util.ArrayList;

import nettion.utils.player.PacketUtils;
import nettion.utils.player.PlayerUtils;

public class Flight extends Module {
	public Mode<Enum<?>> mode = new Mode<>("mode", FlyMode.values(), FlyMode.Motion);
	public static Numbers<Double> speed = new Numbers<>("Speed", 0.3, 0.1, 3.0, 0.1);
	private final Option<Boolean> vanc = new Option<>("MotionClip", true);
	private final Option<Boolean> lagbackcheck = new Option<>("LagBackCheck", true);
	public double ms;
	private boolean blockC03 = false;

	public Flight() {
		super("Flight", ModuleType.Movement);
		this.addValues(mode, speed, vanc, this.lagbackcheck);
	}

	@Override
	public void onEnable() {
		if (mode.getValue() == FlyMode.VulcanClip) {
			if(mc.thePlayer.onGround) {
				clip(0f, -0.1f);
				Timer.timerSpeed = 0.1f;
			}
		} else if (mode.getValue() == FlyMode.Zoom) {
			final double x = mc.thePlayer.posX;
			final double y = mc.thePlayer.posY;
			final double z = mc.thePlayer.posZ;

			double fallDistanceReq = 3.1;

			if (mc.thePlayer.isPotionActive(Potion.jump)) {
				final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
				fallDistanceReq += (float) (amplifier + 1);
			}

			final int packetCount = (int) Math.ceil(fallDistanceReq / 3.25); // Don't change this unless you know the change wont break the self damage.
			for (int i = 0; i < packetCount; i++) {
				PacketUtils.sendPacketWithoutEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y + 3.25, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
				PacketUtils.sendPacketWithoutEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));

			}
			PacketUtils.sendPacketWithoutEvent(new C03PacketPlayer(true));
			if (mc.thePlayer.onGround) mc.thePlayer.jump();
		} else if (mode.getValue() == FlyMode.AAC5) {
			blockC03 = true;
		}
	}

	@Override
	public void onDisable() {
		Timer.timerSpeed = 1;
		if (mode.getValue() == FlyMode.AAC5) {
			sendC03();
			blockC03 = false;
		}
	}

	@EventHandler
	private void onLags(EventPacketReceive event) {
		if(event.getPacket() instanceof S08PacketPlayerPosLook) {
			if (this.lagbackcheck.getValue()) {
				this.setEnabled(false);
				NotificationManager.post(NotificationType.WARNING, "LagBackCheck", "(" + this.getName() + ")The current module is down due to lag.", 2);
			}
		}
	}

	@EventHandler
	private void render(EventRender2D e) {
		ScaledResolution sr = new ScaledResolution(mc);
		double xDiff = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * 2;
		double zDiff = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * 2;
		BigDecimal bg = BigDecimal.valueOf(MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff) * 10d);
		int speed = (int) (bg.intValue() * Timer.timerSpeed);
		String str = speed + "block/sec";
		FontLoaders.F18.drawString(str, (sr.getScaledWidth() - FontLoaders.F18.getStringWidth(str)) / 2f, sr.getScaledHeight() / 2f - 20, Colors.WHITE.c);
	}

	@EventHandler
	private void onUpdate(EventPreUpdate e) {
		this.setSuffix(mode.getValue());
		if (mode.getValue() == FlyMode.Motion) {
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
			mc.thePlayer.motionY = 0;
			if (mc.thePlayer.isMoving()) {
				PlayerUtils.setSpeed(speed.getValue());
				if (vanc.getValue()) {
					clip(0.1, 0.0f);
				}
			}
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY += speed.getValue();
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY -= speed.getValue();
			}
		} else if (mode.getValue() == FlyMode.VulcanClip) {
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionY = 0;
			mc.thePlayer.motionZ = 0;
			/* *
			Timer.timerSpeed = 0.14F;
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionY = 0;
			mc.thePlayer.motionZ = 0;
			e.setGround(true);
			if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
				double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
				double x = -Math.sin(yaw) * speed.getValue() * 10;
				double z = Math.cos(yaw) * speed.getValue() * 10;
				mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + 0, mc.thePlayer.posZ + z);
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			}
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY += speed.getValue();
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY -= speed.getValue();
			}
			* */
			if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
				double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
				double x = -Math.sin(yaw) * speed.getValue() * 3;
				double z = Math.cos(yaw) * speed.getValue() * 3;
				mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + 0, mc.thePlayer.posZ + z);
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			}
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY += speed.getValue();
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY -= speed.getValue();
			}
			Timer.timerSpeed = 1f;
		} else if (mode.getValue() == FlyMode.Vulcan) {
			mc.thePlayer.motionY = 0;
			e.setGround(true);
			Minecraft.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ), EnumFacing.UP, new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ));
		} else if (mode.getValue() == FlyMode.Float) {
			e.setGround(true);
		} else if (mode.getValue() == FlyMode.NCPSlime) {
			mc.gameSettings.keyBindJump.setPressed(false);
			PlayerUtils.setMotion(0.3);
			mc.thePlayer.motionY = 0.0;
			mc.thePlayer.motionY = mc.thePlayer.motionY - (mc.thePlayer.ticksExisted % 10 == 0 ? 0.08 : 0.0);
		} else if (mode.getValue() == FlyMode.Zoom) {
			e.setGround(true);
		} else if (mode.getValue() == FlyMode.AAC5) {
			double vanillaSpeed = speed.getValue();
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.motionY = 0;
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
			if (mc.gameSettings.keyBindJump.isKeyDown())
				mc.thePlayer.motionY += vanillaSpeed;
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				mc.thePlayer.motionY -= vanillaSpeed;
			PlayerUtils.strafe(vanillaSpeed);
		} else if (mode.getValue() == FlyMode.Matrix630) {
			double yMotion = 1.0E-12;
			mc.thePlayer.fallDistance = (float) yMotion;
			mc.thePlayer.motionY = 0.4;
			mc.thePlayer.onGround = false;
			double f = Math.toRadians(mc.thePlayer.rotationYaw);
			mc.thePlayer.motionX -= MathHelper.sin((float) f) * 0.35f;
			mc.thePlayer.motionZ += MathHelper.cos((float) f) * 0.35f;
		}
	}

	@EventHandler
	private void onPost(EventPostUpdate event) {
		if (mode.getValue() == FlyMode.Zoom) {
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.motionY = 0.0;
			mc.thePlayer.motionX = 0.0;
			mc.thePlayer.motionZ = 0.0;
			PlayerUtils.strafe(PlayerUtils.getBaseMoveSpeed() * 3);
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
		}
	}

	public void clip(double dist, float y) {
		double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
		double x = -Math.sin(yaw) * dist;
		double z = Math.cos(yaw) * dist;
		mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
	}

	private final ArrayList<C03PacketPlayer> cacheList = new ArrayList<>();

	@EventHandler
	private void onPacket(EventPacketSend event) {
		Packet<?> packet = event.packet;
		if (mode.getValue() == FlyMode.VulcanClip) {
			mc.thePlayer.setPosition(((S08PacketPlayerPosLook) packet).getX(), ((S08PacketPlayerPosLook) packet).getY(), ((S08PacketPlayerPosLook) packet).getZ());
			event.cancel();
			mc.thePlayer.jump();
			clip(0.127318f, 0f);
			clip(3.425559f, 0);
			clip(3.14285f, 0);
			clip(2.88522f, 0);
		} else if (mode.getValue() == FlyMode.AAC5) {
			if(blockC03 && packet instanceof C03PacketPlayer){
				cacheList.add((C03PacketPlayer) packet);
				event.setCancelled(true);
				if(cacheList.size()>7) {
					sendC03();
				}
			}
		}
	}

	@EventHandler
	private void onPacketReceive(EventPacketReceive event) {
	}

	@EventHandler
	private void onMove(EventMove event) {
		if (mode.getValue() == FlyMode.Verus) {
			if (mc.thePlayer.fallDistance >= 1) {
				mc.thePlayer.fallDistance = 0;
				mc.thePlayer.jump();
				event.setY(0.42D);
			}
		} else if (mode.getValue() == FlyMode.Float) {
			mc.thePlayer.motionY = 0;
			event.setY(0);
			PlayerUtils.setMoveSpeed(event, PlayerUtils.getBaseMoveSpeed());
		}
	}

	private void sendC03(){
		blockC03=false;
		for(C03PacketPlayer packet : cacheList){
			mc.getNetHandler().addToSendQueue(packet);
			if(packet.isMoving()){
				mc.getNetHandler().addToSendQueue((new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(),1e+159,packet.getPositionZ(), true)));
				mc.getNetHandler().addToSendQueue((new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(),packet.getPositionY(),packet.getPositionZ(), true)));
			}
		}
		cacheList.clear();
		blockC03=true;
	}

	public enum FlyMode {
		AAC5,
		Zoom,
		Verus,
		Float,
		Motion,
		Vulcan,
		NCPSlime,
		Matrix630,
		VulcanClip,
	}
}