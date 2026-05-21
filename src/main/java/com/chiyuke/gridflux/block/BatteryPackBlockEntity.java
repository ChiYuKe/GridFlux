package com.chiyuke.gridflux.block;

import com.chiyuke.gridflux.energy.BatteryPackBlockEnergyStorage;
import com.chiyuke.gridflux.energy.BatteryPackMode;
import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.event.UnstableBatteryEvents;
import com.chiyuke.gridflux.item.BatteryItem;
import com.chiyuke.gridflux.menu.BatteryPackConfigMenu;
import com.chiyuke.gridflux.menu.BatteryPackInventory;
import com.chiyuke.gridflux.menu.BatteryPackMenu;
import com.chiyuke.gridflux.registry.ModBlockEntities;
import com.chiyuke.gridflux.registry.ModBlocks;
import com.chiyuke.gridflux.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryPackBlockEntity extends BlockEntity implements MenuProvider {
    private static final int OVERLOAD_MAX = 24_000;
    private static final int OVERLOAD_WARNING_THRESHOLD = OVERLOAD_MAX * 70 / 100;
    private static final int OVERLOAD_OUTPUT_MULTIPLIER = 4;
    private static final int MIN_EXPLOSION_RANGE = 2;
    private static final int MAX_EXPLOSION_RANGE = 16;
    private static final int ENERGY_PER_EXPLOSION_RANGE = 3_657_143;

    private static final float DAMAGE_CHANCE = 1F;

    private final BatteryPackInventory inventory;
    private final BatteryPackBlockEnergyStorage energyStorage = new BatteryPackBlockEnergyStorage(this);
    private int inputBuffer;
    private boolean overloaded;
    private int overloadProgress;
    private boolean overloadWarningSent;
    private boolean exploding;

    public BatteryPackBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BATTERY_PACK.get(), pos, blockState);
        this.inventory = new BatteryPackInventory(getSlotCount(blockState), this::setChanged);
    }

    private static int getSlotCount(BlockState state) {
        return state.getBlock() instanceof BatteryPackBlock batteryPack ? batteryPack.getSlotCount() : BatteryPackInventory.BASIC_SIZE;
    }

    public BatteryPackInventory getInventory() {
        return inventory;
    }

    public BatteryPackBlockEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getInputBuffer() {
        return inputBuffer;
    }

    public int getInputBufferCapacity() {
        return inventory.getTransferRate();
    }

    public int getOutputRate() {
        int transferRate = inventory.getTransferRate();
        return overloaded ? transferRate * OVERLOAD_OUTPUT_MULTIPLIER : transferRate;
    }

    public int getOverloadProgress() {
        return overloadProgress;
    }

    public int getOverloadMax() {
        return OVERLOAD_MAX;
    }

    public boolean isOverloaded() {
        return overloaded;
    }

    public boolean isExploding() {
        return exploding;
    }

    public int getExplosionRange() {
        return getExplosionRangeForEnergy(inventory.getStoredEnergy());
    }

    public int getMaxExplosionRange() {
        return getExplosionRangeForEnergy(inventory.getMaxEnergy());
    }

    private static int getExplosionRangeForEnergy(int energy) {
        if (energy <= 0) {
            return MIN_EXPLOSION_RANGE;
        }
        int extraRange = (int) Math.ceil(energy / (double) ENERGY_PER_EXPLOSION_RANGE);
        return Math.max(MIN_EXPLOSION_RANGE, Math.min(MAX_EXPLOSION_RANGE, MIN_EXPLOSION_RANGE + extraRange));
    }

    public int receiveInputBuffer(int maxReceive, boolean simulate) {
        int received = Math.min(maxReceive, Math.max(0, getInputBufferCapacity() - inputBuffer));
        if (!simulate && received > 0) {
            inputBuffer += received;
            setChanged();
        }
        return received;
    }

    public int extractInputBuffer(int maxExtract, boolean simulate) {
        int extracted = Math.min(maxExtract, inputBuffer);
        if (!simulate && extracted > 0) {
            inputBuffer -= extracted;
            setChanged();
        }
        return extracted;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BatteryPackBlockEntity blockEntity) {
        blockEntity.tickOverload(level, pos);
        if (!blockEntity.inventory.getMode().activeOutput()) {
            return;
        }
        BatteryPackBlockEnergyStorage source = blockEntity.getEnergyStorage();
        if (source.getEnergyStored() <= 0) {
            return;
        }

        int remainingOutput = blockEntity.getOutputRate();
        if (remainingOutput <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (remainingOutput <= 0 || source.getEnergyStored() <= 0) {
                break;
            }

            IEnergyStorage target = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction),
                    direction.getOpposite()
            );
            if (target == null || !target.canReceive()) {
                continue;
            }

            int simulated = source.extractEnergy(remainingOutput, true);
            int accepted = target.receiveEnergy(simulated, true);
            if (accepted > 0) {
                int extracted = source.extractEnergy(accepted, false);
                target.receiveEnergy(extracted, false);
                remainingOutput -= extracted;
            }
        }
    }

    private void tickOverload(Level level, BlockPos pos) {
        boolean changed = false;
        if (overloaded && inventory.getStoredEnergy() <= 0) {
            overloaded = false;
            changed = true;
        }
        if (overloaded) {
            overloadProgress++;
            changed = true;
            if (overloadProgress >= OVERLOAD_WARNING_THRESHOLD && !overloadWarningSent) {
                sendOverloadWarning(level, pos);
                overloadWarningSent = true;
            }
            if (overloadProgress >= OVERLOAD_MAX) {
                explode(level, pos);
            }
        } else if (overloadProgress > 0) {
            overloadProgress = Math.max(0, overloadProgress - 1);
            changed = true;
            if (overloadProgress < OVERLOAD_WARNING_THRESHOLD && overloadWarningSent) {
                overloadWarningSent = false;
            }
        }
        if (changed) {
            setChanged();
        }
    }

    private void sendOverloadWarning(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Component message = Component.translatable(
                "message.grid_flux.battery_pack.overload_critical",
                pos.getX(),
                pos.getY(),
                pos.getZ()
        ).withStyle(ChatFormatting.RED);
        for (Player player : serverLevel.players()) {
            if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 4096.0D) {
                player.displayClientMessage(message, false);
            }
        }
    }

    private void explode(Level level, BlockPos pos) {
        overloaded = false;
        overloadProgress = 0;
        exploding = true;
        float power = Math.max(1.0F, getExplosionRange() * 0.5F);
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, power, Level.ExplosionInteraction.BLOCK);
        dropDamagedBatteries(level, pos);
    }

    private void dropDamagedBatteries(Level level, BlockPos pos) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack battery = damageBattery(inventory.getItem(i));
            if (battery.isEmpty()) {
                continue;
            }
            if (level.random.nextFloat() < DAMAGE_CHANCE) {
                UnstableBatteryEvents.spawnUnstableBattery(level, pos, battery);
            } else {
                Block.popResource(level, pos, battery);
            }
        }
    }

    private ItemStack damageBattery(ItemStack stack) {
        if (!(stack.getItem() instanceof BatteryItem batteryItem)) {
            return ItemStack.EMPTY;
        }
        int damage = batteryItem.getDamage(stack) + 1;
        if (damage > BatteryItem.MAX_DAMAGE) {
            return ItemStack.EMPTY;
        }
        ItemStack damaged = stack.copyWithCount(1);
        damaged.set(ModDataComponents.ENERGY.get(), 0);
        damaged.set(ModDataComponents.BATTERY_DAMAGE.get(), damage);
        return damaged;
    }

    public void loadFromStack(ItemStack stack) {
        BatteryPackInventory stackInventory = BatteryPackInventory.fromStack(stack);
        inventory.loadItems(stackInventory.copyItems());
        inventory.setMode(stackInventory.getMode());
        inputBuffer = 0;
        overloaded = false;
        overloadProgress = 0;
        overloadWarningSent = false;
    }

    public ItemStack createItemStack() {
        ItemStack stack = new ItemStack(getBlockState().getBlock());
        inventory.saveToStack(stack);
        return stack;
    }

    @Override
    public Component getDisplayName() {
        if (getBlockState().is(ModBlocks.ADVANCED_BATTERY_PACK.get())) {
            return Component.translatable("container.grid_flux.advanced_battery_pack");
        }
        return Component.translatable(getBlockState().is(ModBlocks.INTERMEDIATE_BATTERY_PACK.get()) ? "container.grid_flux.intermediate_battery_pack" : "container.grid_flux.battery_pack");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatteryPackMenu(containerId, playerInventory, inventory, inventory.getContainerSize());
    }

    public AbstractContainerMenu createConfigMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatteryPackConfigMenu(containerId, playerInventory, this);
    }

    public ContainerData createConfigData() {
        return new SimpleContainerData(12) {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> inventory.getMode().ordinal();
                    case 1 -> overloaded ? 1 : 0;
                    case 2 -> overloadProgress;
                    case 3 -> inventory.getStoredEnergy();
                    case 4 -> inventory.getMaxEnergy();
                    case 5 -> getOutputRate();
                    case 6 -> getExplosionRange();
                    case 7 -> worldPosition.getX();
                    case 8 -> worldPosition.getY();
                    case 9 -> worldPosition.getZ();
                    case 10 -> getMaxExplosionRange();
                    case 11 -> getOverloadMax();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }
        };
    }

    public void cycleMode() {
        inventory.setMode(inventory.getMode().next());
    }

    public void toggleOverload(Player player) {
        if (!overloaded && inventory.getStoredEnergy() <= 0) {
            player.displayClientMessage(Component.translatable("message.grid_flux.battery_pack.overload_empty"), true);
            return;
        }
        overloaded = !overloaded;
        if (overloaded) {
            player.displayClientMessage(Component.translatable("message.grid_flux.battery_pack.overload_warning"), true);
        }
        setChanged();
    }

    public void showExplosionRange(Player player) {
        player.displayClientMessage(Component.translatable("message.grid_flux.battery_pack.range", getExplosionRange()), true);
        setChanged();
    }

    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, inventory.copyItems(), registries);
        tag.putInt("Mode", inventory.getMode().ordinal());
        tag.putInt("InputBuffer", inputBuffer);
        tag.putBoolean("Overloaded", overloaded);
        tag.putInt("OverloadProgress", overloadProgress);
        tag.putBoolean("OverloadWarningSent", overloadWarningSent);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        inventory.loadItems(items);
        inventory.setMode(BatteryPackMode.byId(tag.getInt("Mode")));
        inputBuffer = Math.min(tag.getInt("InputBuffer"), getInputBufferCapacity());
        overloaded = tag.getBoolean("Overloaded");
        overloadProgress = Math.min(OVERLOAD_MAX - 1, Math.max(0, tag.getInt("OverloadProgress")));
        overloadWarningSent = tag.getBoolean("OverloadWarningSent");
    }
}
