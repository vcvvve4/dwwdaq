/*
 * Adapted from Payload (https://github.com/vcvvve4/add), GPL-3.0.
 */
package com.gaspoweredcake.client33.systems.modules.player;

import com.gaspoweredcake.client33.events.world.TickEvent;
import com.gaspoweredcake.client33.settings.*;
import com.gaspoweredcake.client33.systems.modules.Categories;
import com.gaspoweredcake.client33.systems.modules.Module;
import com.gaspoweredcake.client33.systems.modules.Modules;
import com.gaspoweredcake.client33.systems.modules.movement.AutoWalk;
import com.gaspoweredcake.client33.utils.misc.input.Input;
import meteordevelopment.orbit.EventHandler;

public class YawLock extends Module {
    private final SettingGroup general = settings.getDefaultGroup();
    private final Setting<Mode> mode = general.add(new EnumSetting.Builder<Mode>()
        .name("mode").description("How to lock yaw and pitch.").defaultValue(Mode.Simple).build());
    private final Setting<Double> yaw = angle("yaw-angle", 0, 360);
    private final Setting<Double> pitch = angle("pitch-angle", -90, 90);
    private final Setting<Double> alternateYaw = angle("alternate-yaw-angle", 0, 360);
    private final Setting<Double> alternatePitch = angle("alternate-pitch-angle", -90, 90);
    private final Setting<Double> alternateTime = general.add(new DoubleSetting.Builder()
        .name("alternation-time").description("Seconds between alternate angles.")
        .defaultValue(2).range(0.1, 10).sliderRange(0.1, 10).build());
    private final Setting<Boolean> useAlternateYaw = general.add(new BoolSetting.Builder()
        .name("use-alternate-yaw").description("Use the alternate yaw during the alternate phase.")
        .defaultValue(true).build());
    private final Setting<Boolean> walk = general.add(new BoolSetting.Builder()
        .name("walk-on-alternate").description("Walk forward during the alternate phase.")
        .defaultValue(false).build());
    private boolean alternate, ownsForward;
    private long phaseStarted;

    public YawLock() {
        super(Categories.Player, "yaw-lock", "Locks yaw and pitch, optionally alternating between angles.");
    }

    private Setting<Double> angle(String name, double min, double max) {
        return general.add(new DoubleSetting.Builder().name(name).description("Angle in degrees.")
            .defaultValue(0).range(min, max).sliderRange(min, max).build());
    }

    @Override
    public void onActivate() {
        alternate = false;
        phaseStarted = System.nanoTime();
    }

    @Override
    public void onDeactivate() {
        releaseForward();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) { releaseForward(); return; }
        float targetYaw, targetPitch;
        switch (mode.get()) {
            case Smart -> {
                targetYaw = Math.round((mc.player.getYRot() + 1f) / 45f) * 45f;
                targetPitch = Math.round((mc.player.getXRot() + 1f) / 30f) * 30f;
            }
            case Alternate -> {
                long now = System.nanoTime();
                if (now - phaseStarted >= alternateTime.get() * 1_000_000_000L) {
                    alternate = !alternate;
                    phaseStarted = now;
                }
                targetYaw = (alternate && useAlternateYaw.get() ? alternateYaw.get() : yaw.get()).floatValue();
                targetPitch = (alternate ? alternatePitch.get() : pitch.get()).floatValue();
            }
            default -> { targetYaw = yaw.get().floatValue(); targetPitch = pitch.get().floatValue(); }
        }
        mc.player.setYRot(targetYaw);
        mc.player.yHeadRot = targetYaw;
        mc.player.yBodyRot = targetYaw;
        mc.player.setXRot(targetPitch);
        if (mode.get() == Mode.Alternate && alternate && walk.get()) {
            mc.options.keyUp.setDown(true);
            ownsForward = true;
        } else releaseForward();
    }

    private void releaseForward() {
        if (!ownsForward) return;
        ownsForward = false;
        AutoWalk autoWalk = Modules.get().get(AutoWalk.class);
        if (autoWalk == null || !autoWalk.isActive()) mc.options.keyUp.setDown(Input.isPressed(mc.options.keyUp));
    }

    public enum Mode { Simple, Smart, Alternate }
}
