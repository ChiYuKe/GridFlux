package com.chiyuke.gridflux.energy;

import com.chiyuke.gridflux.block.BatteryPackBlockEntity;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryPackBlockEnergyStorage implements IEnergyStorage {
    private final BatteryPackBlockEntity blockEntity;

    public BatteryPackBlockEnergyStorage(BatteryPackBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        BatteryPackInventory inventory = blockEntity.getInventory();
        if (!inventory.getMode().canReceive()) {
            return 0;
        }

        int received = 0;
        if (inventory.getMode() == BatteryPackMode.BOTH) {
            received += blockEntity.receiveInputBuffer(maxReceive, simulate);
        }
        if (received < maxReceive) {
            received += new BatteryPackEnergyStorage(inventory, true, false).receiveEnergy(maxReceive - received, simulate);
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        BatteryPackInventory inventory = blockEntity.getInventory();
        if (!inventory.getMode().canExtract()) {
            return 0;
        }

        int extracted = 0;
        if (inventory.getMode() == BatteryPackMode.BOTH) {
            extracted += blockEntity.extractInputBuffer(maxExtract, simulate);
        }
        if (extracted < maxExtract) {
            extracted += new BatteryPackEnergyStorage(inventory, false, true).extractEnergy(maxExtract - extracted, simulate);
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return blockEntity.getInputBuffer() + blockEntity.getInventory().getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored() {
        return blockEntity.getInputBufferCapacity() + blockEntity.getInventory().getMaxEnergy();
    }

    @Override
    public boolean canExtract() {
        return blockEntity.getInventory().getMode().canExtract();
    }

    @Override
    public boolean canReceive() {
        return blockEntity.getInventory().getMode().canReceive();
    }
}
