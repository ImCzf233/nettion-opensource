package nettion.ui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import nettion.ui.fonts.CFontRenderer;
import nettion.ui.fonts.FontLoaders;
import nettion.ui.fonts.old.Fonts;
import nettion.utils.Direction;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import nettion.utils.render.RenderUtils;
import nettion.utils.time.TimerUtil;

import java.awt.*;

@Getter
public class Notification {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final 减速动画 animation;

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new 减速动画(250, 1);
    }

    public static void bloom() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        NotificationManager.setToggleTime(0);

        for (Notification notification : NotificationManager.getNotifications()) {
            动画 animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            animation.setDuration(250);
            actualOffset = 8;

            notificationHeight = 18;
            notificationWidth = Math.max(FontLoaders.F22.getStringWidth(notification.getTitle()), FontLoaders.F18.getStringWidth(notification.getDescription()));

            x = sr.getScaledWidth() - notificationWidth * animation.getOutput().floatValue();
            y = sr.getScaledHeight() - (yOffset + 18 + notificationHeight + 10);

            notification.drawExhiBlur(x, y, notificationWidth + 5, notificationHeight + 10);

            yOffset += (notificationHeight + 10 + actualOffset) * animation.getOutput().floatValue();
        }
    }

    public void drawExhi(float x, float y, float width, float height) {
        Gui.drawRect2(x, y, width, height, new Color(0.1F, 0.1F, 0.1F, 0.45f).getRGB());
        RenderUtils.resetColor();
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, getNotificationType().getColor().getRGB());
        RenderUtils.resetColor();
        Color color = new Color(0, 0, 0);
        if (this.getNotificationType() == NotificationType.INFO) {
            color = Color.WHITE;
        } else if (this.getNotificationType() == NotificationType.WARNING) {
            color = Color.YELLOW;
        } else if (this.getNotificationType() == NotificationType.SUCCESS) {
            color = new Color(20, 250, 90);
        } else if (this.getNotificationType() == NotificationType.DISABLE) {
            color = new Color(255, 30, 30);
        }
        Fonts.ICON40.drawString(getNotificationType().getIcon(), x + 3, (y + Fonts.ICON40.getMiddleOfBox(height) + 1), color.getRGB());

        CFontRenderer tahomaFont18 = FontLoaders.F18;
        tahomaFont18.drawString(getTitle(), x + 7 + Fonts.ICON40.getStringWidth(getNotificationType().getIcon()), y + 4, Color.WHITE.getRGB());
        FontLoaders.F14.drawString(getDescription(), x + 7 + Fonts.ICON40.getStringWidth(getNotificationType().getIcon()), y + 8.5f + tahomaFont18.getHeight(), Color.WHITE.getRGB());
    }

    public void drawExhiBlur(float x, float y, float width, float height) {
        Gui.drawRect2(x, y, width, height, new Color(0, 0, 0, 255).getRGB());
        RenderUtils.resetColor();
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, new Color(0, 0, 0, 255).getRGB());
        RenderUtils.resetColor();
    }

    public static void render() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        NotificationManager.setToggleTime(0);

        for (Notification notification : NotificationManager.getNotifications()) {
            动画 animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            animation.setDuration(250);
            actualOffset = 8;

            notificationHeight = 18;
            notificationWidth = Math.max(FontLoaders.F22.getStringWidth(notification.getTitle()), FontLoaders.F18.getStringWidth(notification.getDescription()));

            x = sr.getScaledWidth() - notificationWidth * animation.getOutput().floatValue();
            y = sr.getScaledHeight() - (yOffset + 18 + notificationHeight + 10);

            notification.drawExhi(x, y, notificationWidth + 5, notificationHeight + 10);

            yOffset += (notificationHeight + 10 + actualOffset) * animation.getOutput().floatValue();
        }
    }
}
