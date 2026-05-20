package com.chiyuke.gridflux;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryPackEnergyStorage implements IEnergyStorage {
    private final BatteryPackInventory inventory;
    private final boolean canReceive;
    private final boolean canExtract;

    public BatteryPackEnergyStorage(BatteryPackInventory inventory) {
        this(inventory, inventory.getMode().canReceive(), inventory.getMode().canExtract());
    }

    public BatteryPackEnergyStorage(BatteryPackInventory inventory, boolean canReceive, boolean canExtract) {
        this.inventory = inventory;
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive) {
            return 0;
        }
        int received = 0;
        for (int i = 0; i < inventory.getContainerSize() && received < maxReceive; i++) {
            IEnergyStorage storage = getBatteryStorage(i);
            if (storage != null && storage.canReceive()) {
                received += storage.receiveEnergy(maxReceive - received, simulate);
            }
        }
        if (!simulate && received > 0) {
            inventory.setChanged();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract) {
            return 0;
        }
        int extracted = 0;
        for (int i = 0; i < inventory.getContainerSize() && extracted < maxExtract; i++) {
            IEnergyStorage storage = getBatteryStorage(i);
            if (storage != null && storage.canExtract()) {
                extracted += storage.extractEnergy(maxExtract - extracted, simulate);
            }
        }
        if (!simulate && extracted > 0) {
            inventory.setChanged();
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return inventory.getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored() {
        return inventory.getMaxEnergy();
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    private IEnergyStorage getBatteryStorage(int slot) {
        ItemStack stack = inventory.getItem(slot);
        return stack.isEmpty() ? null : stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }
}
