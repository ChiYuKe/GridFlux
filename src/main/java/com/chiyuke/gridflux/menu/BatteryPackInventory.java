package com.chiyuke.gridflux.menu;

import com.chiyuke.gridflux.energy.BatteryPackMode;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.item.BatteryItem;
import com.chiyuke.gridflux.registry.ModDataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BatteryPackInventory extends SimpleContainer {
    public static final int BASIC_SIZE = 8;
    public static final int INTERMEDIATE_SIZE = 16;
    public static final int ADVANCED_SIZE = 32;

    private final int size;
    private Runnable changedCallback;
    private BatteryPackMode mode = BatteryPackMode.DEFAULT;

    public BatteryPackInventory(Runnable changedCallback) {
        this(BASIC_SIZE, changedCallback);
    }

    public BatteryPackInventory(int size, Runnable changedCallback) {
        super(size);
        this.size = size;
        this.changedCallback = changedCallback;
    }

    public static BatteryPackInventory fromStack(ItemStack stack) {
        int size = com.chiyuke.gridflux.item.BatteryPackBlockItem.getSlotCount(stack);
        BatteryPackInventory inventory = new BatteryPackInventory(size, null);
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        stack.getOrDefault(net.minecraft.core.component.DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        inventory.mode = BatteryPackMode.byId(stack.getOrDefault(ModDataComponents.BATTERY_PACK_MODE.get(), BatteryPackMode.DEFAULT.ordinal()));
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = items.get(i);
            if (isBattery(itemStack)) {
                itemStack.setCount(1);
                inventory.setItem(i, itemStack);
            }
        }
        inventory.changedCallback = () -> inventory.saveToStack(stack);
        return inventory;
    }

    public static boolean isBattery(ItemStack stack) {
        return stack.getItem() instanceof BatteryItem;
    }

    public static int getStoredEnergy(ItemStack stack) {
        return stack.getItem() instanceof BatteryItem batteryItem ? batteryItem.getEnergy(stack) : 0;
    }

    public static int getMaxEnergy(ItemStack stack) {
        return stack.getItem() instanceof BatteryItem batteryItem ? batteryItem.getCapacity() : 0;
    }

    public int getStoredEnergy() {
        int energy = 0;
        for (int i = 0; i < getContainerSize(); i++) {
            energy += getStoredEnergy(getItem(i));
        }
        return energy;
    }

    public int getMaxEnergy() {
        int energy = 0;
        for (int i = 0; i < getContainerSize(); i++) {
            energy += getMaxEnergy(getItem(i));
        }
        return energy;
    }

    public int getTransferRate() {
        int transferRate = 0;
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (stack.getItem() instanceof BatteryItem batteryItem) {
                transferRate += batteryItem.getTransferRate();
            }
        }
        return transferRate;
    }

    public BatteryPackMode getMode() {
        return mode;
    }

    public void setMode(BatteryPackMode mode) {
        this.mode = mode;
        setChanged();
    }

    public NonNullList<ItemStack> copyItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            items.set(i, getItem(i).copy());
        }
        return items;
    }

    public void loadItems(NonNullList<ItemStack> items) {
        for (int i = 0; i < size; i++) {
            ItemStack stack = i < items.size() ? items.get(i) : ItemStack.EMPTY;
            setItem(i, isBattery(stack) ? stack.copyWithCount(1) : ItemStack.EMPTY);
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (!stack.isEmpty() && !isBattery(stack)) {
            stack = ItemStack.EMPTY;
        }
        super.setItem(index, stack.copyWithCount(Math.min(stack.getCount(), 1)));
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (changedCallback != null) {
            changedCallback.run();
        }
    }

    public void saveToStack(ItemStack stack) {
        stack.set(net.minecraft.core.component.DataComponents.CONTAINER, ItemContainerContents.fromItems(copyItems()));
        stack.set(ModDataComponents.BATTERY_PACK_MODE.get(), mode.ordinal());
    }

    public void saveDamagedToStack(ItemStack stack) {
        NonNullList<ItemStack> damagedItems = copyItems();
        for (ItemStack item : damagedItems) {
            if (item.getItem() instanceof BatteryItem) {
                item.set(ModDataComponents.ENERGY.get(), 0);
            }
        }
        stack.set(net.minecraft.core.component.DataComponents.CONTAINER, ItemContainerContents.fromItems(damagedItems));
        stack.set(ModDataComponents.BATTERY_PACK_MODE.get(), mode.ordinal());
    }
}
