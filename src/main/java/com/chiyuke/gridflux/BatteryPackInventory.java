package com.chiyuke.gridflux;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BatteryPackInventory extends SimpleContainer {
    public static final int SIZE = 8;

    private Runnable changedCallback;
    private BatteryPackMode mode = BatteryPackMode.DEFAULT;

    public BatteryPackInventory(Runnable changedCallback) {
        super(SIZE);
        this.changedCallback = changedCallback;
    }

    public static BatteryPackInventory fromStack(ItemStack stack) {
        BatteryPackInventory inventory = new BatteryPackInventory(null);
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        stack.getOrDefault(net.minecraft.core.component.DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        inventory.mode = BatteryPackMode.byId(stack.getOrDefault(ModDataComponents.BATTERY_PACK_MODE.get(), BatteryPackMode.DEFAULT.ordinal()));
        for (int i = 0; i < SIZE; i++) {
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
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < SIZE; i++) {
            items.set(i, getItem(i).copy());
        }
        return items;
    }

    public void loadItems(NonNullList<ItemStack> items) {
        for (int i = 0; i < SIZE; i++) {
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
}
