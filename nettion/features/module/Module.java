package nettion.features.module;

import nettion.Nettion;
import nettion.event.EventBus;
import nettion.features.module.modules.render.ArrayListMod;
import nettion.features.module.modules.render.HUD;
import nettion.features.value.Value;
import nettion.other.FileManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import nettion.utils.SoundPlayer;

public class Module {
   public String name;
   private String suffix;
   public double AnimationX = 28f,AnimationY = 30f;
   private boolean enabled;
   public boolean enabledOnStartup = false;
   private int key;
   public List<Value> values;
   public ModuleType type;
   public static Minecraft mc = Minecraft.getMinecraft();
   public static Random random = new Random();

   public Module(String name, ModuleType type) {
      this.name = name;
      this.type = type;
      this.suffix = "";
      this.key = 0;
      this.enabled = false;
      this.values = new ArrayList();
   }

   public String getName() {
      return this.name;
   }

   public ModuleType getType() {
      return this.type;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

    public String getSuffix() {
      if (ArrayListMod.hideTag.getValue()) {
         return "";
      } else {
         return this.suffix;
      }
   }

   public void setSuffix(Object obj) {
      String suffix = obj.toString();
      if(suffix.isEmpty()) {
         this.suffix = suffix;
      } else {
         this.suffix = String.format("\u00a77\u00a7f%s\u00a77", new Object[]{EnumChatFormatting.GRAY + suffix});
      }
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if(enabled) {
         this.onEnable();
         EventBus.getInstance().register(new Object[]{this});
         if (mc.theWorld != null) {
            if (HUD.sound.getValue()) {
               new SoundPlayer().playSound(SoundPlayer.SoundType.Enable, -5);
            }
         }
      } else {
         EventBus.getInstance().unregister(new Object[]{this});
         if (mc.theWorld != null) {
            if (HUD.sound.getValue()) {
               new SoundPlayer().playSound(SoundPlayer.SoundType.Disable, -5);
            }
         }
         this.onDisable();
      }
   }

   public void addValues(Value... values) {
      Value[] var5 = values;
      int var4 = values.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Value value = var5[var3];
         this.values.add(value);
      }
   }

   public List<Value> getValues() {
      return this.values;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
      String content = "";
      Nettion.instance.getModuleManager();

      Module m;
      for(Iterator var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", new Object[]{m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator()})) {
         m = (Module)var4.next();
      }

      FileManager.save("Binds.txt", content, false);
   }

   public void onEnable() {
   }

   public void onDisable() {
   }
}

