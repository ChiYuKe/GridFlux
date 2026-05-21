package com.chiyuke.gridflux.client.gui;

import com.chiyuke.gridflux.menu.BatteryPackConfigMenu;
import com.chiyuke.gridflux.util.EnergyText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryPackConfigScreen extends AbstractContainerScreen<BatteryPackConfigMenu> {
    private static final int BUTTON_SIZE = 10;
    private static final int MODE_BUTTON_X = 150;
    private static final int MODE_BUTTON_Y = 51;
    private static final int OVERLOAD_BUTTON_X = 150;
    private static final int OVERLOAD_BUTTON_Y = 80;
    private static final int BAR_X = 8;
    private static final int BAR_Y = 82;
    private static final int BAR_WIDTH = 92;
    private static final int BAR_HEIGHT = 10;

    public BatteryPackConfigScreen(BatteryPackConfigMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 126;
        this.inventoryLabelY = 1000;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && clickButton(mouseX, mouseY, MODE_BUTTON_X, MODE_BUTTON_Y, BatteryPackConfigMenu.MODE_BUTTON)) {
            return true;
        }
        if (button == 0 && clickButton(mouseX, mouseY, OVERLOAD_BUTTON_X, OVERLOAD_BUTTON_Y, BatteryPackConfigMenu.OVERLOAD_BUTTON)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickButton(double mouseX, double mouseY, int x, int y, int id) {
        if (!isHovering(x, y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY)) {
            return false;
        }
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
        return true;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(this.font, Component.translatable(
                "tooltip.grid_flux.energy",
                EnergyText.format(this.menu.getStoredEnergy()),
                EnergyText.format(this.menu.getMaxEnergy())
        ), 8, 18, 0x3F9FA7, false);
        guiGraphics.drawString(this.font, Component.translatable(
                "tooltip.grid_flux.battery_pack.buffer",
                EnergyText.format(this.menu.getBufferCapacity())
        ), 8, 29, 0x7C7C7C, false);
        drawRightAligned(guiGraphics, this.menu.getMode().displayName(), MODE_BUTTON_X - 4, 52);
        guiGraphics.drawString(this.font, Component.translatable("screen.grid_flux.battery_pack.overload_progress"), 8, 68, 0x404040, false);
        drawRightAligned(guiGraphics, Component.translatable(this.menu.isOverloaded() ? "screen.grid_flux.battery_pack.overload_on" : "screen.grid_flux.battery_pack.overload_off"), OVERLOAD_BUTTON_X - 4, 81);
        guiGraphics.drawString(this.font, Component.translatable("screen.grid_flux.battery_pack.range_prediction", 2, this.menu.getExplosionRange()), 8, 105, 0x404040, false);
    }

    private void drawRightAligned(GuiGraphics guiGraphics, Component text, int right, int y) {
        guiGraphics.drawString(this.font, text, right - this.font.width(text), y, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);
        guiGraphics.fill(x + 3, y + 3, x + this.imageWidth - 3, y + this.imageHeight - 3, 0xFFE0E0E0);
        guiGraphics.fill(x, y, x + this.imageWidth, y + 1, 0xFFFFFFFF);
        guiGraphics.fill(x, y + this.imageHeight - 1, x + this.imageWidth, y + this.imageHeight, 0xFF555555);
        guiGraphics.fill(x + 7, y + 46, x + this.imageWidth - 7, y + 47, 0xFFC6C6C6);
        guiGraphics.fill(x + 7, y + 97, x + this.imageWidth - 7, y + 98, 0xFFC6C6C6);
        drawButton(guiGraphics, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, 0xFF3F9FA7);
        drawButton(guiGraphics, x + OVERLOAD_BUTTON_X, y + OVERLOAD_BUTTON_Y, this.menu.isOverloaded() ? 0xFFFF5555 : 0xFF777777);
        drawProgress(guiGraphics, x + BAR_X, y + BAR_Y, this.menu.getOverloadProgress());
    }

    private static void drawButton(GuiGraphics guiGraphics, int x, int y, int color) {
        guiGraphics.fill(x, y, x + BUTTON_SIZE, y + BUTTON_SIZE, 0xFF555555);
        guiGraphics.fill(x + 1, y + 1, x + BUTTON_SIZE - 1, y + BUTTON_SIZE - 1, 0xFFD6D6D6);
        guiGraphics.fill(x + 3, y + 3, x + BUTTON_SIZE - 3, y + BUTTON_SIZE - 3, color);
    }

    private static void drawProgress(GuiGraphics guiGraphics, int x, int y, int progress) {
        guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF555555);
        guiGraphics.fill(x + 1, y + 1, x + BAR_WIDTH - 1, y + BAR_HEIGHT - 1, 0xFFCFCFCF);
        int filled = Math.max(0, Math.min(BAR_WIDTH - 2, progress * (BAR_WIDTH - 2) / 100));
        guiGraphics.fill(x + 1, y + 1, x + 1 + filled, y + BAR_HEIGHT - 1, 0xFFFF5555);
    }
}
