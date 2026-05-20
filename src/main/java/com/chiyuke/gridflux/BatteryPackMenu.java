package com.chiyuke.gridflux;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BatteryPackMenu extends AbstractContainerMenu {
    private static final int PACK_SLOT_COUNT = BatteryPackInventory.SIZE;
    private static final int PLAYER_SLOT_START = PACK_SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final Container container;
    private final BatteryPackInventory batteryInventory;
    private final ContainerData data;

    public BatteryPackMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new BatteryPackInventory(null));
    }

    public BatteryPackMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.BATTERY_PACK.get(), containerId);
        checkContainerSize(container, PACK_SLOT_COUNT);
        this.container = container;
        this.batteryInventory = container instanceof BatteryPackInventory inventory ? inventory : null;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return index == 0 && batteryInventory != null ? batteryInventory.getMode().ordinal() : BatteryPackMode.DEFAULT.ordinal();
            }

            @Override
            public void set(int index, int value) {
                if (index == 0 && batteryInventory != null) {
                    batteryInventory.setMode(BatteryPackMode.byId(value));
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
        this.addDataSlots(data);
        container.startOpen(playerInventory.player);

        for (int slot = 0; slot < PACK_SLOT_COUNT; slot++) {
            this.addSlot(new BatteryPackSlot(container, slot, 17 + slot * 18, 36));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    public Container getContainer() {
        return container;
    }

    public int getStoredEnergy() {
        int energy = 0;
        for (int i = 0; i < PACK_SLOT_COUNT; i++) {
            energy += BatteryPackInventory.getStoredEnergy(container.getItem(i));
        }
        return energy;
    }

    public int getMaxEnergy() {
        int energy = 0;
        for (int i = 0; i < PACK_SLOT_COUNT; i++) {
            energy += BatteryPackInventory.getMaxEnergy(container.getItem(i));
        }
        return energy;
    }

    public BatteryPackMode getMode() {
        return BatteryPackMode.byId(data.get(0));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && batteryInventory != null) {
            batteryInventory.setMode(batteryInventory.getMode().next());
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            original = stack.copy();

            if (index < PACK_SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (BatteryPackInventory.isBattery(stack)) {
                if (!this.moveItemStackTo(stack, 0, PACK_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
