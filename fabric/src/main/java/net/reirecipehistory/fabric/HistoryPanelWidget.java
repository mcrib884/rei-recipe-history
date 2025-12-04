package net.reirecipehistory.fabric;

import com.mojang.blaze3d.vertex.*;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.reirecipehistory.RecipeHistoryManager;
import net.reirecipehistory.ReiRecipeHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Widget that displays the recipe history as interactive slots.
 * Uses the exact same positioning as the main entry list for perfect alignment.
 */
@Environment(EnvType.CLIENT)
public class HistoryPanelWidget extends WidgetWithBounds {
    private static final int ROWS = 2;
    private static final int BORDER_COLOR = 0xFF8B8B8B; // Gray border like REI
    private static final int BG_COLOR = 0x80000000; // Semi-transparent black background
    
    private Rectangle bounds;
    private final List<Slot> slots = new ArrayList<>();
    
    public HistoryPanelWidget(Rectangle bounds) {
        this.bounds = bounds;
    }
    
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * Gets REI's current entry size for proper GUI scaling.
     */
    private int getEntrySize() {
        return EntryListWidget.entrySize();
    }
    
    /**
     * Rebuilds the slot widgets based on current history.
     * Uses innerBounds from the main entry list for perfect alignment.
     */
    private void rebuildSlots() {
        slots.clear();
        
        if (bounds == null || bounds.isEmpty()) {
            return;
        }
        
        List<EntryStack<?>> history = RecipeHistoryManager.getInstance().getHistory();
        int entrySize = getEntrySize();
        
        // Use the stored innerX from the main entry list for perfect alignment
        int innerX = ReiRecipeHistoryPlugin.historyInnerX;
        int innerWidth = ReiRecipeHistoryPlugin.historyInnerWidth;
        
        // Fallback if not set
        if (innerWidth <= 0) {
            innerWidth = bounds.width;
            innerX = bounds.x;
        }
        
        int slotsPerRow = Math.max(1, innerWidth / entrySize);
        int maxSlots = slotsPerRow * ROWS;
        
        for (int i = 0; i < Math.min(history.size(), maxSlots); i++) {
            int row = i / slotsPerRow;
            int col = i % slotsPerRow;
            
            // Use the exact same X position as the main list's inner slots
            int x = innerX + col * entrySize;
            int y = bounds.y + row * entrySize;
            
            EntryStack<?> entry = history.get(i);
            
            // Create interactive slot with proper settings for REI interaction
            Slot slot = Widgets.createSlot(new Rectangle(x, y, entrySize, entrySize))
                    .entry(entry)
                    .disableBackground()
                    .interactable(true)
                    .interactableFavorites(true);
            
            slots.add(slot);
        }
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (bounds == null || bounds.isEmpty()) {
            return;
        }
        
        int entrySize = getEntrySize();
        int gridHeight = entrySize * ROWS;
        
        // Use the stored inner positioning for perfect alignment
        int innerX = ReiRecipeHistoryPlugin.historyInnerX;
        int innerWidth = ReiRecipeHistoryPlugin.historyInnerWidth;
        
        // Fallback if not set
        if (innerWidth <= 0) {
            innerWidth = bounds.width;
            innerX = bounds.x;
        }
        
        // Draw background around the slot grid only
        int bgX = innerX;
        int bgY = bounds.y;
        int bgMaxX = innerX + innerWidth;
        int bgMaxY = bounds.y + gridHeight;
        
        GuiComponent.fill(matrices, bgX, bgY, bgMaxX, bgMaxY, BG_COLOR);
        
        // Draw border around the slot grid (matching 1:1 with slots)
        GuiComponent.fill(matrices, bgX, bgY, bgMaxX, bgY + 1, BORDER_COLOR); // top
        GuiComponent.fill(matrices, bgX, bgMaxY - 1, bgMaxX, bgMaxY, BORDER_COLOR); // bottom
        GuiComponent.fill(matrices, bgX, bgY, bgX + 1, bgMaxY, BORDER_COLOR); // left
        GuiComponent.fill(matrices, bgMaxX - 1, bgY, bgMaxX, bgMaxY, BORDER_COLOR); // right
        
        // Rebuild slots each frame to catch history changes
        rebuildSlots();
        
        // Render all slots
        for (Slot slot : slots) {
            slot.render(matrices, mouseX, mouseY, delta);
        }
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.unmodifiableList(slots);
    }
    
    private Slot getSlotAt(double mouseX, double mouseY) {
        for (Slot slot : slots) {
            if (slot.containsMouse(mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!bounds.contains(mouseX, mouseY)) {
            return false;
        }
        Slot slot = getSlotAt(mouseX, mouseY);
        if (slot != null) {
            return slot.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!bounds.contains(mouseX, mouseY)) {
            return false;
        }
        Slot slot = getSlotAt(mouseX, mouseY);
        if (slot != null) {
            return slot.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Slot slot = getSlotAt(mouseX, mouseY);
        if (slot != null) {
            return slot.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Slot slot = getSlotAt(mouseX, mouseY);
        if (slot != null) {
            return slot.mouseScrolled(mouseX, mouseY, amount);
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Slot slot : slots) {
            if (slot.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Slot slot : slots) {
            if (slot.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
}
