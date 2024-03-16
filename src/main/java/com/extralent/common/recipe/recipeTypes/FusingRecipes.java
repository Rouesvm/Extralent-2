package com.extralent.common.recipe.recipeTypes;

import com.extralent.common.block.ModBlocks;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.RecipeHandler;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class FusingRecipes extends RecipeHandler {
    public static void registerFusingRecipes() {
        addRecipe(Lists.newArrayList(ModItems.rydrixIngot, Items.REDSTONE), ModItems.lydrix);
        addRecipe(Lists.newArrayList(Item.getItemFromBlock(ModBlocks.blockLydrix), Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)),
                Item.getItemFromBlock(ModBlocks.machineCasing)
        );
    }
}
