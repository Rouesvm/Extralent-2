package com.extralent.common.item;

import com.extralent.Extralent;
import com.extralent.common.misc.ModMisc;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class GenericItem extends Item {

    protected String name;

    public GenericItem(String name) {
        this.name = name;

        this.setCreativeTab(Extralent.creativeTab);
        this.setRegistryName(new ResourceLocation(ModMisc.MODID, name));
        this.setTranslationKey(ModMisc.MODID + "." + name);
    }

    public void registerItemModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
