package com.chiyuke.gridflux;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

// 使用 @Mod 注解标记这是模组的主入口，传入模组 ID
@Mod(GridFlux.MOD_ID)
public class GridFlux {
    public static final String MOD_ID = "grid_flux";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 模组的构造方法，游戏加载时 NeoForge 会自动调用
    public GridFlux(IEventBus modEventBus, ModContainer modContainer) {


        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        // 注册常规设置事件的监听器
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        // 注册模组的配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}", Config.MAGIC_NUMBER_INTRODUCTION.get() + Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.LITHIUM_ORE);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.RAW_LITHIUM);
            event.accept(ModItems.LITHIUM_INGOT);
            event.accept(ModItems.LITHIUM_DUST);
            event.accept(ModBlocks.RAW_LITHIUM_BLOCK);
            event.accept(ModBlocks.LITHIUM_BLOCK);
        }
    }

}
