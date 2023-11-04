package com.extralent.common.recipe;

import com.extralent.api.tools.RecipeAPI;
import com.extralent.common.block.ModBlocks;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.recipeTypes.FusingRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeHandler {
    private static final List<RecipeAPI> recipes = new ArrayList<>();
    private static final Map<ItemStackHandler, RecipeAPI> recipeCache = new HashMap<>();

    public static RecipeAPI getRecipeForInput(ItemStackHandler inv) {
        if (recipeCache.containsKey(inv)) {
            return recipeCache.get(inv);
        }

        for (RecipeAPI recipe : recipes) {
            if (recipe.matches(inv, null)) {
                recipeCache.put(inv, recipe);
                return recipe;
            }
        }

        recipeCache.put(inv, null);
        return null;
    }

    public static void addRecipe(List<Item> input, Item output) {
        NonNullList<ItemStack> inputs = input.parallelStream()
                .map(ItemStack::new)
                .collect(Collectors.toCollection(NonNullList::create));
        addRecipe(new RecipeAPI(inputs, new ItemStack(output)));
    }

    public static void addRecipe(Item input, Item output) {
        NonNullList<ItemStack> inputs = NonNullList.create();
        inputs.add(new ItemStack(input));
        addRecipe(new RecipeAPI(inputs, new ItemStack(output)));
    }

    public static void addRecipe(RecipeAPI recipe) {
        recipes.add(recipe);
    }

    public static void registerRecipes() {
        FusingRecipes.registerFusingRecipes();
        GameRegistry.addSmelting(ModBlocks.rydrixOre, new ItemStack(ModItems.rydrixIngot, 1), 2.0f);
    }
}
