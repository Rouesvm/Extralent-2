package com.extralent.common.recipe;

import com.extralent.api.tools.RecipeAPI;
import com.extralent.common.block.ModBlocks;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.recipeTypes.FusingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
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

    public static void registerRecipes() {
        FusingRecipes.registerRecipes();
        GameRegistry.addSmelting(ModBlocks.rydrixOre, new ItemStack(ModItems.rydrixIngot, 1), 2.0f);
    }
}
