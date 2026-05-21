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

    private static final int BASIC_MODE_BUTTON_Y = 76;
    private static final int INTERMEDIATE_MODE_BUTTON_Y = 94;
    private static final int ADVANCED_MODE_BUTTON_Y = 130;

    private static final int MODE_BUTTON_SIZE = 10;
    private static final int MODE_LABEL_RIGHT = MODE_BUTTON_X - 4;
    private final int modeButtonY;

    public BatteryPackScreen(BatteryPackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = imageHeight(menu.getPackSlotCount());
        this.inventoryLabelY = inventoryLabelY(menu.getPackSlotCount());
        this.modeButtonY = modeButtonY(menu.getPackSlotCount());
    }

    private static int imageHeight(int slotCount) {
        if (slotCount > BatteryPackInventory.INTERMEDIATE_SIZE) {
            return 220;
        }
        return slotCount > BatteryPackInventory.BASIC_SIZE ? 184 : 166;
    }

    private static int inventoryLabelY(int slotCount) {
        if (slotCount > BatteryPackInventory.INTERMEDIATE_SIZE) {
            return 130;
        }
        return slotCount > BatteryPackInventory.BASIC_SIZE ? 94 : 76;
    }

    private static int modeButtonY(int slotCount) {
        if (slotCount > BatteryPackInventory.INTERMEDIATE_SIZE) {
            return ADVANCED_MODE_BUTTON_Y;
        }
        return slotCount > BatteryPackInventory.BASIC_SIZE ? INTERMEDIATE_MODE_BUTTON_Y : BASIC_MODE_BUTTON_Y;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(MODE_BUTTON_X, modeButtonY, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE, mouseX, mouseY)) {
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

        drawButton(guiGraphics, x + MODE_BUTTON_X, y + modeButtonY);
        drawModeIcon(guiGraphics, x + MODE_BUTTON_X, y + modeButtonY, this.menu.getMode());

        int columns = Math.min(8, this.menu.getPackSlotCount());
        for (int slot = 0; slot < this.menu.getPackSlotCount(); slot++) {
            int column = slot % columns;
            int row = slot / columns;
            drawSlot(guiGraphics, x + 16 + column * 18, y + 39 + row * 18);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(guiGraphics, x + 7 + column * 18, y + this.inventoryLabelY + 11 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(guiGraphics, x + 7 + column * 18, y + this.inventoryLabelY + 69);
        }
    }

    private static void drawSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 18, y + 18, 0xFFE0E0E0);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFFA8A8A8);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 2, 0xFF777777);
        guiGraphics.fill(x + 1, y + 1, x + 2, y + 17, 0xFF777777);
        guiGraphics.fill(x + 2, y + 2, x + 16, y + 16, 0xFFD6D6D6);
        guiGraphics.fill(x + 1, y + 16, x + 17, y + 17, 0xFFF0F0F0);
        guiGraphics.fill(x + 16, y + 1, x + 17, y + 17, 0xFFF0F0F0);
        guiGraphics.fill(x + 2, y + 15, x + 16, y + 16, 0xFFE3E3E3);
        guiGraphics.fill(x + 15, y + 2, x + 16, y + 16, 0xFFE3E3E3);
    }

    private static void drawButton(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + MODE_BUTTON_SIZE, y + MODE_BUTTON_SIZE, 0xFF555555);
        guiGraphics.fill(x + 1, y + 1, x + MODE_BUTTON_SIZE - 1, y + MODE_BUTTON_SIZE - 1, 0xFFD6D6D6);
        guiGraphics.fill(x + 3, y + 3, x + MODE_BUTTON_SIZE - 3, y + MODE_BUTTON_SIZE - 3, 0xFF3F9FA7);
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
