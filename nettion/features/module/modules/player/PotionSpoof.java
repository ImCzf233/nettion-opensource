package nettion.features.module.modules.player;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import nettion.event.EventHandler;
import nettion.event.events.world.EventUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Option;

public class PotionSpoof extends Module {
    private final Option<Boolean> speedValue = new Option<>("Speed", false);
    private final Option<Boolean> moveSlowDownValue = new Option<>("Slowness", false);
    private final Option<Boolean> hasteValue = new Option<>("Haste", false);
    private final Option<Boolean> digSlowDownValue = new Option<>("MiningFatigue", false);
    private final Option<Boolean> blindnessValue = new Option<>("Blindness", false);
    private final Option<Boolean> strengthValue = new Option<>("Strength", false);
    private final Option<Boolean> jumpBoostValue = new Option<>("JumpBoost", false);
    private final Option<Boolean> weaknessValue = new Option<>("Weakness", false);
    private final Option<Boolean> regenerationValue = new Option<>("Regeneration", false);
    private final Option<Boolean> witherValue = new Option<>("Wither", false);
    private final Option<Boolean> resistanceValue = new Option<>("Resistance", false);
    private final Option<Boolean> fireResistanceValue = new Option<>("FireResistance", false);
    private final Option<Boolean> absorptionValue = new Option<>("Absorption", false);
    private final Option<Boolean> healthBoostValue = new Option<>("HealthBoost", false);
    private final Option<Boolean> poisonValue = new Option<>("Poison", false);
    private final Option<Boolean> saturationValue = new Option<>("Saturation", false);
    private final Option<Boolean> waterBreathingValue = new Option<>("WaterBreathing", false);

    public PotionSpoof() {
        super("PotionSpoof", ModuleType.Player);
        addValues(
                speedValue,
                moveSlowDownValue,
                hasteValue,
                digSlowDownValue,
                blindnessValue,
                strengthValue,
                jumpBoostValue,
                weaknessValue,
                regenerationValue,
                witherValue,
                resistanceValue,
                fireResistanceValue,
                absorptionValue,
                healthBoostValue,
                poisonValue,
                saturationValue,
                waterBreathingValue
        );
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.removePotionEffectClient(Potion.moveSpeed.id);
            mc.thePlayer.removePotionEffectClient(Potion.digSpeed.id);
            mc.thePlayer.removePotionEffectClient(Potion.moveSlowdown.id);
            mc.thePlayer.removePotionEffectClient(Potion.blindness.id);
            mc.thePlayer.removePotionEffectClient(Potion.damageBoost.id);
            mc.thePlayer.removePotionEffectClient(Potion.jump.id);
            mc.thePlayer.removePotionEffectClient(Potion.weakness.id);
            mc.thePlayer.removePotionEffectClient(Potion.regeneration.id);
            mc.thePlayer.removePotionEffectClient(Potion.fireResistance.id);
            mc.thePlayer.removePotionEffectClient(Potion.wither.id);
            mc.thePlayer.removePotionEffectClient(Potion.resistance.id);
            mc.thePlayer.removePotionEffectClient(Potion.absorption.id);
            mc.thePlayer.removePotionEffectClient(Potion.healthBoost.id);
            mc.thePlayer.removePotionEffectClient(Potion.digSlowdown.id);
            mc.thePlayer.removePotionEffectClient(Potion.poison.id);
            mc.thePlayer.removePotionEffectClient(Potion.saturation.id);
            mc.thePlayer.removePotionEffectClient(Potion.waterBreathing.id);
        }
    }

    @EventHandler
    private void onUpdate(EventUpdate event) {
        if (speedValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1337, 1));
        }
        if (hasteValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 1337, 1));
        }
        if (moveSlowDownValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 1337, 1));
        }
        if (blindnessValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 1337, 1));
        }
        if (strengthValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 1337, 1));
        }
        if (jumpBoostValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.jump.id, 1337, 1));
        }
        if (weaknessValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.weakness.id, 1337, 1));
        }
        if (regenerationValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 1337, 1));
        }
        if (fireResistanceValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 1337, 1));
        }
        if (witherValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.wither.id, 1337, 1));
        }
        if (resistanceValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 1337, 1));
        }
        if (absorptionValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.absorption.id, 1337, 1));
        }
        if (healthBoostValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.healthBoost.id, 1337, 1));
        }
        if (digSlowDownValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1337, 1));
        }
        if (poisonValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.poison.id, 1337, 1));
        }
        if (saturationValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.saturation.id, 1337, 1));
        }
        if (waterBreathingValue.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1337, 1));
        }
    }
}
