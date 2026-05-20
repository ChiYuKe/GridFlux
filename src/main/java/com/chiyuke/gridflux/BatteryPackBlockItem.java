package com.chiyuke.gridflux;

import java.text.NumberFormat;
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
    private static final NumberFormat ENERGY_FORMAT = NumberFormat.getIntegerInstance();

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
                ENERGY_FORMAT.format(inventory.getStoredEnergy()),
                ENERGY_FORMAT.format(inventory.getMaxEnergy())
        ).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable(
                "tooltip.grid_flux.battery_pack.mode",
                inventory.getMode().displayName()
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
                        ENERGY_FORMAT.format(batteryItem.getEnergy(battery)),
                        ENERGY_FORMAT.format(batteryItem.getCapacity())
                ).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
