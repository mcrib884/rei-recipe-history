package net.reirecipehistory.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.reirecipehistory.ReiRecipeHistory;

/**
 * Fabric client-side mod initializer.
 */
@Environment(EnvType.CLIENT)
public class ReiRecipeHistoryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ReiRecipeHistory.LOGGER.info("REI Recipe History client initializing...");
    }
}
