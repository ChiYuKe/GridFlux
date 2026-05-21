package com.chiyuke.gridflux.registry;

import com.chiyuke.gridflux.GridFlux;
import com.chiyuke.gridflux.menu.BatteryPackMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, GridFlux.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BatteryPackMenu>> BATTERY_PACK = MENU_TYPES.register(
            "battery_pack",
            () -> new MenuType<>(BatteryPackMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
}
