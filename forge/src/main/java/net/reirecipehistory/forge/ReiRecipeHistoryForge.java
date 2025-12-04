package net.reirecipehistory.forge;

import net.minecraftforge.fml.common.Mod;
import net.reirecipehistory.ReiRecipeHistory;

@Mod(ReiRecipeHistory.MOD_ID)
public class ReiRecipeHistoryForge {
    public ReiRecipeHistoryForge() {
        ReiRecipeHistory.init();
    }
}
