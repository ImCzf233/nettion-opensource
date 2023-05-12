package nettion;

import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import nettion.ui.menu.ClientMainMenu;
import nettion.ui.alt.AltManager;
import nettion.utils.render.ColorUtils;
import nettion.features.value.Value;
import nettion.event.events.world.EventPacketSend;
import nettion.other.FileManager;
import nettion.config.LoadConfig;
import nettion.other.FriendManager;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.ui.hudeditor.HUDEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import nettion.features.command.CommandManager;

public class Nettion {
    public final String name = "Nettion";
    public final double version = 2.2;
    public static Nettion instance = new Nettion();
    private ModuleManager modulemanager;
    private CommandManager commandmanager;
    private FriendManager friendmanager;
    public final Logger logger = LogManager.getLogger();

    @SneakyThrows
    public void initiate() {
        Display.setTitle(instance.name + " " + instance.version);
        this.commandmanager = new CommandManager();
        this.commandmanager.init();
        this.friendmanager = new FriendManager();
        this.friendmanager.init();
        this.modulemanager = new ModuleManager();
        this.modulemanager.init();
        new AltManager();
        FileManager.init();
        LoadConfig.init();
        new HUDEditor().init();
        Minecraft.getMinecraft().displayGuiScreen(new ClientMainMenu());
    }

    public ModuleManager getModuleManager() {
        return this.modulemanager;
    }

    public CommandManager getCommandManager() {
        return this.commandmanager;
    }

    public void shutDown() {
        StringBuilder values = new StringBuilder();
        instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values.append(String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator()));
            }
        }
        LoadConfig.save("Normal", values.toString(), false);
        String enabled = "";
        instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (!m.isEnabled()) continue;
            enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
        }
        FileManager.save("Enabled.txt", enabled, false);
    }

    public static void dispatchEvent(EventPacketSend event) {
        ColorUtils.getEventProtocol().dispatch(event);
    }

}