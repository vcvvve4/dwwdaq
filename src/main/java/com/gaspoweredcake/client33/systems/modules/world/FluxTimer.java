/*
 * Adapted from Payload's FluxTimer (https://github.com/vcvvve4/add).
 * Payload is distributed under the GNU General Public License v3.0.
 */
package com.gaspoweredcake.client33.systems.modules.world;

import com.gaspoweredcake.client33.events.world.TickEvent;
import com.gaspoweredcake.client33.settings.DoubleSetting;
import com.gaspoweredcake.client33.settings.Setting;
import com.gaspoweredcake.client33.settings.SettingGroup;
import com.gaspoweredcake.client33.systems.modules.Categories;
import com.gaspoweredcake.client33.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class FluxTimer extends Module {
    private final SettingGroup general = settings.getDefaultGroup();
    private final Setting<Double> multiplier = general.add(new DoubleSetting.Builder()
        .name("multiplier").description("Game speed during the accelerated phase.")
        .defaultValue(1).range(0.1, 3).sliderRange(0.1, 3).build());
    private final Setting<Double> normalSeconds = general.add(new DoubleSetting.Builder()
        .name("seconds-before-enable").description("Time spent at normal speed.")
        .defaultValue(2).range(0, 10).sliderRange(0, 10).build());
    private final Setting<Double> boostedSeconds = general.add(new DoubleSetting.Builder()
        .name("seconds-before-disable").description("Time spent at the configured multiplier.")
        .defaultValue(1).range(0, 10).sliderRange(0, 10).build());

    private boolean normal;
    private long phaseStarted;

    public FluxTimer() {
        super(Categories.World, "flux-timer", "Alternates between normal speed and a timer multiplier.");
    }

    @Override
    public void onActivate() {
        normal = false;
        phaseStarted = System.nanoTime();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        long now = System.nanoTime();
        double duration = normal ? normalSeconds.get() : boostedSeconds.get();
        if (now - phaseStarted >= duration * 1_000_000_000L) {
            normal = !normal;
            phaseStarted = now;
        }
    }

    public double getMultiplier() {
        return normal ? Timer.OFF : multiplier.get();
    }
}
