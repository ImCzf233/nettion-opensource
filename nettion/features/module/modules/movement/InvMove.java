package nettion.features.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.world.ChestStealer;
import nettion.features.value.values.Option;
import org.lwjgl.input.Keyboard;

public class InvMove extends Module {
	public final Option<Boolean> bypass = new Option<>("Bypass", false);

	public InvMove() {
		super("InvMove", ModuleType.Movement);
		addValues(bypass);
	}

	private final KeyBinding[] AFFECTED_BINDINGS = new KeyBinding[] {
			mc.gameSettings.keyBindForward,
			mc.gameSettings.keyBindBack,
			mc.gameSettings.keyBindRight,
			mc.gameSettings.keyBindLeft,
			mc.gameSettings.keyBindJump
	};

	@EventHandler
	public void onRender(EventRender2D e){
		if(!this.isEnabled()){
			return;
		}
		if (mc.currentScreen instanceof GuiChat) {
			return;
		}
		for (final KeyBinding bind : AFFECTED_BINDINGS) {
			bind.setPressed(GameSettings.isKeyDown(bind));
		}
	}

	@EventHandler
	public void onUpdate(EventPreUpdate event) {
		if (bypass.getValue()) {
			if (!ModuleManager.getModuleByClass(ChestStealer.class).isEnabled() || !(mc.currentScreen instanceof GuiChest)) {
				if (mc.currentScreen != null) {
					EntityPlayerSP var10000;
					if (Keyboard.isKeyDown(205) && !(mc.currentScreen instanceof GuiChat)) {
						var10000 = mc.thePlayer;
						var10000.rotationYaw += 8.0F;
					}

					if (Keyboard.isKeyDown(203) && !(mc.currentScreen instanceof GuiChat)) {
						var10000 = mc.thePlayer;
						var10000.rotationYaw -= 8.0F;
					}

					if (Keyboard.isKeyDown(200) && !(mc.currentScreen instanceof GuiChat)) {
						var10000 = mc.thePlayer;
						var10000.rotationPitch -= 8.0F;
					}

					if (Keyboard.isKeyDown(208) && !(mc.currentScreen instanceof GuiChat)) {
						var10000 = mc.thePlayer;
						var10000.rotationPitch += 8.0F;
					}

					KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};
					KeyBinding[] var3;
					int var4;
					int var5;
					KeyBinding bind;
					if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
						var3 = moveKeys;
						var4 = moveKeys.length;

						for(var5 = 0; var5 < var4; ++var5) {
							bind = var3[var5];
							bind.pressed = Keyboard.isKeyDown(bind.getKeyCode());
						}
					} else {
						var3 = moveKeys;
						var4 = moveKeys.length;

						for(var5 = 0; var5 < var4; ++var5) {
							bind = var3[var5];
							if (!Keyboard.isKeyDown(bind.getKeyCode())) {
								KeyBinding.setKeyBindState(bind.getKeyCode(), false);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPacketSend(EventPacketSend event) {
	}
}
