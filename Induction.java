/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.IntegrityChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ReactorCraft.ReactorRecipes;
import Reika.RotationalInduction.Auxiliary.InductionTab;
import Reika.RotationalInduction.Registry.InductionBlocks;
import Reika.RotationalInduction.Registry.InductionItems;
import Reika.RotationalInduction.Registry.InductionOptions;
import Reika.RotationalInduction.Registry.InductionOres;
import Reika.RotationalInduction.Registry.InductionTiles;
import Reika.RotationalInduction.Registry.WireType;
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

@Mod( modid = "RotationalInduction", name="Rotational Induction", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true/*,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "RotationalInductionData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "RotationalInductionData" }, packetHandler = ServerPackets.class)*/)

public class Induction extends DragonAPIMod {

	@Instance("RotationalInduction")
	public static Induction instance = new Induction();

	public static CreativeTabs tabInduction = new InductionTab(CreativeTabs.getNextID(), "Rotational Induction");

	public static final Block[] blocks = new Block[InductionBlocks.blockList.length];
	public static final Item[] items = new Item[InductionItems.itemList.length];
	public static final ControlledConfig config = new ControlledConfig(instance, InductionOptions.optionList, InductionBlocks.blockList, InductionItems.itemList, null, 0);

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.RotationalInduction.InductionClient", serverSide="Reika.RotationalInduction.InductionCommon")
	public static InductionCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		MinecraftForge.EVENT_BUS.register(this);

		logger = new ModLogger(instance, true, false, false);
		proxy.registerSounds();

		this.addBlocks();
		this.addItems();
		InductionTiles.loadMappings();

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, InductionBlocks.blockList, blocks);
		for (int i = 0; i < InductionTiles.TEList.length; i++) {
			GameRegistry.registerTileEntity(InductionTiles.TEList[i].getTEClass(), "Induction"+InductionTiles.TEList[i].getName());
			ReikaJavaLibrary.initClass(InductionTiles.TEList[i].getTEClass());
		}
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, InductionItems.itemList, items);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		ReactorRecipes.addRecipes();
		GameRegistry.registerWorldGenerator(new InductionOreGenerator());

		TickRegistry.registerTickHandler(new NetworkTicker(), Side.SERVER);

		IntegrityChecker.instance.addMod(instance, InductionBlocks.blockList, InductionItems.itemList);

		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType wire = WireType.wireList[i];
			wire.addCrafting();
		}
		for (int i = 0; i < InductionOres.oreList.length; i++) {
			InductionOres ore = InductionOres.oreList[i];
			ReikaRecipeHelper.addSmelting(ore.getOreBlock(), ore.getProduct(), ore.xpDropped);
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {

	}

	@Override
	public String getDisplayName() {
		return "Rotational Induction";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return null;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}
}
