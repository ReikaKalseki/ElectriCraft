/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.IntegrityChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Auxiliary.ElectriTab;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriOptions;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.GeoStrata.API.AcceleratorBlacklist;
import Reika.GeoStrata.API.AcceleratorBlacklist.BlacklistReason;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod( modid = "ElectriCraft", name="ElectriCraft", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI;required-after:RotaryCraft")
@NetworkMod(clientSideRequired = true, serverSideRequired = true/*,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ElectriCraftData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ElectriCraftData" }, packetHandler = ServerPackets.class)*/)

public class ElectriCraft extends DragonAPIMod {

	@Instance("ElectriCraft")
	public static ElectriCraft instance = new ElectriCraft();

	public static CreativeTabs tabElectri = new ElectriTab(CreativeTabs.getNextID(), "Rotational Electri");

	public static final Block[] blocks = new Block[ElectriBlocks.blockList.length];
	public static final Item[] items = new Item[ElectriItems.itemList.length];
	public static final ElectriConfig config = new ElectriConfig(instance, ElectriOptions.optionList, ElectriBlocks.blockList, ElectriItems.itemList, null, 0);

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.ElectriCraft.ElectriClient", serverSide="Reika.ElectriCraft.ElectriCommon")
	public static ElectriCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		logger = new ModLogger(instance, false);
		proxy.registerSounds();

		this.addBlocks();
		this.addItems();
		ElectriTiles.loadMappings();
		this.basicSetup(evt);
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ElectriBlocks.blockList, blocks);
		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
			GameRegistry.registerTileEntity(ElectriTiles.TEList[i].getTEClass(), "Electri"+ElectriTiles.TEList[i].getName());
			ReikaJavaLibrary.initClass(ElectriTiles.TEList[i].getTEClass());
		}
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ElectriItems.itemList, items);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		GameRegistry.registerWorldGenerator(new ElectriOreGenerator());

		TickRegistry.registerTickHandler(new NetworkTicker(), Side.SERVER);

		IntegrityChecker.instance.addMod(instance, ElectriBlocks.blockList, ElectriItems.itemList);

		ElectriRecipes.loadOreDict();
		ElectriRecipes.addRecipes();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		if (ModList.GEOSTRATA.isLoaded()) {
			for (int i = 0; i < ElectriTiles.TEList.length; i++) {
				ElectriTiles m = ElectriTiles.TEList[i];
				AcceleratorBlacklist.addBlacklist(m.getTEClass(), m.getName(), BlacklistReason.EXPLOIT);
			}
		}
	}

	@Override
	public String getDisplayName() {
		return "ElectriCraft";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return null;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}
}
