package com.chiyuke.gridflux.block;

import com.chiyuke.gridflux.energy.BatteryPackBlockEnergyStorage;
import com.chiyuke.gridflux.energy.BatteryPackMode;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import com.chiyuke.gridflux.menu.BatteryPackMenu;
import com.chiyuke.gridflux.registry.ModBlockEntities;
import com.chiyuke.gridflux.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryPackBlockEntity extends BlockEntity implements MenuProvider {
    private final BatteryPackInventory inventory = new BatteryPackInventory(this::setChanged);
    private final BatteryPackBlockEnergyStorage energyStorage = new BatteryPackBlockEnergyStorage(this);
    private int inputBuffer;

    public BatteryPackBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BATTERY_PACK.get(), pos, blockState);
    }

    public BatteryPackInventory getInventory() {
        return inventory;
    }

    public BatteryPackBlockEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getInputBuffer() {
        return inputBuffer;
    }

    public int getInputBufferCapacity() {
        return inventory.getTransferRate();
    }

    public int receiveInputBuffer(int maxReceive, boolean simulate) {
        int received = Math.min(maxReceive, Math.max(0, getInputBufferCapacity() - inputBuffer));
        if (!simulate && received > 0) {
            inputBuffer += received;
            setChanged();
        }
        return received;
    }

    public int extractInputBuffer(int maxExtract, boolean simulate) {
        int extracted = Math.min(maxExtract, inputBuffer);
        if (!simulate && extracted > 0) {
            inputBuffer -= extracted;
            setChanged();
        }
        return extracted;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BatteryPackBlockEntity blockEntity) {
        if (!blockEntity.inventory.getMode().activeOutput()) {
            return;
        }
        BatteryPackBlockEnergyStorage source = blockEntity.getEnergyStorage();
        if (source.getEnergyStored() <= 0) {
            return;
        }

        int remainingOutput = blockEntity.inventory.getTransferRate();
        if (remainingOutput <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (remainingOutput <= 0 || source.getEnergyStored() <= 0) {
                break;
            }

            IEnergyStorage target = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction),
                    direction.getOpposite()
            );
            if (target == null || !target.canReceive()) {
                continue;
            }

            int simulated = source.extractEnergy(remainingOutput, true);
            int accepted = target.receiveEnergy(simulated, true);
            if (accepted > 0) {
                int extracted = source.extractEnergy(accepted, false);
                target.receiveEnergy(extracted, false);
                remainingOutput -= extracted;
            }
        }
    }

    public void loadFromStack(ItemStack stack) {
        BatteryPackInventory stackInventory = BatteryPackInventory.fromStack(stack);
        inventory.loadItems(stackInventory.copyItems());
        inventory.setMode(stackInventory.getMode());
        inputBuffer = 0;
    }

    public ItemStack createItemStack() {
        ItemStack stack = new ItemStack(ModBlocks.BATTERY_PACK.get());
        inventory.saveToStack(stack);
        return stack;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.grid_flux.battery_pack");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatteryPackMenu(containerId, playerInventory, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, inventory.copyItems(), registries);
        tag.putInt("Mode", inventory.getMode().ordinal());
        tag.putInt("InputBuffer", inputBuffer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> items = NonNullList.withSize(BatteryPackInventory.SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        inventory.loadItems(items);
        inventory.setMode(BatteryPackMode.byId(tag.getInt("Mode")));
        inputBuffer = Math.min(tag.getInt("InputBuffer"), getInputBufferCapacity());
    }
}
