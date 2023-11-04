package com.extralent.common.recipe.recipeTypes;

import com.extralent.common.block.ModBlocks;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.RecipeHandler;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class FusingRecipes {
    public static void registerFusingRecipes() {
        RecipeHandler.addRecipe(Lists.newArrayList(ModItems.rydrixIngot, Items.REDSTONE), ModItems.lydrix);
        RecipeHandler.addRecipe(Lists.newArrayList(ModBlocks.blockLydrix.getItemBlock(), Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)), ModBlocks.machineCasing.getItemBlock());
    }
}
