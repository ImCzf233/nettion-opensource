package nettion.features.module;

import nettion.config.LoadConfig;
import nettion.features.module.modules.combat.*;
import nettion.features.module.modules.ghost.*;
import nettion.features.module.modules.movement.*;
import nettion.features.module.modules.player.*;
import nettion.features.module.modules.render.*;
import nettion.features.module.modules.world.*;
import nettion.event.EventBus;
import nettion.event.EventHandler;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.events.misc.EventKey;
import nettion.event.events.render.EventRender2D;
import nettion.event.events.render.EventRender3D;
import nettion.features.value.Value;
import nettion.other.FileManager;
import nettion.other.Manager;
import nettion.utils.render.GLUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class ModuleManager
implements Manager {
    public static List<Module> modules = new ArrayList<Module>();
    private boolean enabledNeededMod = true;

    @Override
    public void init() {
        // Combat
        modules.add(new AntiBot());
        modules.add(new AntiFireball());
        modules.add(new AutoArmor());
        modules.add(new AutoHead());
        modules.add(new AutoPot());
        modules.add(new AutoPVP());
        modules.add(new BowAimBot());
        modules.add(new Criticals());
        modules.add(new FastBow());
        modules.add(new Killaura());
        modules.add(new Regen());
        modules.add(new SuperKnockback());
        modules.add(new TargetStrafe());
        modules.add(new TPAura());
        modules.add(new Velocity());
        // Ghost
        modules.add(new AimAssist());
        modules.add(new AutoClicker());
        modules.add(new HitBox());
        modules.add(new Reach());
        modules.add(new SafeWalk());
        // Movement
        modules.add(new AirJump());
        modules.add(new AntiVoid());
        modules.add(new AutoWalk());
        modules.add(new Flight());
        modules.add(new InvMove());
        modules.add(new Jesus());
        modules.add(new LongJump());
        modules.add(new NoSlow());
        modules.add(new Scaffold());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Step());
        modules.add(new Strafe());
        // Player
        modules.add(new AntiHunger());
        modules.add(new AutoAccept());
        modules.add(new AutoHypixel());
        modules.add(new AutoRespawn());
        modules.add(new AutoTool());
        modules.add(new BedNuker());
        modules.add(new Derp());
        modules.add(new FastUse());
        modules.add(new InvCleaner());
        modules.add(new IRC());
        modules.add(new Kick());
        modules.add(new LightningTracker());
        modules.add(new MiddleClickFriends());
        modules.add(new NoCommand());
        modules.add(new NoFall());
        modules.add(new PotionSpoof());
        modules.add(new Teams());
        modules.add(new Zoot());
        // Render
        modules.add(new NotiTest());
        modules.add(new Animations());
        modules.add(new ArrayListMod());
        modules.add(new BetterChat());
        modules.add(new Cape());
        modules.add(new Chams());
        modules.add(new ChestESP());
        modules.add(new ChinaHat());
        modules.add(new ClickGui());
        modules.add(new EnchantEffect());
        modules.add(new ESP());
        modules.add(new FPSHurtCam());
        modules.add(new FreeCam());
        modules.add(new FullBright());
        modules.add(new Hotbar());
        modules.add(new HUD());
        modules.add(new HUDEditor());
        modules.add(new ItemESP());
        modules.add(new ItemPhysics());
        modules.add(new MobendsMOD());
        modules.add(new MotionBlur());
        modules.add(new NameTags());
        modules.add(new NoFov());
        modules.add(new CameraClip());
        modules.add(new NoHurtCam());
        modules.add(new NoRender());
        modules.add(new Projectiles());
        modules.add(new Scoreboard());
        modules.add(new Tracers());
        modules.add(new Trail());
        modules.add(new WindowHUD());
        modules.add(new Wings());
        modules.add(new WorldTime());
        modules.add(new XRay());
        modules.add(new Zoom());
        // World
        modules.add(new Blink());
        modules.add(new ChestStealer());
        modules.add(new Disabler());
        modules.add(new FakeLag());
        modules.add(new FastPlace());
        modules.add(new GameSpeed());
        modules.add(new MemoryFix());
        modules.add(new Phase());
        modules.add(new PingSpoof());
        modules.add(new ServerCrasher());
        modules.add(new SpeedMine());
        modules.add(new Teleport());
        modules.add(new VoidFlickFix());
        // Script

        this.readSettings();
        EventBus.getInstance().register(this);
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getModuleByClass(Class<? extends Module> cls) {
        for (Module m : modules) {
            if (m.getClass() != cls) continue;
            return m;
        }
        return null;
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public static List<Module> getModulesInType(ModuleType t) {
        ArrayList<Module> output = new ArrayList<Module>();
        for (Module m : modules) {
            if (m.getType() != t) continue;
            output.add(m);
        }
        return output;
    }

    @EventHandler
    private void onKeyPress(EventKey e) {
        for (Module m : modules) {
            if (m.getKey() != e.getKey()) continue;
            m.setEnabled(!m.isEnabled());
        }
    }

    @EventHandler
    private void onGLHack(EventRender3D e) {
        GlStateManager.getFloat(2982, (FloatBuffer) GLUtils.MODELVIEW.clear());
        GlStateManager.getFloat(2983, (FloatBuffer)GLUtils.PROJECTION.clear());
        GlStateManager.glGetInteger(2978, (IntBuffer)GLUtils.VIEWPORT.clear());
    }

    @EventHandler
    private void on2DRender(EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            for (Module m : modules) {
                if (!m.enabledOnStartup) continue;
                m.setEnabled(true);
            }
        }
    }

    private void readSettings() {
        List<String> binds = FileManager.read("Binds.txt");
        for (String v : binds) {
            String name = v.split(":")[0];
            String bind = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;
            m.setKey(Keyboard.getKeyIndex((String)bind.toUpperCase()));
        }
        List<String> enabled = FileManager.read("Enabled.txt");
        for (String v : enabled) {
            Module m = ModuleManager.getModuleByName(v);
            if (m == null) continue;
            m.enabledOnStartup = true;
        }
        List<String> vals = LoadConfig.read("Normal");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;
            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));
                    continue;
                }
                ((Mode)value).setMode(v.split(":")[2]);
            }
        }
    }

    public static void readConfig(String text) {
        List<String> vals = LoadConfig.read(text);
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null) continue;
            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));
                    continue;
                }
                ((Mode)value).setMode(v.split(":")[2]);
            }
        }
    }
}

