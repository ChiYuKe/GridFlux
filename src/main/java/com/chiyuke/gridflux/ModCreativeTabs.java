package com.chiyuke.gridflux;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GridFlux.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GRID_FLUX = CREATIVE_MODE_TABS.register("grid_flux",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.grid_flux"))
                    .icon(() -> new ItemStack(ModItems.BASIC_LITHIUM_BATTERY.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.LITHIUM_ORE);
                        output.accept(ModItems.RAW_LITHIUM);
                        output.accept(ModBlocks.RAW_LITHIUM_BLOCK);
                        output.accept(ModItems.LITHIUM_INGOT);
                        output.accept(ModBlocks.LITHIUM_BLOCK);
                        output.accept(ModItems.LITHIUM_DUST);
                        output.accept(ModItems.GRAPHITE_DUST);
                        output.accept(ModItems.COPPER_COIL);
                        output.accept(ModItems.BASIC_LITHIUM_BATTERY);
                        output.accept(ModItems.INTERMEDIATE_LITHIUM_BATTERY);
                        output.accept(ModItems.ADVANCED_LITHIUM_BATTERY);
                        output.accept(ModItems.BATTERY_PACK);
                    })
                    .build()
    );
}
