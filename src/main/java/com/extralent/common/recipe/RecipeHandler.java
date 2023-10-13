package com.extralent.common.recipe;

import com.extralent.api.tools.RecipeAPI;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeHandler {
    private static final List<RecipeAPI> recipes = new ArrayList<>();

    public static void addRecipe(RecipeAPI recipe) {
        recipes.add(recipe);
    }

    public static RecipeAPI getRecipeForInput(ItemStack input1, ItemStack input2) {
        for (RecipeAPI recipe : recipes) {
            if (recipe.matches(input1, input2)) {
                return recipe;
            }
        }
        return null;
    }
}
