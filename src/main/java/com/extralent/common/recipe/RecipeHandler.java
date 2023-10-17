package com.extralent.common.recipe;

import com.extralent.api.tools.RecipeAPI;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class RecipeHandler {
    private static final List<RecipeAPI> recipes = new ArrayList<>();

    public static void addRecipe(RecipeAPI recipe) {
        recipes.add(recipe);
    }

    public static RecipeAPI getRecipeForInput(ItemStackHandler inv) {
        for (RecipeAPI recipe : recipes) {
            if (recipe.matches(inv, null)) {
                return recipe;
            }
        }
        return null;
    }
}
