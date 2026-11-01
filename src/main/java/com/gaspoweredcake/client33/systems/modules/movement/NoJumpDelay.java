/*
 * Adapted from Payload (https://github.com/vcvvve4/add), GPL-3.0.
 */
package com.gaspoweredcake.client33.systems.modules.movement;

import com.gaspoweredcake.client33.events.world.TickEvent;
import com.gaspoweredcake.client33.mixin.LivingEntityAccessor;
import com.gaspoweredcake.client33.settings.IntSetting;
import com.gaspoweredcake.client33.settings.Setting;
import com.gaspoweredcake.client33.systems.modules.Categories;
import com.gaspoweredcake.client33.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class NoJumpDelay extends Module {
    private final Setting<Integer> delay = settings.getDefaultGroup().add(new IntSetting.Builder()
        .name("delay")
        .description("Maximum delay between jumps in ticks.")
        .defaultValue(0)
        .range(0, 20)
        .sliderRange(0, 20)
        .build());

    public NoJumpDelay() {
        super(Categories.Movement, "no-jump-delay", "Changes the delay between jumps.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;
        LivingEntityAccessor player = (LivingEntityAccessor) mc.player;
        if (player.client33$getJumpCooldown() > delay.get()) player.client33$setJumpCooldown(delay.get());
    }
}
