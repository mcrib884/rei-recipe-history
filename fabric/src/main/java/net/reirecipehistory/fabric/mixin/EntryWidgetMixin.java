package net.reirecipehistory.fabric.mixin;

import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.reirecipehistory.RecipeHistoryManager;
import net.reirecipehistory.ReiRecipeHistory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to intercept interactions on REI entry widgets and add to history.
 * Covers both mouse clicks (via doAction) and keyboard shortcuts (via keyPressedIgnoreContains).
 */
@Mixin(value = EntryWidget.class, remap = false)
public abstract class EntryWidgetMixin {
    
    /**
     * Helper to add current entry to history.
     */
    private void addCurrentEntryToHistory() {
        try {
            EntryWidget self = (EntryWidget) (Object) this;
            EntryStack<?> entry = self.getCurrentEntry();
            if (entry != null && !entry.isEmpty()) {
                ReiRecipeHistory.LOGGER.info("Adding to history: {}", entry.getValue());
                RecipeHistoryManager.getInstance().addToHistory(entry);
            }
        } catch (Exception e) {
            ReiRecipeHistory.LOGGER.error("Error adding to history", e);
        }
    }
    
    /**
     * Intercept doAction which is called for mouse clicks (recipes/usages).
     */
    @Inject(method = "doAction", at = @At("RETURN"))
    private void onDoActionReturn(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            addCurrentEntryToHistory();
        }
    }
    
    /**
     * Intercept keyPressedIgnoreContains which is called for keyboard shortcuts (R/U keys).
     */
    @Inject(method = "keyPressedIgnoreContains", at = @At("RETURN"))
    private void onKeyPressedIgnoreContainsReturn(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            addCurrentEntryToHistory();
        }
    }
}
