package com.chiyuke.gridflux;

import net.minecraft.network.chat.Component;

public enum BatteryPackMode {
    DISCHARGE("discharge", true, false, true),
    CHARGE("charge", false, true, false),
    STANDBY("standby", true, true, false);

    public static final BatteryPackMode DEFAULT = DISCHARGE;

    private final String key;
    private final boolean canExtract;
    private final boolean canReceive;
    private final boolean activeOutput;

    BatteryPackMode(String key, boolean canExtract, boolean canReceive, boolean activeOutput) {
        this.key = key;
        this.canExtract = canExtract;
        this.canReceive = canReceive;
        this.activeOutput = activeOutput;
    }

    public boolean canExtract() {
        return canExtract;
    }

    public boolean canReceive() {
        return canReceive;
    }

    public boolean activeOutput() {
        return activeOutput;
    }

    public Component displayName() {
        return Component.translatable("mode.grid_flux.battery_pack." + key);
    }

    public BatteryPackMode next() {
        BatteryPackMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static BatteryPackMode byId(int id) {
        BatteryPackMode[] values = values();
        return id >= 0 && id < values.length ? values[id] : DEFAULT;
    }
}
