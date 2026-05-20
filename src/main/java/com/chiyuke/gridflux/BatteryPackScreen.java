package com.chiyuke.gridflux;

import java.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryPackScreen extends AbstractContainerScreen<BatteryPackMenu> {
    private static final NumberFormat ENERGY_FORMAT = NumberFormat.getIntegerInstance();
    private static final int MODE_X = 112;
    private static final int MODE_Y = 15;
    private static final int MODE_WIDTH = 56;
    private static final int MODE_HEIGHT = 20;

    public BatteryPackScreen(BatteryPackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(MODE_X, MODE_Y, MODE_WIDTH, MODE_HEIGHT, mouseX, mouseY)) {
            if (Minecraft.getInstance().gameMode != null) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Component modeMessage() {
        return Component.translatable("screen.grid_flux.battery_pack.mode", this.menu.getMode().displayName());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(
                this.font,
                Component.translatable(
                        "tooltip.grid_flux.energy",
                        ENERGY_FORMAT.format(this.menu.getStoredEnergy()),
                        ENERGY_FORMAT.format(this.menu.getMaxEnergy())
                ),
                8,
                18,
                0x3F9FA7,
                false
        );
        int textWidth = this.font.width(modeMessage());
        guiGraphics.drawString(
                this.font,
                modeMessage(),
                MODE_X + (MODE_WIDTH - textWidth) / 2,
                MODE_Y + 6,
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

        drawPanel(guiGraphics, x + MODE_X, y + MODE_Y, MODE_WIDTH, MODE_HEIGHT);

        for (int slot = 0; slot < BatteryPackInventory.SIZE; slot++) {
            drawSlot(guiGraphics, x + 16 + slot * 18, y + 35);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(guiGraphics, x + 7 + column * 18, y + 83 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(guiGraphics, x + 7 + column * 18, y + 141);
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
}
