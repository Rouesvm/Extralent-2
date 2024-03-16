package com.extralent.common;

import com.extralent.Extralent;
import com.extralent.api.network.Messages;
import com.extralent.common.block.ModBlocks;
import com.extralent.common.core.handler.FuelHandler;
import com.extralent.common.core.handler.GuiHandler;
import com.extralent.common.item.ModItems;
import com.extralent.common.recipe.RecipeHandler;
import com.extralent.common.worldgen.OreGenerator;
import com.extralent.common.worldgen.WorldTickHandler;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Messages.registerMessages("extralent");
        GameRegistry.registerWorldGenerator(OreGenerator.instance, 5);

        MinecraftForge.EVENT_BUS.register(OreGenerator.instance);
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Extralent.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(WorldTickHandler.instance);
        MinecraftForge.EVENT_BUS.register(FuelHandler.instance);

        RecipeHandler.registerRecipes();
        ModItems.registerFuelHandlers();
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModBlocks.registerOreDict();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ModItems.register(event.getRegistry());
        ModBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.registerBlocks(event.getRegistry());
    }

    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    public EntityPlayer getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }
}

