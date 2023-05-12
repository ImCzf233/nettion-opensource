package nettion.features.command.commands;

import nettion.features.value.Value;
import nettion.config.LoadConfig;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.Nettion;
import nettion.utils.Helper;
import nettion.features.command.Command;

import java.util.Arrays;

public class Config extends Command {
    public Config() {
        super("Config", new String[]{"cfg"}, "", "sketit");
    }

    @Override
    public String execute(final String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                Helper.sendMessage("> Configs: " + Arrays.toString(LoadConfig.getList()));
            } else if (args[0].equalsIgnoreCase("save")) {
                StringBuilder values = new StringBuilder();
                Nettion.instance.getModuleManager();
                for (Module m : ModuleManager.getModules()) {
                    for (Value v : m.getValues()) {
                        values.append(String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator()));
                    }
                }
                LoadConfig.save("Normal", values.toString(), false);
                Helper.sendMessage("> Save");
            } else {
                for (String str: args) {
                    ModuleManager.readConfig(str);
                    Helper.sendMessage("Configuration and loading successful! Please bind the button manually.");
                }
            }
        } else {
            Helper.sendMessage("> Invalid syntax Valid .config <config>");
        }
        return null;
    }
}
