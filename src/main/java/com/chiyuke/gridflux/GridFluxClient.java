package com.chiyuke.gridflux;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


@Mod(value = GridFlux.MOD_ID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = GridFlux.MOD_ID, value = Dist.CLIENT)
public class GridFluxClient {
    public GridFluxClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

        GridFlux.LOGGER.info("HELLO FROM CLIENT SETUP");
    }

    @SubscribeEvent
    static void registerMenuScreens(RegisterMenuScreensEvent event) {

    }
}
