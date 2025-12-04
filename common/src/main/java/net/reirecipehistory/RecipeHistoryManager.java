package net.reirecipehistory;

import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the history of viewed/clicked items in REI.
 * Singleton that stores up to MAX_HISTORY items with move-to-front on duplicates.
 */
public class RecipeHistoryManager {
    private static final RecipeHistoryManager INSTANCE = new RecipeHistoryManager();
    private static final int MAX_HISTORY = 50;
    
    private final LinkedList<EntryStack<?>> history = new LinkedList<>();
    
    private RecipeHistoryManager() {}
    
    public static RecipeHistoryManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Adds an entry to history. If already present, moves to front.
     * Trims to MAX_HISTORY items.
     */
    public synchronized void addToHistory(EntryStack<?> stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        
        // Make a copy to avoid reference issues
        EntryStack<?> stackCopy = stack.normalize();
        
        // Remove if already exists (we'll add to front)
        history.removeIf(existing -> EntryStacks.equalsExact(existing, stackCopy));
        
        // Add to front
        history.addFirst(stackCopy);
        
        // Trim to max size
        while (history.size() > MAX_HISTORY) {
            history.removeLast();
        }
    }
    
    /**
     * Returns a copy of the current history list.
     */
    public synchronized List<EntryStack<?>> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Returns the number of items in history.
     */
    public synchronized int size() {
        return history.size();
    }
    
    /**
     * Clears all history.
     */
    public synchronized void clear() {
        history.clear();
    }
}
