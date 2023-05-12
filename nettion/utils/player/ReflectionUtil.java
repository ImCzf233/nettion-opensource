package nettion.utils.player;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

public class ReflectionUtil {
    public static void rightClickMouse() {
        try {
            String s = "func_147121_ag";
            Minecraft mc = Minecraft.getMinecraft();
            Class<?> c = mc.getClass();
            Method m = c.getDeclaredMethod(s, new Class[0]);
            m.setAccessible(true);
            m.invoke(mc, new Object[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
