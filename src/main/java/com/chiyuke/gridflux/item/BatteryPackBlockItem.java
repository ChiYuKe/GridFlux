package com.chiyuke.gridflux.item;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import com.chiyuke.gridflux.menu.BatteryPackMenu;
import com.chiyuke.gridflux.registry.ModDataComponents;
import com.chiyuke.gridflux.registry.ModItems;
import com.chiyuke.gridflux.util.EnergyText;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BatteryPackBlockItem extends BlockItem {
    private final int slotCount;
    private final String containerTranslationKey;

    public BatteryPackBlockItem(Block block, Properties properties) {
        this(block, BatteryPackInventory.BASIC_SIZE, "container.grid_flux.battery_pack", properties);
    }

    public BatteryPackBlockItem(Block block, int slotCount, String containerTranslationKey, Properties properties) {
        super(block, properties.stacksTo(1));
        this.slotCount = slotCount;
        this.containerTranslationKey = containerTranslationKey;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public static int getSlotCount(ItemStack stack) {
        return stack.getItem() instanceof BatteryPackBlockItem batteryPack ? batteryPack.getSlotCount() : BatteryPackInventory.BASIC_SIZE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            BatteryPackInventory inventory = BatteryPackInventory.fromStack(stack);
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, menuPlayer) -> new BatteryPackMenu(containerId, playerInventory, inventory, slotCount),
                    Component.translatable(containerTranslationKey)
            ));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BatteryPackInventory inventory = BatteryPackInventory.fromStack(stack);
        tooltipComponents.add(Component.translatable(
                "tooltip.grid_flux.energy",
                EnergyText.format(inventory.getStoredEnergy()),
                EnergyText.format(inventory.getMaxEnergy())
        ).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable(
                "tooltip.grid_flux.battery_pack.mode",
                inventory.getMode().displayName()
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                "tooltip.grid_flux.battery_pack.buffer",
                EnergyText.format(inventory.getTransferRate())
        ).withStyle(ChatFormatting.GRAY));
        int damage = stack.getOrDefault(ModDataComponents.BATTERY_PACK_DAMAGE.get(), 0);
        if (damage > 0) {
            tooltipComponents.add(Component.translatable(
                    "tooltip.grid_flux.battery_pack.damage",
                    damage,
                    2
            ).withStyle(ChatFormatting.RED));
        }

        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.grid_flux.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        Map<BatteryTooltipKey, BatteryTooltipGroup> groups = new LinkedHashMap<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack battery = inventory.getItem(i);
            if (battery.getItem() instanceof BatteryItem batteryItem) {
                BatteryTooltipKey key = new BatteryTooltipKey(battery.getItem(), batteryItem.getEnergy(battery), batteryItem.getCapacity());
                groups.computeIfAbsent(key, ignored -> new BatteryTooltipGroup(battery, batteryItem)).addSlot(i + 1);
            }
        }
        for (BatteryTooltipGroup group : groups.values()) {
            tooltipComponents.add(Component.translatable(
                    "tooltip.grid_flux.battery_pack.grouped_slot",
                    group.stack.getHoverName(),
                    group.count,
                    EnergyText.format(group.energy),
                    EnergyText.format(group.capacity)
            ).withStyle(getBatteryColor(group.stack)));
        }
    }

    private static ChatFormatting getBatteryColor(ItemStack battery) {
        if (battery.getItem() == ModItems.BASIC_LITHIUM_BATTERY.get()) {
            return ChatFormatting.YELLOW;
        }
        if (battery.getItem() == ModItems.INTERMEDIATE_LITHIUM_BATTERY.get()) {
            return ChatFormatting.GREEN;
        }
        if (battery.getItem() == ModItems.ADVANCED_LITHIUM_BATTERY.get()) {
            return ChatFormatting.LIGHT_PURPLE;
        }
        return ChatFormatting.GRAY;
    }

    private record BatteryTooltipKey(Item item, int energy, int capacity) {
    }

    private static class BatteryTooltipGroup {
        private final ItemStack stack;
        private final int energy;
        private final int capacity;
        private int count;

        private BatteryTooltipGroup(ItemStack stack, BatteryItem batteryItem) {
            this.stack = stack.copyWithCount(1);
            this.energy = batteryItem.getEnergy(stack);
            this.capacity = batteryItem.getCapacity();
        }

        private void addSlot(int slot) {
            count++;
        }
    }
}
