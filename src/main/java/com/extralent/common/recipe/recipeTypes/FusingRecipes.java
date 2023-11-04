package com.extralent.common.recipe.recipeTypes;

import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.RecipeHandler;
import com.google.common.collect.Lists;
import net.minecraft.init.Items;

public class FusingRecipes {
    public static void registerFusingRecipes() {
        RecipeHandler.addRecipe(Lists.newArrayList(ModItems.rydrixIngot, Items.REDSTONE), ModItems.lydrix);
    }
}
