package nettion.features.module.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import nettion.Nettion;
import nettion.event.EventBus;
import nettion.event.events.render.EventBloom;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender2D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.fonts.old.Fonts;
import nettion.ui.notification.Notification;
import nettion.utils.ServerUtils;
import nettion.utils.render.RenderUtils;
import nettion.utils.render.RoundedUtils;
import nettion.utils.render.blur.KawaseBloom;
import nettion.utils.render.blur.KawaseBlur;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HUD extends Module {
    public static Option<Boolean> enabled = new Option<>("Enabled", true);
    public final Mode<Enum> hmode = new Mode<>("TextMode", mode.values(), mode.Neverlose);
    public static Numbers<Double> alpha = new Numbers<>("TextAlpha", 50.0, 1.0, 255.0, 1.0);
    public static Option<Boolean> bloom = new Option<>("Bloom", false);
    public static Option<Boolean> blur = new Option<>("Blur", false);
    private final Numbers<Double> iterations = new Numbers<Double>("BlurRadius", 2.0, 1.0, 8.0,1.0);
    private final Numbers<Double> offset = new Numbers<Double>("BlurOffset", 3.0, 1.0, 10.0,1.0);
    public static Option<Boolean> sound = new Option<>("ToggleSound", true);
    private final Numbers<Double>  shadowRadius = new Numbers<Double> ("ShadowRadius", 3.0, 1.0, 8.0,0.1);
    private final Numbers<Double>  shadowOffset = new Numbers<Double> ("ShadowOffset", 1.0, 2.0, 10.0,0.1);
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public HUD() {
        super("HUD", ModuleType.Render);
        this.addValues(enabled, hmode, alpha, sound, blur, bloom,shadowRadius,shadowOffset,iterations,offset);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public void blurScreen() {
        //修改渲染顺序bloom更自然
        if (bloom.getValue()) {
            stencilFramebuffer = RenderUtils.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            EventBus.getInstance().call(new EventBloom());
            stencilFramebuffer.unbindFramebuffer();
            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, shadowRadius.getValue().intValue(), shadowOffset.getValue().intValue());
        }
        if (blur.getValue()) {
            stencilFramebuffer = RenderUtils.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            EventBus.getInstance().call(new EventBloom());
            stencilFramebuffer.unbindFramebuffer();
            KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, iterations.getValue().intValue(), offset.getValue().intValue());
        }
    }

    @EventHandler
    private void bloom(EventBloom event) {
        if (!enabled.getValue()) {
            return;
        }
        if (!mc.gameSettings.showDebugInfo) {
            if (hmode.getValue() == mode.Neverlose){
                String servername = mc.isSingleplayer() ? "Singleplayer" : mc.getCurrentServerData().serverIP;
                String text = " | " + mc.thePlayer.getName() + " | " + servername + " | " + getFormattedTimeMinute() + " | " + Minecraft.getDebugFPS() + "fps" + " | " + ServerUtils.getPing() + "ms";
                RoundedUtils.drawRound(6, 5, Fonts.R20.getStringWidth(Nettion.instance.name) + Fonts.R18.getStringWidth(text) + 3 + 3, 12, 3, new Color(0, 0, 0, 255));
                GlStateManager.resetColor();
            } else if (hmode.getValue() == mode.NeverloseNew) {
                String text = mc.thePlayer.getName() + " | " + Minecraft.getDebugFPS() + "fps" + " | " + getFormattedTimeMinute();
                RoundedUtils.drawRound(Fonts.R22.getStringWidth("NET") + 14, 5, Fonts.R18.getStringWidth(text) + 4, 12, 3, new Color(0, 0, 0, 255));
                RoundedUtils.drawRound(6, 5, Fonts.R22.getStringWidth("NET") + 4, 12, 3, new Color(0, 0, 0, 255));
            }
        }
        Notification.bloom();
    }

    @EventHandler
    private void renderHud(EventRender2D event) {
        if (!enabled.getValue()) {
            return;
        }
        if (!mc.gameSettings.showDebugInfo) {
            if (hmode.getValue() == mode.Neverlose){
                String servername = mc.isSingleplayer() ? "Singleplayer" : mc.getCurrentServerData().serverIP;
                String text = " | " + mc.thePlayer.getName() + " | " + servername + " | " + getFormattedTimeMinute() + " | " + Minecraft.getDebugFPS() + "fps" + " | " + ServerUtils.getPing() + "ms";
                RoundedUtils.drawRound(6, 5, Fonts.R20.getStringWidth(Nettion.instance.name) + Fonts.R18.getStringWidth(text) + 3 + 3, 12, 3, new Color(0, 0, 0, alpha.getValue().intValue()));
                Fonts.R20.drawString(Nettion.instance.name, 9, 8, new Color(24,114,165).getRGB());
                Fonts.R20.drawString(Nettion.instance.name, 8, 8, -1);
                Fonts.R18.drawString(text, Fonts.R20.getStringWidth(Nettion.instance.name) + 9, 8.5f, -1);
            } else if (hmode.getValue() == mode.NeverloseNew){
                String text = mc.thePlayer.getName() + " | " + Minecraft.getDebugFPS() + "fps" + " | " + getFormattedTimeMinute();
                RoundedUtils.drawRound(Fonts.R22.getStringWidth("NET") + 14, 5, Fonts.R18.getStringWidth(text) + 4, 12, 3, new Color(0, 0, 0, 255));
                RoundedUtils.drawRound(6, 5, Fonts.R22.getStringWidth("NET") + 4, 12, 3, new Color(0, 0, 0, 255));
                Fonts.R22.drawString("NET", 7, 7, new Color(24,114,165).getRGB());
                Fonts.R22.drawString("NET", 7.5f, 7.5f, -1);
                Fonts.R18.drawString(text, Fonts.R22.getStringWidth("NET") + 16, 8.5f, -1);
            }
        }
    }

    public static String getFormattedTimeMinute() {
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.now();
        return dtfTime.format(localTime);
    }

    enum mode {
        NeverloseNew,
        Neverlose,
    }
}

