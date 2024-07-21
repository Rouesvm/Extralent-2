package com.extralent.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeAPI {
    private final ItemStack output;
    private final NonNullList<ItemStack> inputs;

    public RecipeAPI(NonNullList<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public NonNullList<ItemStack> getInput() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean matches(ItemStackHandler inv) {
        List<ItemStack> remainingInputs = new ArrayList<>(this.inputs);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            Iterator<ItemStack> iterator = remainingInputs.iterator();
            while (iterator.hasNext()) {
                ItemStack input = iterator.next();
                if (ItemStack.areItemsEqual(stack, input) || isOreDictionaryMatch(stack, input)) {
                    iterator.remove();
                    break;
                }
            }
        }
        return remainingInputs.isEmpty();
    }

    private boolean isOreDictionaryMatch(ItemStack stack, ItemStack input) {
        int[] stacksA = OreDictionary.getOreIDs(stack);
        int[] stacksB = OreDictionary.getOreIDs(input);
        for (int stack0 : stacksA) {
            for (int stack1 : stacksB) {
                if (stack0 == stack1) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack getCraftingResult(ItemStackHandler inv) {
        return matches(inv) ? this.output.copy() : ItemStack.EMPTY;
    }
}