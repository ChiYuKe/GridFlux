package com.chiyuke.gridflux;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GridFlux.MOD_ID);

    public static final DeferredItem<BlockItem> LITHIUM_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.LITHIUM_ORE);
    public static final DeferredItem<BlockItem> RAW_LITHIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.RAW_LITHIUM_BLOCK);
    public static final DeferredItem<BlockItem> LITHIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.LITHIUM_BLOCK);

    public static final DeferredItem<Item> RAW_LITHIUM = ITEMS.registerSimpleItem("raw_lithium");
    public static final DeferredItem<Item> LITHIUM_INGOT = ITEMS.registerSimpleItem("lithium_ingot");
    public static final DeferredItem<Item> LITHIUM_DUST = ITEMS.registerSimpleItem("lithium_dust");
}
