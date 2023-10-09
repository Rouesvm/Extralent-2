package com.extralent.common.item;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {
    public static ItemBase lydrix = new ItemBase("lydrix");

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                lydrix
        );
    }

    public static void registerModels() {
        lydrix.registerItemModel();
    }
}