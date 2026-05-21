package com.chiyuke.gridflux.block;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.registry.ModBlockEntities;
import com.chiyuke.gridflux.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BatteryPackBlock extends BaseEntityBlock {
    public static final MapCodec<BatteryPackBlock> CODEC = simpleCodec(BatteryPackBlock::new);
    public static final TagKey<Item> WRENCHES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));
    private final int slotCount;

    public BatteryPackBlock(BlockBehaviour.Properties properties) {
        this(com.chiyuke.gridflux.menu.BatteryPackInventory.BASIC_SIZE, properties);
    }

    public BatteryPackBlock(int slotCount, BlockBehaviour.Properties properties) {
        super(properties);
        this.slotCount = slotCount;
    }

    public int getSlotCount() {
        return slotCount;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryPackBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(
                blockEntityType,
                ModBlockEntities.BATTERY_PACK.get(),
                BatteryPackBlockEntity::serverTick
        );
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BatteryPackBlockEntity blockEntity) {
            player.openMenu(blockEntity);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BatteryPackBlockEntity blockEntity) {
            if (stack.is(WRENCHES) || stack.is(ModItems.WRENCH.get())) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, menuPlayer) -> blockEntity.createConfigMenu(containerId, playerInventory, menuPlayer),
                        Component.translatable("container.grid_flux.battery_pack_config")
                ));
                return ItemInteractionResult.sidedSuccess(false);
            }
            player.openMenu(blockEntity);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof BatteryPackBlockEntity blockEntity) {
            blockEntity.loadFromStack(stack);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof BatteryPackBlockEntity blockEntity) {
                if (!blockEntity.isExploding()) {
                    Block.popResource(level, pos, blockEntity.createItemStack());
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
