package com.extralent.common.item;

import com.extralent.Extralent;
import com.extralent.common.misc.ModMisc;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemBase extends Item {

    protected String name;

    public ItemBase(String name) {
        this.name = name;

        this.setCreativeTab(Extralent.creativeTab);
        this.setRegistryName(new ResourceLocation(ModMisc.MODID, name));
        this.setTranslationKey(ModMisc.MODID + "." + name);
    }

    public void registerItemModel() {
        Extralent.proxy.registerItemRenderer(this, 0, name);
    }
}
