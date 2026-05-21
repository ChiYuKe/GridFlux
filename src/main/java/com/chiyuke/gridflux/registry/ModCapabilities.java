package com.chiyuke.gridflux.registry;

import com.chiyuke.gridflux.energy.BatteryPackEnergyStorage;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.item.BatteryItem;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ModCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        registerBattery(event, ModItems.BASIC_LITHIUM_BATTERY.get());
        registerBattery(event, ModItems.INTERMEDIATE_LITHIUM_BATTERY.get());
        registerBattery(event, ModItems.ADVANCED_LITHIUM_BATTERY.get());
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new BatteryPackEnergyStorage(BatteryPackInventory.fromStack(stack)),
                ModBlocks.BATTERY_PACK.get(),
                ModBlocks.INTERMEDIATE_BATTERY_PACK.get(),
                ModBlocks.ADVANCED_BATTERY_PACK.get()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.BATTERY_PACK.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
    }

    private static void registerBattery(RegisterCapabilitiesEvent event, Item item) {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> {
            if (stack.getItem() instanceof BatteryItem batteryItem) {
                return new DamagedBatteryEnergyStorage(stack, batteryItem);
            }
            return null;
        }, item);
    }

    private record DamagedBatteryEnergyStorage(ItemStack stack, BatteryItem batteryItem) implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int capacity = getMaxEnergyStored();
            int stored = getEnergyStored();
            int received = Math.min(Math.max(0, capacity - stored), Math.min(maxReceive, batteryItem.getTransferRate()));
            if (!simulate && received > 0) {
                stack.set(ModDataComponents.ENERGY.get(), stored + received);
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int stored = getEnergyStored();
            int extracted = Math.min(stored, Math.min(maxExtract, batteryItem.getTransferRate()));
            if (!simulate && extracted > 0) {
                stack.set(ModDataComponents.ENERGY.get(), stored - extracted);
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return Math.min(stack.getOrDefault(ModDataComponents.ENERGY.get(), 0), getMaxEnergyStored());
        }

        @Override
        public int getMaxEnergyStored() {
            return batteryItem.getCapacity(stack);
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return getMaxEnergyStored() > 0;
        }
    }
}
