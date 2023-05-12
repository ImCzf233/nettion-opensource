package nettion.features.module.modules.render;

import com.ibm.icu.math.BigDecimal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Timer;
import nettion.event.EventHandler;
import nettion.event.events.misc.EventChatReceived;
import nettion.event.events.render.EventBloom;
import nettion.event.events.render.EventRender2D;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.combat.Killaura;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.ui.fonts.FontLoaders;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.hudeditor.HUDEditor;
import nettion.ui.hudeditor.WindowAnimation;
import nettion.utils.ServerUtils;
import nettion.utils.render.*;
import nettion.utils.time.TimerUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class WindowHUD extends Module {
    public static Numbers<Double> alpha = new Numbers<>("BackgroundAlpha", 50.0, 1.0, 255.0, 1.0);
    public static Option<Boolean> armorStatus = new Option<>("ArmorStatus", false);
    public static Option<Boolean> inventory = new Option<>("Inventory", true);
    public static Option<Boolean> speedometer = new Option<>("Speedometer", true);
    public static Option<Boolean> serverHUD = new Option<>("ServerHUD", true);
    public static Option<Boolean> sessionInfo = new Option<>("SessionInfo", true);
    public static Option<Boolean> targetHUD = new Option<>("TargetHUD", true);
    int animAlpha = 0;
    boolean startAnim, stopAnim;
    public EntityLivingBase lastEnt;
    static double rect;
    float anim2 = 0f;
    int HM,H,M,S;
    int kills;
    int wins;
    private final ArrayList<Float> speeds = new ArrayList<>();
    private final TimerUtils updateTimer = new TimerUtils();

    public WindowHUD() {
        super("WindowHUD", ModuleType.Render);
        addValues(alpha, armorStatus, inventory, speedometer, serverHUD, sessionInfo, targetHUD);
    }

    @Override
    public void onEnable() {
        HM = 0;
        H = 0;
        M = 0;
        S = 0;
        animAlpha = 0;
        startAnim = false;
        stopAnim = false;
    }

    @EventHandler
    private void bloom(EventBloom event) {
        if (!mc.gameSettings.showDebugInfo) {
            // ArmorStatus
            if (armorStatus.getValue()) {
                float X = HUDEditor.armor.x;
                float Y = HUDEditor.armor.y;
                RoundedUtils.drawRound(X, Y, FontLoaders.F14.getStringWidth("100%") + 23.5f, 63.5f, 3, new Color(0, 0, 0, 255));
                GlStateManager.resetColor();
            }
            // Inventory
            if (inventory.getValue()) {
                RoundedUtils.drawRound(HUDEditor.inv.x, HUDEditor.inv.y, (20 * 9) + 1.5f, (20 * 4) - 5.5F, 5, new Color(0,0,0, 255));
                GlStateManager.resetColor();
            }
            // Speedometer
            if (speedometer.getValue()) {
                float X = HUDEditor.speedometer.x;
                float Y = HUDEditor.speedometer.y;
                RoundedUtils.drawRound(X + 3.0F, Y + 4.0F, 100.0F - 6.0F, 50.0F - 8.0F, 5, new Color(0, 0, 0, 255));
                GlStateManager.resetColor();
            }
            // ServerHUD
            if (serverHUD.getValue()) {
                int X = HUDEditor.serh.x;
                int Y = HUDEditor.serh.y;
                String text;
                if (ServerUtils.getIp() == "Singleplayer") {
                    text = "Singleplayer";
                } else {
                    text = mc.getCurrentServerData().serverIP;
                }
                RoundedUtils.drawRound(X, Y, Fonts.R20.getStringWidth(text) + 38, 30.0F, 5.0F, new Color(0, 0, 0, 255));
            }
            // SessionInfo
            if (sessionInfo.getValue()) {
                float X = HUDEditor.session.x;
                float Y = HUDEditor.session.y;
                RoundedUtils.drawRound(X - 3, Y, -3 + 119.5f, 54, 5, new Color(0, 0, 0, 255));
                GlStateManager.resetColor();
            }
            // TargetHUD
            if (targetHUD.getValue()) {
                if (ModuleManager.getModuleByClass(Killaura.class).isEnabled() || HUDEditor.HudEditor) {
                    if (getTarget() != null) {
                        int x = HUDEditor.targethud.x;
                        int y = HUDEditor.targethud.y;
                        double hea;
                        hea = getTarget().getHealth();
                        double f1 = new BigDecimal(hea).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

                        if (FontLoaders.F22.getStringWidth(getTarget().getName()) > FontLoaders.F14.getStringWidth("Health:" + f1 + "")) rect = FontLoaders.F22.getStringWidth(getTarget().getName());
                        if (FontLoaders.F22.getStringWidth(getTarget().getName()) == FontLoaders.F14.getStringWidth("Health:" + f1 + "")) rect = FontLoaders.F22.getStringWidth(getTarget().getName());
                        if (FontLoaders.F22.getStringWidth(getTarget().getName()) < FontLoaders.F14.getStringWidth("Health:" + f1 + "")) rect = FontLoaders.F14.getStringWidth("Health:" + f1 + "");

                        RoundedUtils.drawRound(x - 3, y - 13, (int) (22 + rect) + 6, - 7 + 28 + 11, 5, new Color(0, 0, 0, 255));
                        GlStateManager.resetColor();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRender2D(EventRender2D event) {
        if (!mc.gameSettings.showDebugInfo) {
            // ArmorStatus
            if (armorStatus.getValue()) {
                float X = HUDEditor.armor.x;
                float Y = HUDEditor.armor.y;
                RoundedUtils.drawRound(X, Y, FontLoaders.F14.getStringWidth("100%") + 23.5f, 63.5f, 3, new Color(0, 0, 0, alpha.getValue().intValue()));
                GlStateManager.resetColor();
                int i = 0;
                for (ItemStack itemStack : mc.thePlayer.inventory.armorInventory) {
                    renderItemStack(i, itemStack);
                    i++;
                }
            }
            // Inventory
            if (inventory.getValue()) {
                float startX = HUDEditor.inv.x;
                float startY = HUDEditor.inv.y + 15;
                int curIndex = 0;
                RoundedUtils.drawRound(HUDEditor.inv.x, HUDEditor.inv.y, (20 * 9) + 1.5f, (20 * 4) - 5.5F, 5, new Color(0,0,0, alpha.getValue().intValue()));
                GlStateManager.resetColor();
                FontLoaders.F18.drawCenteredString("InventoryHUD", HUDEditor.inv.x + (10*9), HUDEditor.inv.y + 5, Colors.WHITE.c);

                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.mainInventory[i];
                    if (slot == null) {
                        startX += 20;
                        curIndex += 1;

                        if (curIndex > 8) {
                            curIndex = 0;
                            startY += 20;
                            startX = HUDEditor.inv.x;
                        }

                        continue;
                    }

                    this.drawItemStack(slot, startX + 1, startY);
                    startX += 20;
                    curIndex += 1;
                    if (curIndex > 8) {
                        curIndex = 0;
                        startY += 20;
                        startX = HUDEditor.inv.x + 2;
                    }
                }
            }
            // Speedometer
            if (speedometer.getValue()) {
                float X = HUDEditor.speedometer.x;
                float Y = HUDEditor.speedometer.y;
                RoundedUtils.drawRound(X + 3.0F, Y + 4.0F, 100.0F - 6.0F, 50.0F - 8.0F, 5, new Color(0, 0, 0, alpha.getValue().intValue()));
                if ((float)this.speeds.size() < 100.0F - 6.0F) {
                    for(int i = 0; (float)i < 100.0F - 6.0F; ++i) {
                        this.speeds.add(0.0F);
                    }
                }

                if ((float)this.speeds.size() > 100.0F - 6.0F) {
                    this.speeds.remove(0);
                }

                float speed = (float)mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ) * Minecraft.getMinecraft().getTimer().ticksPerSecond * Timer.timerSpeed;
                String bps = (new DecimalFormat("#.##")).format(speed);
                if (this.updateTimer.hasTimeElapsed(1, true)) {
                    this.speeds.add(speed);
                }
                StencilUtils.initStencil();
                StencilUtils.bindWriteStencilBuffer();
                RoundedUtils.round(X + 3.0F, Y + 4.0F, 100.0F - 6.0F, 50.0F - 8.0F, 10.0F, Color.WHITE);
                //RenderUtils.drawRect2(X + 3.0F, Y + 4.0F, 100.0F - 6.0F, 50.0F - 8.0F, Color.WHITE.getRGB());
                StencilUtils.bindReadStencilBuffer(1);
                GL11.glPushMatrix();
                GL11.glShadeModel(7425);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.5F);
                GL11.glBegin(2);
                float i = 0.0F;
                GL11.glVertex2f(X - 6.0F, Y + 50.0F + 6.0F);
                for (Iterator<Float> var4 = this.speeds.iterator(); var4.hasNext(); i += 0.5F) {
                    float speedPoint = var4.next();
                    ColorUtils.glColor(Color.WHITE.getRGB());
                    float g = speedPoint > 35.0F ? 2.0F : 1.0F;
                    GL11.glVertex2f(X + 4.0F + i * 2.0F, Y + 50.0F - 7.0F - speedPoint / g);
                }
                GL11.glVertex2f(X + 100.0F + 6.0F, Y + 50.0F + 6.0F);
                GL11.glEnd();
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glShadeModel(7424);
                GL11.glLineWidth(2.0F);
                GL11.glPopMatrix();


                StencilUtils.uninitStencilBuffer();
                FontLoaders.F18.drawString("Speed:", X + 6.0F, Y + 8.0F, Color.WHITE.getRGB(), false);
                FontLoaders.F18.drawString(bps + " b/s", X + 100.0F - 6.0F - (float)FontLoaders.F18.getStringWidth(bps + " b/s"), Y + 8.0F, Color.WHITE.getRGB(), false);
            }
            // ServerHUD
            if (serverHUD.getValue()) {
                int X = HUDEditor.serh.x;
                int Y = HUDEditor.serh.y;
                String text;
                if (ServerUtils.getIp() == "Singleplayer") {
                    text = "Singleplayer";
                } else {
                    text = mc.getCurrentServerData().serverIP;
                }
                if (ServerUtils.getIp() == "Singleplayer") {
                    mc.getTextureManager().bindTexture(new ResourceLocation("nettion/singleplayer.png"));
                } else {
                    mc.getTextureManager().bindTexture(new ResourceLocation("servers/" + ServerUtils.getIp() + "/icon"));
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                GL11.glEnable(3042);
                RoundedUtils.drawRound(X, Y, Fonts.R20.getStringWidth(text) + 38, 30.0F, 5.0F, new Color(0, 0, 0, alpha.getValue().intValue()));
                RoundedUtils.drawRoundTextured(X, Y, 30.0F, 30.0F, 5.0F, 1.0F);
                Fonts.R20.drawString(text, X + 34, Y + 12, Color.WHITE.getRGB());
            }
            // SessionInfo
            if (sessionInfo.getValue()) {
                String time;
                if (Minecraft.getMinecraft().isSingleplayer()) {
                    time = "SinglePlayer";
                } else {
                    time = H + "h " + M + "m " + S + "s";
                }

                float X = HUDEditor.session.x;
                float Y = HUDEditor.session.y;
                RoundedUtils.drawRound(X - 3, Y, -3 + 119.5f, 54, 5, new Color(0, 0, 0, alpha.getValue().intValue()));
                GlStateManager.resetColor();
                FontLoaders.F20.drawString("SessionInfo", X + 32 - 3, Y + 4, 0xffffffff);
                FontLoaders.F18.drawString("PlayTime: " + time, X + 5 - 3, Y + 18, 0xffffffff);
                FontLoaders.F18.drawString("Kills: " + kills, X + 5 - 3, Y + 30, 0xffffffff);
                FontLoaders.F18.drawString("Wins: " + wins, X + 5 - 3, Y + 42, 0xffffffff);
            }
            // TargetHUD
            if (targetHUD.getValue()) {
                if (ModuleManager.getModuleByClass(Killaura.class).isEnabled() || HUDEditor.HudEditor) {
                    int x = HUDEditor.targethud.x;
                    int y = HUDEditor.targethud.y;
                    if (getTarget() != null) {
                        this.Background();
                        this.Name();
                        this.Head(getTarget());
                        EntityLivingBase target1 = getTarget();
                        if (target1 != this.lastEnt && target1 != null) {
                            this.lastEnt = target1;
                        }
                        if (startAnim) {
                            stopAnim = false;
                        }
                        if (animAlpha == 255 && getTarget() == null) {
                            stopAnim = true;
                        }
                        startAnim = getTarget() != null;
                        if (startAnim) {
                            if (animAlpha < 255) {
                                animAlpha += 15;
                            }
                        }
                        if (stopAnim) {
                            if (animAlpha > 0) {
                                animAlpha -= 15;
                            }
                        }
                        if (getTarget() == null && animAlpha < 255) {
                            stopAnim = true;
                        }
                        EntityLivingBase player = null;
                        if (lastEnt != null) {
                            player = lastEnt;
                        }
                        if (player != null && animAlpha >= 135) {
                            double Width = getWidth();
                            if (Width < 50.0) {
                                Width = 50.0;
                            }
                            final double healthLocation;

                            if (getTarget().getHealth() > 20)
                                healthLocation = 16 + rect;
                            else
                                healthLocation = ((16 + rect) / 20) * (int) getTarget().getHealth();

                            anim2 = WindowAnimation.moveUD(anim2, (float) healthLocation, 18f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());
                            final Color c1 = ColorUtils.interpolateColorsBackAndForth(15, 3, new Color(140, 40, 255), new Color(46,234,255), false);
                            final Color c2 = ColorUtils.interpolateColorsBackAndForth(15, 1, new Color(46,234,255), new Color(140, 40, 255), false);
                            //health
                            if (!((x + 7) == (x + 7 + anim2))) {
                                RoundedUtils.drawGradientRoundLR((float) (x), (float) (y + 13), (float) (6 + anim2), 3, 1.5f, c1, c2);
                            }
                            double hea = getTarget().getHealth();
                            double f1 = new BigDecimal(hea).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                            FontLoaders.F14.drawString(String.valueOf(f1), x + 22, y + 3.5f, -1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTick(EventTick e) {
        // SessionInfo
        if (sessionInfo.getValue()) {
            if (!Minecraft.getMinecraft().isSingleplayer()) {
                HM += 1;
                if (HM == 20){
                    S = S + 1;
                    HM = 0;
                }
                if (S == 60){
                    M = M + 1;
                    S = 0;
                }
                if (M == 60){
                    H = H + 1;
                    M = 0;
                }
            }
        }
    }

    @EventHandler
    public void onChat(EventChatReceived event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (!message.contains(":") && (message.contains("by " + mc.thePlayer.getName()) || message.contains("para " + mc.thePlayer.getName()) || message.contains("fue destrozado a manos de " + mc.thePlayer.getName()))) {
            kills += 1;
        }
        if (message.contains("You won!")) {
            wins += 1;
        }
    }

    private void renderItemStack(int i, ItemStack itemStack) {
        float X = HUDEditor.armor.x;
        float Y = HUDEditor.armor.y;

        if (itemStack == null) return;
        GL11.glPushMatrix();
        int yAdd = -16 * i + 48;
        if (itemStack.getItem().isDamageable()) {
            double damage = ((itemStack.getMaxDamage() - itemStack.getItemDamage()) / (double) itemStack.getMaxDamage()) * 100;
            FontLoaders.F14.drawString(String.format("%.2f", damage, 100d), X + 16, Y + yAdd + 7, -1);
        }
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) X, (int) (Y + yAdd));
        GL11.glPopMatrix();
    }

    private void drawItemStack(ItemStack stack, float x, float y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemIntoGUI(stack, (int) x, (int) y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, (int) x, (int) y, null);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.enableAlpha();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private int getWidth() {
        if (!HUDEditor.HudEditor) {
            return 38 + FontLoaders.F18.getStringWidth(Killaura.target.getName());
        } else {
            return 38 + FontLoaders.F18.getStringWidth(mc.thePlayer.getName());
        }
    }

    private EntityLivingBase getTarget() {
        if (!HUDEditor.HudEditor) {
            return Killaura.target;
        } else {
            return mc.thePlayer;
        }
    }

    private void Head(EntityLivingBase target) {
        if (!(getTarget() instanceof EntityPlayer)) {
            return;
        }
        int x = HUDEditor.targethud.x;
        int y = HUDEditor.targethud.y;
        float hurtPercent = target.hurtTime / 10F;
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) getTarget()).getLocationSkin());
        GL11.glColor4f(1F, 1F - hurtPercent, 1F - hurtPercent, 1F);
        Gui.drawScaledCustomSizeModalRect(x, y - 10, 8.0F, 8.0F, 8, 8, 20, 20, 64, 64);
    }

    private void Background() {
        int x = HUDEditor.targethud.x;
        int y = HUDEditor.targethud.y;
        double hea;
        hea = getTarget().getHealth();
        double f1 = new BigDecimal(hea).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

        if (FontLoaders.F22.getStringWidth(getTarget().getName()) > FontLoaders.F14.getStringWidth("Health:" + f1 + "")) rect = FontLoaders.F22.getStringWidth(getTarget().getName());
        if (FontLoaders.F22.getStringWidth(getTarget().getName()) == FontLoaders.F14.getStringWidth("Health:" + f1 + "")) rect = FontLoaders.F22.getStringWidth(getTarget().getName());
        if (FontLoaders.F22.getStringWidth(getTarget().getName()) < FontLoaders.F14.getStringWidth("Health:" + f1 + "")) rect = FontLoaders.F14.getStringWidth("Health:" + f1 + "");
        RoundedUtils.drawRound(x - 3, y - 13, (int) (22 + rect) + 6, - 7 + 28 + 11, 5, new Color(0, 0, 0, alpha.getValue().intValue()));
        GlStateManager.resetColor();
    }

    private void Name() {
        int x = HUDEditor.targethud.x;
        int y = HUDEditor.targethud.y + 35;
        FontLoaders.F22.drawString(getTarget().getName(), x + 20 + 2, y - 44, -1);
    }
}
