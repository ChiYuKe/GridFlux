package com.chiyuke.gridflux.event;

import com.chiyuke.gridflux.GridFlux;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = GridFlux.MOD_ID)
public final class UnstableBatteryEvents {
    private static final String UNSTABLE_BATTERY = "GridFluxUnstableBattery";
    private static final String UNSTABLE_BATTERY_AGE = "GridFluxUnstableBatteryAge";
    private static final int COLLISION_GRACE_TICKS = 6;
    private static final int MAX_FLIGHT_TICKS = 200;
    private static final float EXPLOSION_POWER = 1.5F;

    private UnstableBatteryEvents() {
    }

    public static void spawnUnstableBattery(Level level, BlockPos origin, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel) || stack.isEmpty()) {
            return;
        }

        ItemEntity itemEntity = new ItemEntity(
                serverLevel,
                origin.getX() + 0.5D,
                origin.getY() + 0.75D,
                origin.getZ() + 0.5D,
                stack
        );
        double angle = serverLevel.random.nextDouble() * Math.PI * 2.0D;
        double speed = 0.45D + serverLevel.random.nextDouble() * 0.4D;
        Vec3 movement = new Vec3(
                Math.cos(angle) * speed,
                0.45D + serverLevel.random.nextDouble() * 0.3D,
                Math.sin(angle) * speed
        );
        itemEntity.setDeltaMovement(movement);
        itemEntity.setPickUpDelay(40);
        itemEntity.getPersistentData().putBoolean(UNSTABLE_BATTERY, true);
        serverLevel.addFreshEntity(itemEntity);
    }

    @SubscribeEvent
    public static void tickUnstableBattery(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemEntity itemEntity) || itemEntity.level().isClientSide) {
            return;
        }

        CompoundTag data = itemEntity.getPersistentData();
        if (!data.getBoolean(UNSTABLE_BATTERY)) {
            return;
        }

        int age = data.getInt(UNSTABLE_BATTERY_AGE) + 1;
        data.putInt(UNSTABLE_BATTERY_AGE, age);
        if (age <= COLLISION_GRACE_TICKS) {
            return;
        }

        if (age >= MAX_FLIGHT_TICKS || itemEntity.onGround() || itemEntity.horizontalCollision || itemEntity.verticalCollision) {
            explodeAndDiscard(itemEntity);
        }
    }

    private static void explodeAndDiscard(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        if (!level.isClientSide) {
            level.explode(
                    itemEntity,
                    itemEntity.getX(),
                    itemEntity.getY(),
                    itemEntity.getZ(),
                    EXPLOSION_POWER,
                    Level.ExplosionInteraction.BLOCK
            );
        }
        itemEntity.discard();
    }
}
