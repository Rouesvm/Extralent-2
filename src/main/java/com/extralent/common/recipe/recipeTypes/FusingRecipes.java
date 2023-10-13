package com.extralent.common.recipe.recipeTypes;

import com.extralent.api.tools.RecipeAPI;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.RecipeHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FusingRecipes {

    public static void registerRecipes() {
        RecipeAPI recipe = new RecipeAPI(new ItemStack(ModItems.rydrixIngot), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.lydrix));
        RecipeHandler.addRecipe(recipe);
    }
}
