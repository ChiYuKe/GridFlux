package com.chiyuke.gridflux.item;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import com.chiyuke.gridflux.menu.BatteryPackMenu;
import com.chiyuke.gridflux.registry.ModItems;
import com.chiyuke.gridflux.util.EnergyText;
import java.util.List;

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
    public BatteryPackBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            BatteryPackInventory inventory = BatteryPackInventory.fromStack(stack);
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, menuPlayer) -> new BatteryPackMenu(containerId, playerInventory, inventory),
                    Component.translatable("container.grid_flux.battery_pack")
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

        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.grid_flux.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack battery = inventory.getItem(i);
            if (battery.getItem() instanceof BatteryItem batteryItem) {
                tooltipComponents.add(Component.translatable(
                        "tooltip.grid_flux.battery_pack.slot",
                        i + 1,
                        battery.getHoverName(),
                        EnergyText.format(batteryItem.getEnergy(battery)),
                        EnergyText.format(batteryItem.getCapacity())
                ).withStyle(getBatteryColor(battery)));
            }
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
}
