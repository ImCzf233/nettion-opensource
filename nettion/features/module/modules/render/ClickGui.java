package nettion.features.module.modules.render;

import nettion.Nettion;
import nettion.config.LoadConfig;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Option;
import nettion.features.value.Value;
import nettion.other.FileManager;
import nettion.ui.clickgui.astolfo.asClickgui;
import nettion.ui.clickgui.neverlose.ClickUI;
import nettion.ui.clickgui.novoline.ClickyUI;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClickGui extends Module {
    public final Mode<Enum> mode = new Mode<>("Mode", modes.values(), modes.Soar);
    public static final Option<Boolean> Streamer = new Option<>("Streamer", true);
    public static final Option<Boolean> Visitable = new Option<>("Visitable", false);

    public static List<Module> memoriseML = new CopyOnWriteArrayList<>();
    public static ModuleType memoriseCatecory;

    public static float startX;
    public static float startY;
    public static ModuleType currentModuleType;
    public static int tempWheel;

    public static int memoriseX = 30;//30
    public static int memoriseY = 30;
    public static int memoriseWheel;

    public ClickGui() {
        super("ClickGUI", ModuleType.Render);
        addValues(mode, Streamer, Visitable);
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
        StringBuilder values = new StringBuilder();
        Nettion.instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values.append(String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator()));
            }
        }
        LoadConfig.save("Normal", values.toString(), false);
        String enabled = "";
        Nettion.instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (!m.isEnabled()) continue;
            enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
        }
        FileManager.save("Enabled.txt", enabled, false);
        if (mode.getValue() == modes.Soar) {
            mc.displayGuiScreen(new nettion.ui.clickgui.soar.ClickGui());
        } else if (mode.getValue() == modes.NeverLose) {
            mc.displayGuiScreen(new ClickUI());
            ClickUI.setX((int) startX);
            ClickUI.setY((int) startY);
            ClickUI.setWheel(tempWheel);
            if (currentModuleType != null) {
                ClickUI.setCategory(currentModuleType);
            }
        } else if (mode.getValue() == modes.Astolfo) {
            mc.displayGuiScreen(new asClickgui());
        } else if (mode.getValue() == modes.Novoline) {
            mc.displayGuiScreen(new ClickyUI());
        }
    }

    enum modes {
        Soar,
        Astolfo,
        Novoline,
        NeverLose
    }
}
