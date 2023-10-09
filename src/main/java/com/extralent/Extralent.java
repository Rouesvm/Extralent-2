package com.extralent;

import com.extralent.common.CommonProxy;
import com.extralent.common.item.ModItems;
import com.extralent.common.misc.ModMisc;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
	modid = ModMisc.MODID,
	name = ModMisc.NAME,
	version = ModMisc.VERSION
)
public class Extralent {

	public static final Logger LOGGER = LogManager.getLogger(ModMisc.MODID);

	public static CreativeTabs creativeTab = new CreativeTabs("Extralent") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.lydrix);
		}
	};

	@Mod.Instance(ModMisc.MODID)
	public static Extralent instance;

	@SidedProxy(clientSide = ModMisc.PROXY_CLIENT, serverSide = ModMisc.PROXY_SERVER)
	public static CommonProxy proxy;

	public static Logger logger;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
