package net.reirecipehistory.fabric;

import net.fabricmc.api.ModInitializer;
import net.reirecipehistory.ReiRecipeHistory;

public class ReiRecipeHistoryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ReiRecipeHistory.init();
    }
}
