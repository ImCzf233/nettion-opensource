package nettion.features.module.modules.render;

import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;

public class NotiTest extends Module {
    public NotiTest() {
        super("NotiTest", ModuleType.Render);
    }

    @Override
    public void onEnable() {
        NotificationManager.post(NotificationType.WARNING, "WARNING", "1234567890qwertyuiopasdfghjklzxcvbnm", 3);
        NotificationManager.post(NotificationType.DISABLE, "DISABLE", "1234567890qwertyuiopasdfghjklzxcvbnm", 3);
        NotificationManager.post(NotificationType.INFO, "INFO", "1234567890qwertyuiopasdfghjklzxcvbnm", 3);
        NotificationManager.post(NotificationType.SUCCESS, "SUCCESS", "1234567890qwertyuiopasdfghjklzxcvbnm", 3);
        this.setEnabled(false);
    }
}
