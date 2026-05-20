package com.chiyuke.gridflux;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GridFlux.MOD_ID);

    public static final DeferredBlock<Block> LITHIUM_ORE = BLOCKS.registerSimpleBlock(
            "lithium_ore",
            BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<Block> RAW_LITHIUM_BLOCK = BLOCKS.registerSimpleBlock(
            "raw_lithium_block",
            BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<Block> LITHIUM_BLOCK = BLOCKS.registerSimpleBlock(
            "lithium_block",
            BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    );
}
