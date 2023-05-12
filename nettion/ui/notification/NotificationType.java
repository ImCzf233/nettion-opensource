package nettion.ui.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(20, 250, 90), "H"),
    DISABLE(new Color(255, 30, 30), "I"),
    INFO(Color.WHITE, "J"),
    WARNING(Color.YELLOW, "K");
    private final Color color;
    private final String icon;
}