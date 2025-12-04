package net.reirecipehistory.fabric.mixin;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import net.reirecipehistory.ReiRecipeHistory;
import net.reirecipehistory.fabric.HistoryPanelWidget;
import net.reirecipehistory.fabric.ReiRecipeHistoryPlugin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to add the history panel widget to the REI overlay.
 */
@Mixin(value = ScreenOverlayImpl.class, remap = false)
public abstract class ScreenOverlayImplMixin {
    
    @Shadow
    @Final
    private List<Widget> widgets;
    
    @Shadow
    public abstract Rectangle getBounds();
    
    @Unique
    private HistoryPanelWidget reirecipehistory$historyPanel;
    
    /**
     * Inject into init() to add our history panel widget.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        try {
            // Wait for the entry list to provide its bounds via the mixin
            Rectangle panelBounds = ReiRecipeHistoryPlugin.historyPanelBounds;
            
            if (panelBounds == null || panelBounds.isEmpty()) {
                // Fallback: calculate from entry list bounds
                EntryListWidget entryList = ScreenOverlayImpl.getEntryListWidget();
                Rectangle entryListBounds = entryList.getBounds();
                
                if (entryListBounds == null || entryListBounds.isEmpty()) {
                    ReiRecipeHistory.LOGGER.warn("Entry list bounds empty");
                    return;
                }
                
                int entrySize = EntryListWidget.entrySize();
                int panelHeight = entrySize * 2;
                
                // Position right below the entry list
                panelBounds = new Rectangle(
                    entryListBounds.x,
                    entryListBounds.getMaxY(),
                    entryListBounds.width,
                    panelHeight
                );
            }
            
            reirecipehistory$historyPanel = new HistoryPanelWidget(panelBounds);
            
            // Add to widgets list
            widgets.add(reirecipehistory$historyPanel);
            
            ReiRecipeHistory.LOGGER.info("History panel at: {}", panelBounds);
        } catch (Exception e) {
            ReiRecipeHistory.LOGGER.error("Failed to add history panel", e);
        }
    }
}
