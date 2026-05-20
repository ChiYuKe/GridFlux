package com.chiyuke.gridflux;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GridFlux.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryPackBlockEntity>> BATTERY_PACK =
            BLOCK_ENTITIES.register("battery_pack", () -> BlockEntityType.Builder.of(
                    BatteryPackBlockEntity::new,
                    ModBlocks.BATTERY_PACK.get()
            ).build(null));
}
