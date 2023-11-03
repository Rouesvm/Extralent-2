package com.extralent.api.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class RecipeAPI {
    private final ItemStack output;
    private final NonNullList<ItemStack> inputs;

    public RecipeAPI(NonNullList<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public boolean matches(ItemStackHandler inv, @Nullable World worldin) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemStack.areItemStacksEqual(stack, inputs.get(i))) {
                return false;
            }
        }
        return true;
    }

    public ItemStack getCraftingResult(ItemStackHandler inv) {
        return matches(inv, null) ? output.copy() : ItemStack.EMPTY;
    }
}