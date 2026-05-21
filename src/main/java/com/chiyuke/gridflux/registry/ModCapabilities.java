package com.chiyuke.gridflux.registry;

import com.chiyuke.gridflux.energy.BatteryPackEnergyStorage;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.item.BatteryItem;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

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
                return new ComponentEnergyStorage(
                        stack,
                        ModDataComponents.ENERGY.get(),
                        batteryItem.getCapacity(),
                        batteryItem.getTransferRate()
                );
            }
            return null;
        }, item);
    }
}
