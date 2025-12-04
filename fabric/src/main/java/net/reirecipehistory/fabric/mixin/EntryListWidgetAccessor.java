package net.reirecipehistory.fabric.mixin;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin to get innerBounds from EntryListWidget.
 */
@Mixin(value = EntryListWidget.class, remap = false)
public interface EntryListWidgetAccessor {
    
    @Accessor("innerBounds")
    Rectangle getInnerBounds();
}
