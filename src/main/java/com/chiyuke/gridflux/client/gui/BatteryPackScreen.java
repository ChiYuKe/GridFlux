package com.chiyuke.gridflux.client.gui;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.energy.BatteryPackMode;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import com.chiyuke.gridflux.menu.BatteryPackMenu;
import com.chiyuke.gridflux.util.EnergyText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryPackScreen extends AbstractContainerScreen<BatteryPackMenu> {
    private static final int MODE_BUTTON_X = 155;
    private static final int MODE_BUTTON_Y = 76;
    private static final int MODE_BUTTON_SIZE = 10;
    private static final int MODE_LABEL_RIGHT = MODE_BUTTON_X - 4;

    public BatteryPackScreen(BatteryPackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = 76;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(MODE_BUTTON_X, MODE_BUTTON_Y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE, mouseX, mouseY)) {
            if (Minecraft.getInstance().gameMode != null) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(
                this.font,
                Component.translatable(
                        "tooltip.grid_flux.energy",
                        EnergyText.format(this.menu.getStoredEnergy()),
                        EnergyText.format(this.menu.getMaxEnergy())
                ),
                8,
                18,
                0x3F9FA7,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable(
                        "tooltip.grid_flux.battery_pack.buffer",
                        EnergyText.format(this.menu.getBufferCapacity())
                ),
                8,
                29,
                0x7C7C7C,
                false
        );
        Component modeName = this.menu.getMode().displayName();
        int textWidth = this.font.width(modeName);
        guiGraphics.drawString(
                this.font,
                modeName,
                MODE_LABEL_RIGHT - textWidth,
                this.inventoryLabelY,
                0x404040,
                false
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);
        guiGraphics.fill(x + 3, y + 3, x + this.imageWidth - 3, y + this.imageHeight - 3, 0xFFE0E0E0);
        guiGraphics.fill(x, y, x + this.imageWidth, y + 1, 0xFFFFFFFF);
        guiGraphics.fill(x, y + this.imageHeight - 1, x + this.imageWidth, y + this.imageHeight, 0xFF555555);

        drawPanel(guiGraphics, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
        drawModeIcon(guiGraphics, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, this.menu.getMode());

        for (int slot = 0; slot < BatteryPackInventory.SIZE; slot++) {
            drawSlot(guiGraphics, x + 16 + slot * 18, y + 39);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(guiGraphics, x + 7 + column * 18, y + 87 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(guiGraphics, x + 7 + column * 18, y + 145);
        }
    }

    private static void drawSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 18, y + 18, 0xFF555555);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);
        guiGraphics.fill(x + 2, y + 2, x + 16, y + 16, 0xFFCFCFCF);
    }

    private static void drawPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0xFF555555);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF8B8B8B);
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, 0xFFD6D6D6);
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + 3, 0xFFFFFFFF);
        guiGraphics.fill(x + 2, y + height - 3, x + width - 2, y + height - 2, 0xFF777777);
    }

    private static void drawModeIcon(GuiGraphics guiGraphics, int x, int y, BatteryPackMode mode) {
        if (mode == BatteryPackMode.CHARGE) {
            drawArrow(guiGraphics, x, y, 0xFF2EC4B6, true);
        } else if (mode == BatteryPackMode.DISCHARGE) {
            drawArrow(guiGraphics, x, y, 0xFFFFB000, false);
        } else {
            drawArrow(guiGraphics, x - 1, y, 0xFF2EC4B6, true);
            drawArrow(guiGraphics, x + 1, y, 0xFFFFB000, false);
        }
    }

    private static void drawArrow(GuiGraphics guiGraphics, int x, int y, int color, boolean up) {
        int center = x + 4;
        if (up) {
            guiGraphics.fill(center, y + 3, center + 1, y + 6, color);
            guiGraphics.fill(center - 1, y + 2, center + 2, y + 3, color);
            guiGraphics.fill(center - 2, y + 3, center + 3, y + 4, color);
        } else {
            guiGraphics.fill(center, y + 2, center + 1, y + 5, color);
            guiGraphics.fill(center - 2, y + 5, center + 3, y + 6, color);
            guiGraphics.fill(center - 1, y + 6, center + 2, y + 7, color);
        }
    }
}
