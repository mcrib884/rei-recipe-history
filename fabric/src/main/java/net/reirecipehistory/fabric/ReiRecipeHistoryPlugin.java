package net.reirecipehistory.fabric;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.reirecipehistory.ReiRecipeHistory;

/**
 * REI plugin for the recipe history addon.
 */
@Environment(EnvType.CLIENT)
public class ReiRecipeHistoryPlugin implements REIClientPlugin {
    
    // Stored by EntryListWidgetMixin for use by HistoryPanelWidget
    public static Rectangle historyPanelBounds = new Rectangle();
    public static int historyInnerX = 0;
    public static int historyInnerWidth = 0;
    
    @Override
    public double getPriority() {
        return -100; // Low priority to load after other plugins
    }
    
    @Override
    public void registerScreens(me.shedaniel.rei.api.client.registry.screen.ScreenRegistry registry) {
        ReiRecipeHistory.LOGGER.info("REI Recipe History plugin loaded!");
    }
}
