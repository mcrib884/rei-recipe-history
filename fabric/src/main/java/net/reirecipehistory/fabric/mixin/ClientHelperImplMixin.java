package net.reirecipehistory.fabric.mixin;

import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.ClientHelperImpl;
import net.reirecipehistory.RecipeHistoryManager;
import net.reirecipehistory.ReiRecipeHistory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to intercept ALL recipe view openings in REI.
 * This hooks into the central openView method which is called regardless of
 * how the user opens recipes (REI sidebar click, R/U key on inventory, etc.)
 */
@Mixin(value = ClientHelperImpl.class, remap = false)
public class ClientHelperImplMixin {
    
    /**
     * Intercept openView to capture all recipe/usage lookups.
     * The ViewSearchBuilder contains the stacks being viewed.
     */
    @Inject(method = "openView", at = @At("HEAD"))
    private void onOpenView(ViewSearchBuilder builder, CallbackInfoReturnable<Boolean> cir) {
        try {
            // Add items whose recipes are being viewed
            for (EntryStack<?> stack : builder.getRecipesFor()) {
                if (stack != null && !stack.isEmpty()) {
                    ReiRecipeHistory.LOGGER.info("Adding recipe lookup to history: {}", stack.getValue());
                    RecipeHistoryManager.getInstance().addToHistory(stack);
                }
            }
            
            // Add items whose usages are being viewed
            for (EntryStack<?> stack : builder.getUsagesFor()) {
                if (stack != null && !stack.isEmpty()) {
                    ReiRecipeHistory.LOGGER.info("Adding usage lookup to history: {}", stack.getValue());
                    RecipeHistoryManager.getInstance().addToHistory(stack);
                }
            }
        } catch (Exception e) {
            ReiRecipeHistory.LOGGER.error("Error adding to history from openView", e);
        }
    }
}
