/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import java.net.URL;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.API.AcceleratorBlacklist;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.CreativeTabSorter;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;
import Reika.DragonAPI.ModInteract.ReikaThaumHelper;
import Reika.ElectriCraft.Auxiliary.ElectriTab;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriOptions;
import Reika.ElectriCraft.Registry.ElectriOres;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.RotaryCraft;
import WayofTime.alchemicalWizardry.api.event.TeleposeEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod( modid = "ElectriCraft", name="ElectriCraft", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI;required-after:RotaryCraft")

public class ElectriCraft extends DragonAPIMod {

	public static final String packetChannel = "ElectriCraftData";

	@Instance("ElectriCraft")
	public static ElectriCraft instance = new ElectriCraft();

	public static CreativeTabs tabElectri = new ElectriTab("ElectriCraft");

	public static final Block[] blocks = new Block[ElectriBlocks.blockList.length];
	public static final Item[] items = new Item[ElectriItems.itemList.length];
	public static final ElectriConfig config = new ElectriConfig(instance, ElectriOptions.optionList, null, 0);

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.ElectriCraft.ElectriClient", serverSide="Reika.ElectriCraft.ElectriCommon")
	public static ElectriCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		logger = new ModLogger(instance, false);
		proxy.registerSounds();

		this.addBlocks();
		this.addItems();
		ElectriTiles.loadMappings();

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ElectriPacketCore());

		CreativeTabSorter.instance.registerCreativeTabAfter(tabElectri, RotaryCraft.tabRotary);

		FMLInterModComms.sendMessage("zzzzzcustomconfigs", "blacklist-mod-as-output", this.getModContainer().getModId());

		this.basicSetup(evt);
		this.finishTiming();
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
	protected HashMap<String, String> getDependencies() {
		HashMap map = new HashMap();
		map.put("RotaryCraft", RotaryCraft.currentVersion);
		return map;
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
		proxy.registerRenderers();
		GameRegistry.registerWorldGenerator(new ElectriOreGenerator(), 0);

		TickRegistry.instance.registerTickHandler(new NetworkTicker(), Side.SERVER);

		IntegrityChecker.instance.addMod(instance, ElectriBlocks.blockList, ElectriItems.itemList);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ElectriGuiHandler());

		ElectriRecipes.loadOreDict();
		ElectriRecipes.addRecipes();

		if (ModList.NEI.isLoaded()) {
			NEI_DragonAPI_Config.hideBlocks(blocks);
		}

		ReikaEEHelper.blacklistRegistry(ElectriBlocks.blockList);
		ReikaEEHelper.blacklistRegistry(ElectriItems.itemList);

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);
		if (ModList.CHROMATICRAFT.isLoaded()) {
			for (int i = 0; i < ElectriTiles.TEList.length; i++) {
				ElectriTiles m = ElectriTiles.TEList[i];
				AcceleratorBlacklist.addBlacklist(m.getTEClass(), m.getName(), BlacklistReason.EXPLOIT);
			}
		}

		if (ModList.THAUMCRAFT.isLoaded()) {
			for (int i = 0; i < ElectriOres.oreList.length; i++) {
				ElectriOres ore = ElectriOres.oreList[i];
				ItemStack block = ore.getOreBlock();
				ItemStack drop = ore.getProduct();
				//ReikaThaumHelper.addAspects(block, Aspect.STONE, 1);
			}

			ReikaThaumHelper.addAspects(ElectriOres.PLATINUM.getOreBlock(), Aspect.GREED, 6, Aspect.METAL, 2);
			ReikaThaumHelper.addAspects(ElectriOres.NICKEL.getOreBlock(), Aspect.METAL, 1);

			ReikaThaumHelper.addAspects(ElectriOres.PLATINUM.getProduct(), Aspect.GREED, 6, Aspect.METAL, 2);
			ReikaThaumHelper.addAspects(ElectriOres.NICKEL.getProduct(), Aspect.METAL, 2);
		}
		this.finishTiming();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	public void noTelepose(TeleposeEvent evt) {
		if (evt.getInitialTile() instanceof NetworkTileEntity || evt.getFinalTile() instanceof NetworkTileEntity)
			evt.setCanceled(true);
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
