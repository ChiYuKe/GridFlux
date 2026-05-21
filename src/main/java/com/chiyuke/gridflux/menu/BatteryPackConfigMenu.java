package com.chiyuke.gridflux.menu;

import com.chiyuke.gridflux.block.BatteryPackBlockEntity;
import com.chiyuke.gridflux.energy.BatteryPackMode;
import com.chiyuke.gridflux.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class BatteryPackConfigMenu extends AbstractContainerMenu {
    public static final int MODE_BUTTON = 0;
    public static final int OVERLOAD_BUTTON = 1;

    private static final int DATA_MODE = 0;
    private static final int DATA_OVERLOADED = 1;
    private static final int DATA_PROGRESS = 2;
    private static final int DATA_STORED = 3;
    private static final int DATA_MAX = 4;
    private static final int DATA_BUFFER = 5;
    private static final int DATA_RANGE = 6;
    private static final int DATA_X = 7;
    private static final int DATA_Y = 8;
    private static final int DATA_Z = 9;
    private static final int DATA_COUNT = 10;

    private final BatteryPackBlockEntity blockEntity;
    private final ContainerData data;

    public BatteryPackConfigMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new SimpleContainerData(DATA_COUNT));
    }

    public BatteryPackConfigMenu(int containerId, Inventory playerInventory, BatteryPackBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity.createConfigData());
    }

    private BatteryPackConfigMenu(int containerId, Inventory playerInventory, BatteryPackBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.BATTERY_PACK_CONFIG.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        this.addDataSlots(data);
    }

    public BatteryPackMode getMode() {
        return BatteryPackMode.byId(data.get(DATA_MODE));
    }

    public boolean isOverloaded() {
        return data.get(DATA_OVERLOADED) != 0;
    }

    public int getOverloadProgress() {
        return data.get(DATA_PROGRESS);
    }

    public int getStoredEnergy() {
        return data.get(DATA_STORED);
    }

    public int getMaxEnergy() {
        return data.get(DATA_MAX);
    }

    public int getBufferCapacity() {
        return data.get(DATA_BUFFER);
    }

    public int getExplosionRange() {
        return data.get(DATA_RANGE);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(data.get(DATA_X), data.get(DATA_Y), data.get(DATA_Z));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (blockEntity == null) {
            return false;
        }
        if (id == MODE_BUTTON) {
            blockEntity.cycleMode();
            return true;
        }
        if (id == OVERLOAD_BUTTON) {
            blockEntity.toggleOverload(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity == null || blockEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
