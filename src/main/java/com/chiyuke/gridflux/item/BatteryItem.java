package com.chiyuke.gridflux.item;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.registry.ModDataComponents;
import com.chiyuke.gridflux.util.EnergyText;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class BatteryItem extends Item {
    private final int capacity;
    private final int transferRate;

    public BatteryItem(int capacity, int transferRate, Properties properties) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.transferRate = transferRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTransferRate() {
        return transferRate;
    }

    public int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getEnergy(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getEnergy(stack) / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x62E8F0;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(
                "tooltip.grid_flux.energy",
                EnergyText.format(getEnergy(stack)),
                EnergyText.format(capacity)
        ).withStyle(ChatFormatting.AQUA));
    }
}
