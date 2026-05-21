package com.chiyuke.gridflux.registry;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.item.BatteryItem;
import com.chiyuke.gridflux.item.BatteryPackBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GridFlux.MOD_ID);

    public static final DeferredItem<BlockItem> LITHIUM_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.LITHIUM_ORE);
    public static final DeferredItem<BlockItem> RAW_LITHIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.RAW_LITHIUM_BLOCK);
    public static final DeferredItem<BlockItem> LITHIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.LITHIUM_BLOCK);
    public static final DeferredItem<BatteryPackBlockItem> BATTERY_PACK = ITEMS.register(
            "battery_pack",
            () -> new BatteryPackBlockItem(ModBlocks.BATTERY_PACK.get(), new Item.Properties())
    );
    public static final DeferredItem<BatteryPackBlockItem> INTERMEDIATE_BATTERY_PACK = ITEMS.register(
            "intermediate_battery_pack",
            () -> new BatteryPackBlockItem(ModBlocks.INTERMEDIATE_BATTERY_PACK.get(), com.chiyuke.gridflux.menu.BatteryPackInventory.INTERMEDIATE_SIZE, "container.grid_flux.intermediate_battery_pack", new Item.Properties())
    );
    public static final DeferredItem<BatteryPackBlockItem> ADVANCED_BATTERY_PACK = ITEMS.register(
            "advanced_battery_pack",
            () -> new BatteryPackBlockItem(ModBlocks.ADVANCED_BATTERY_PACK.get(), com.chiyuke.gridflux.menu.BatteryPackInventory.ADVANCED_SIZE, "container.grid_flux.advanced_battery_pack", new Item.Properties())
    );

    public static final DeferredItem<Item> RAW_LITHIUM = ITEMS.registerSimpleItem("raw_lithium");
    public static final DeferredItem<Item> LITHIUM_INGOT = ITEMS.registerSimpleItem("lithium_ingot");
    public static final DeferredItem<Item> LITHIUM_DUST = ITEMS.registerSimpleItem("lithium_dust");
    public static final DeferredItem<Item> GRAPHITE_DUST = ITEMS.registerSimpleItem("graphite_dust");
    public static final DeferredItem<Item> COPPER_COIL = ITEMS.registerSimpleItem("copper_coil");
    public static final DeferredItem<Item> WRENCH = ITEMS.registerSimpleItem("wrench");
    public static final DeferredItem<BatteryItem> BASIC_LITHIUM_BATTERY = ITEMS.registerItem(
            "basic_lithium_battery",
            properties -> new BatteryItem(100_000, 1_000, properties)
    );
    public static final DeferredItem<BatteryItem> INTERMEDIATE_LITHIUM_BATTERY = ITEMS.registerItem(
            "intermediate_lithium_battery",
            properties -> new BatteryItem(400_000, 4_000, properties)
    );
    public static final DeferredItem<BatteryItem> ADVANCED_LITHIUM_BATTERY = ITEMS.registerItem(
            "advanced_lithium_battery",
            properties -> new BatteryItem(1_600_000, 16_000, properties)
    );
}
