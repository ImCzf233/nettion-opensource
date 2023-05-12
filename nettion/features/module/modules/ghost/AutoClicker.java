/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package nettion.features.module.modules.ghost;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.utils.time.TimerUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AutoClicker
        extends Module {
    public final TimerUtils time = new TimerUtils();
    public final Numbers<Double> cpsmin = new Numbers<Double>("MinCPS", 8.0, 2.0, 20.0, 1.0);
    public final Numbers<Double> cpsmax = new Numbers<Double>("MaxCPS", 8.0, 2.0, 20.0, 1.0);
    protected final Random r = new Random();
    protected long lastMS = -1L;
    public final Option<Boolean> BreakBlock = new Option<Boolean>("BreakBlock", true);
    public final Option<Boolean> InvClicker = new Option<Boolean>("Inventory", false);
    public int Click;
    public boolean Clicked;
    private double delay;
    private final TimerUtils time2 = new TimerUtils();
    private final TimerUtils time3 = new TimerUtils();
    private final TimerUtils time4 = new TimerUtils();

    public AutoClicker() {
        super("AutoClicker", ModuleType.Ghost);
        this.addValues(this.cpsmin, this.cpsmax, this.InvClicker, this.BreakBlock);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        boolean isblock;
        BlockPos bp = mc.thePlayer.rayTrace(6.0, 0.0f).getBlockPos();
        boolean bl = isblock = mc.theWorld.getBlockState(bp).getBlock() != Blocks.air && AutoClicker.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY;
        if (!((Boolean)this.BreakBlock.getValue()).booleanValue()) {
            isblock = false;
        }
        if (this.time2.delay((float) this.delay) && !this.time2.delay((float) (this.delay + this.delay / 2.0))) {
            this.Clicked = true;
        }
        if (this.time2.delay((float) (this.delay + this.delay - 1.0))) {
            this.Clicked = false;
            AutoClicker autoClicker = this;
            autoClicker.time2.reset();
        }
    }

    @EventHandler
    private void invClicks(EventPreUpdate event) {
        if (!Keyboard.isKeyDown((int)42)) {
            return;
        }
        if (AutoClicker.mc.currentScreen instanceof GuiContainer && ((Boolean)this.InvClicker.getValue()).booleanValue()) {
            float invClickDelay = 1000.0f / ((Double)this.cpsmax.getValue()).floatValue() + (float)this.r.nextInt(50);
            if (Mouse.isButtonDown((int)0)) {
                if (time4.delay(invClickDelay)) {
                    try {
                        //mc.currentScreen.InventoryClicks();
                        AutoClicker autoClicker = this;
                        autoClicker.time3.reset();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
    }
}

