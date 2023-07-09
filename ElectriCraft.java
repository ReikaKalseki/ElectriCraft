/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;

import Reika.ChromatiCraft.API.AdjacencyUpgradeAPI.BlacklistReason;
import Reika.ChromatiCraft.API.ChromatiAPI;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.CreativeTabSorter;
import Reika.DragonAPI.Auxiliary.NEI_DragonAPI_Config;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.PackModificationTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Base.EnumOreBlock;
import Reika.DragonAPI.Instantiable.Event.TileEntityMoveEvent;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.ItemStackRepository;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.SensitiveItemRegistry;
import Reika.DragonAPI.ModInteract.DeepInteract.TimeTorchHelper;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ElectriCraft.Auxiliary.ElectriBookTracker;
import Reika.ElectriCraft.Auxiliary.ElectriDescriptions;
import Reika.ElectriCraft.Auxiliary.ElectriStacks;
import Reika.ElectriCraft.Auxiliary.ElectriTab;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriOptions;
import Reika.ElectriCraft.Registry.ElectriOres;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.World.ElectriOreGenerator;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Registry.ConfigRegistry;

import WayofTime.alchemicalWizardry.api.event.TeleposeEvent;
import cpw.mods.fml.common.FMLCommonHandler;
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
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

@Mod( modid = "ElectriCraft", name="ElectriCraft", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI;required-after:RotaryCraft")

public class ElectriCraft extends DragonAPIMod {

	public static final String packetChannel = "ElectriCraftData";

	@Instance("ElectriCraft")
	public static ElectriCraft instance = new ElectriCraft();

	public static CreativeTabs tabElectri = new ElectriTab("ElectriCraft");

	public static final Block[] blocks = new Block[ElectriBlocks.blockList.length];
	public static final Item[] items = new Item[ElectriItems.itemList.length];
	public static final ElectriConfig config = new ElectriConfig(instance, ElectriOptions.optionList, null);

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
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");
		proxy.registerSounds();

		this.addBlocks();
		this.addItems();
		((EnumOreBlock)ElectriBlocks.ORE.getBlockInstance()).register();
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
			ReikaJavaLibrary.initClass(ElectriTiles.TEList[i].getTEClass(), true);
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
		RetroGenController.instance.addHybridGenerator(ElectriOreGenerator.instance, 0);

		ItemStackRepository.instance.registerClass(this, ElectriStacks.class);

		TickRegistry.instance.registerTickHandler(ElectriNetworkManager.instance);

		IntegrityChecker.instance.addMod(instance, ElectriBlocks.blockList, ElectriItems.itemList);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ElectriGuiHandler());

		if (ConfigRegistry.HANDBOOK.getState())
			PlayerFirstTimeTracker.addTracker(new ElectriBookTracker());

		ElectriRecipes.loadOreDict();
		ElectriRecipes.addRecipes();

		if (ModList.NEI.isLoaded()) {
			for (ElectriBlocks block : ElectriBlocks.blockList)
				if (block != ElectriBlocks.ORE)
					NEI_DragonAPI_Config.hideBlock(block.getBlockInstance());
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ElectriDescriptions.loadData();

		PackModificationTracker.instance.addMod(this, config);

		FMLInterModComms.sendMessage("Randomod", "blacklist", this.getModContainer().getModId());

		//ReikaEEHelper.blacklistRegistry(ElectriBlocks.blockList);
		//ReikaEEHelper.blacklistRegistry(ElectriItems.itemList);

		SensitiveItemRegistry.instance.registerItem(this, ElectriItems.PLACER.getItemInstance(), true);
		SensitiveItemRegistry.instance.registerItem(this, ElectriItems.BATTERY.getItemInstance(), true);
		SensitiveItemRegistry.instance.registerItem(this, ElectriItems.RFBATTERY.getItemInstance(), true);

		SensitiveItemRegistry.instance.registerItem(this, WireType.SUPERCONDUCTOR.getCraftedProduct(), true);
		SensitiveItemRegistry.instance.registerItem(this, WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct(), true);

		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);
		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
			ElectriTiles m = ElectriTiles.TEList[i];
			if (ModList.CHROMATICRAFT.isLoaded()) {
				if (m == ElectriTiles.WIRE) {
					for (WireType mat : WireType.wireList) {
						ChromatiAPI.getAPI().adjacency().addAcceleratorBlacklist(m.getTEClass(), m.getName(), mat.getCraftedProduct(), BlacklistReason.EXPLOIT);
						ChromatiAPI.getAPI().adjacency().addAcceleratorBlacklist(m.getTEClass(), m.getName(), mat.getCraftedInsulatedProduct(), BlacklistReason.EXPLOIT);
					}
				}
				else if (m == ElectriTiles.BATTERY) {
					for (BatteryType mat : BatteryType.batteryList)
						ChromatiAPI.getAPI().adjacency().addAcceleratorBlacklist(m.getTEClass(), m.getName(), mat.getCraftedProduct(), BlacklistReason.EXPLOIT);
				}
				else {
					ChromatiAPI.getAPI().adjacency().addAcceleratorBlacklist(m.getTEClass(), m.getName(), m.getCraftedProduct(), BlacklistReason.EXPLOIT);
				}
			}
			TimeTorchHelper.blacklistTileEntity(m.getTEClass());
		}

		LuaMethod.registerMethods("Reika.ElectriCraft.Auxiliary.Lua");
		ElectriRecipes.addPostLoadRecipes();

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

	@SubscribeEvent
	public void cancelFramez(TileEntityMoveEvent evt) {
		if (!this.isMovable(evt.tile)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void loadTextures(TextureStitchEvent.Pre evt) {
		if (evt.map.getTextureType() == 0) {
			for (int i = 0; i < BatteryType.batteryList.length; i++) {
				BatteryType type = BatteryType.batteryList[i];
				type.loadIcon(evt.map);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	@ClassDependent("WayofTime.alchemicalWizardry.api.event.TeleposeEvent")
	public void noTelepose(TeleposeEvent evt) {
		if (!this.isMovable(evt.getInitialTile()) || !this.isMovable(evt.getFinalTile()))
			evt.setCanceled(true);
	}

	private boolean isMovable(TileEntity te) {
		return !(te instanceof NetworkTileEntity);
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
	public URL getBugSite() {
		return DragonAPICore.getReikaGithubPage();
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

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}
}
