package com.chiyuke.gridflux.menu;

import com.chiyuke.gridflux.energy.BatteryPackMode;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BatteryPackMenu extends AbstractContainerMenu {
    private final int packSlotCount;
    private final int playerSlotStart;
    private final int playerSlotEnd;
    private final Container container;
    private final BatteryPackInventory batteryInventory;
    private final ContainerData data;

    public BatteryPackMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, BatteryPackInventory.BASIC_SIZE);
    }

    public BatteryPackMenu(int containerId, Inventory playerInventory, int slotCount) {
        this(containerId, playerInventory, new BatteryPackInventory(slotCount, null), slotCount);
    }

    public BatteryPackMenu(int containerId, Inventory playerInventory, Container container) {
        this(containerId, playerInventory, container, container.getContainerSize());
    }

    public BatteryPackMenu(int containerId, Inventory playerInventory, Container container, int slotCount) {
        this(containerId, playerInventory, container, slotCount, menuTypeFor(slotCount));
    }

    private BatteryPackMenu(int containerId, Inventory playerInventory, Container container, int slotCount, MenuType<?> menuType) {
        super(menuType, containerId);
        checkContainerSize(container, slotCount);
        this.packSlotCount = slotCount;
        this.playerSlotStart = packSlotCount;
        this.playerSlotEnd = playerSlotStart + 36;
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

        int columns = Math.min(8, packSlotCount);
        int startX = packSlotCount > 8 ? 17 : 17;
        for (int slot = 0; slot < packSlotCount; slot++) {
            int column = slot % columns;
            int row = slot / columns;
            this.addSlot(new BatteryPackSlot(container, slot, startX + column * 18, 40 + row * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, playerInventoryY(slotCount) + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, playerInventoryY(slotCount) + 58));
        }
    }

    private static int playerInventoryY(int slotCount) {
        if (slotCount > BatteryPackInventory.INTERMEDIATE_SIZE) {
            return 142;
        }
        return slotCount > BatteryPackInventory.BASIC_SIZE ? 106 : 88;
    }

    private static MenuType<?> menuTypeFor(int slotCount) {
        if (slotCount == BatteryPackInventory.ADVANCED_SIZE) {
            return ModMenuTypes.ADVANCED_BATTERY_PACK.get();
        }
        if (slotCount == BatteryPackInventory.INTERMEDIATE_SIZE) {
            return ModMenuTypes.INTERMEDIATE_BATTERY_PACK.get();
        }
        return ModMenuTypes.BATTERY_PACK.get();
    }

    public Container getContainer() {
        return container;
    }

    public int getStoredEnergy() {
        int energy = 0;
        for (int i = 0; i < packSlotCount; i++) {
            energy += BatteryPackInventory.getStoredEnergy(container.getItem(i));
        }
        return energy;
    }

    public int getMaxEnergy() {
        int energy = 0;
        for (int i = 0; i < packSlotCount; i++) {
            energy += BatteryPackInventory.getMaxEnergy(container.getItem(i));
        }
        return energy;
    }

    public int getPackSlotCount() {
        return packSlotCount;
    }

    public int getBufferCapacity() {
        return batteryInventory != null ? batteryInventory.getTransferRate() : 0;
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

            if (index < packSlotCount) {
                if (!this.moveItemStackTo(stack, playerSlotStart, playerSlotEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (BatteryPackInventory.isBattery(stack)) {
                if (!this.moveItemStackTo(stack, 0, packSlotCount, false)) {
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
