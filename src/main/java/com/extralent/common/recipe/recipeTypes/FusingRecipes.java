package com.extralent.common.recipe.recipeTypes;

import com.extralent.api.tools.RecipeAPI;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.RecipeHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class FusingRecipes {

    public static void registerRecipes() {
        NonNullList<ItemStack> inputs = NonNullList.create();
        inputs.add(new ItemStack(ModItems.rydrixIngot));
        inputs.add(new ItemStack(Items.REDSTONE));
        RecipeAPI recipe = new RecipeAPI(inputs, new ItemStack(ModItems.lydrix));
        RecipeHandler.addRecipe(recipe);
    }
}
