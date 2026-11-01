/*
 * Adapted from Payload (https://github.com/vcvvve4/add), GPL-3.0.
 */
package com.gaspoweredcake.client33.systems.modules.combat;

import com.gaspoweredcake.client33.settings.DoubleSetting;
import com.gaspoweredcake.client33.settings.Setting;
import com.gaspoweredcake.client33.systems.modules.Categories;
import com.gaspoweredcake.client33.systems.modules.Module;

public class NoAttackDelay extends Module {
    private final Setting<Double> baseTime = settings.getDefaultGroup().add(new DoubleSetting.Builder()
        .name("base-time")
        .description("Value used to calculate client attack cooldown progress.")
        .defaultValue(3).range(0, 3).sliderRange(0, 3).build());

    public NoAttackDelay() {
        super(Categories.Combat, "no-attack-delay", "Overrides client attack cooldown progress.");
    }

    public float progress() {
        return Math.min(1.0f, baseTime.get().floatValue() * 0.5f);
    }
}
