package net.reirecipehistory.fabric.mixin;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import net.reirecipehistory.ReiRecipeHistory;
import net.reirecipehistory.fabric.ReiRecipeHistoryPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to modify EntryListWidget bounds to reserve space for the history panel.
 */
@Mixin(value = EntryListWidget.class, remap = false)
public abstract class EntryListWidgetMixin {
    
    @Shadow
    protected Rectangle bounds;
    
    /**
     * After bounds are set in updateArea, reduce height to make room for history panel.
     * The updateArea method sets this.bounds from REIRuntime.calculateEntryListArea()
     * and then calls updateEntriesPosition() - we inject between these to modify bounds.
     */
    @Inject(method = "updateArea", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/entrylist/EntryListWidget;updateEntriesPosition()V"))
    private void beforeUpdateEntriesPosition(Rectangle bounds, String searchTerm, CallbackInfo ci) {
        if (this.bounds != null && !this.bounds.isEmpty()) {
            int entrySize = EntryListWidget.entrySize();
            int panelHeight = entrySize * 2; // 2 rows for history
            
            // Only modify if bounds are large enough
            if (this.bounds.height > panelHeight + entrySize) {
                // Store the original bottom position for the history panel
                int historyY = this.bounds.getMaxY() - panelHeight;
                
                // Store bounds for the history panel to use
                ReiRecipeHistoryPlugin.historyPanelBounds = new Rectangle(
                    this.bounds.x,
                    historyY,
                    this.bounds.width,
                    panelHeight
                );
                
                // Reduce the entry list bounds height to make room
                this.bounds = new Rectangle(
                    this.bounds.x,
                    this.bounds.y,
                    this.bounds.width,
                    this.bounds.height - panelHeight
                );
                
                ReiRecipeHistory.LOGGER.debug("Entry list shrunk to: {}, history panel: {}", 
                    this.bounds, ReiRecipeHistoryPlugin.historyPanelBounds);
            }
        }
    }
    
    /**
     * After updateEntriesPosition completes, capture the innerBounds for alignment.
     */
    @Inject(method = "updateEntriesPosition", at = @At("TAIL"))
    private void afterUpdateEntriesPosition(CallbackInfo ci) {
        // Get innerBounds via accessor
        Rectangle innerBounds = ((EntryListWidgetAccessor) this).getInnerBounds();
        
        if (innerBounds != null && !innerBounds.isEmpty()) {
            // Store the inner positioning for the history panel to use
            ReiRecipeHistoryPlugin.historyInnerX = innerBounds.x;
            ReiRecipeHistoryPlugin.historyInnerWidth = innerBounds.width;
            
            ReiRecipeHistory.LOGGER.debug("Entry list innerBounds: {}", innerBounds);
        }
    }
}
