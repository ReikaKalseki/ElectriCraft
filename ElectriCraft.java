/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.IntegrityChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ThermalRecipeHelper;
import Reika.ElectroCraft.Auxiliary.ElectroTab;
import Reika.ElectroCraft.Items.ItemWirePlacer;
import Reika.ElectroCraft.Registry.ElectroBlocks;
import Reika.ElectroCraft.Registry.ElectroItems;
import Reika.ElectroCraft.Registry.ElectroOptions;
import Reika.ElectroCraft.Registry.ElectroOres;
import Reika.ElectroCraft.Registry.ElectroTiles;
import Reika.ElectroCraft.Registry.WireType;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.DifficultyEffects;
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

@Mod( modid = "ElectroCraft", name="Rotational Electro", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true/*,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ElectroCraftData" }, packetHandler = ClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ElectroCraftData" }, packetHandler = ServerPackets.class)*/)

public class ElectroCraft extends DragonAPIMod {

	@Instance("ElectroCraft")
	public static ElectroCraft instance = new ElectroCraft();

	public static CreativeTabs tabElectro = new ElectroTab(CreativeTabs.getNextID(), "Rotational Electro");

	public static final Block[] blocks = new Block[ElectroBlocks.blockList.length];
	public static final Item[] items = new Item[ElectroItems.itemList.length];
	public static final ControlledConfig config = new ControlledConfig(instance, ElectroOptions.optionList, ElectroBlocks.blockList, ElectroItems.itemList, null, 0);

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.ElectroCraft.ElectroClient", serverSide="Reika.ElectroCraft.ElectroCommon")
	public static ElectroCommon proxy;

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
		ElectroTiles.loadMappings();

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ElectroBlocks.blockList, blocks);
		for (int i = 0; i < ElectroTiles.TEList.length; i++) {
			GameRegistry.registerTileEntity(ElectroTiles.TEList[i].getTEClass(), "Electro"+ElectroTiles.TEList[i].getName());
			ReikaJavaLibrary.initClass(ElectroTiles.TEList[i].getTEClass());
		}
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ElectroItems.itemList, items);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		GameRegistry.registerWorldGenerator(new ElectroOreGenerator());

		TickRegistry.registerTickHandler(new NetworkTicker(), Side.SERVER);

		IntegrityChecker.instance.addMod(instance, ElectroBlocks.blockList, ElectroItems.itemList);

		this.addRecipes();
	}

	private void addRecipes() {
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType wire = WireType.wireList[i];
			wire.addCrafting();
		}
		for (int i = 0; i < ElectroOres.oreList.length; i++) {
			ElectroOres ore = ElectroOres.oreList[i];
			ReikaRecipeHelper.addSmelting(ore.getOreBlock(), ore.getProduct(), ore.xpDropped);
			OreDictionary.registerOre(ore.getDictionaryName(), ore.getOreBlock());
			OreDictionary.registerOre(ore.getProductDictionaryName(), ore.getProduct());
		}

		ItemStack w = ReikaItemHelper.getSizedItemStack(WireType.SUPERCONDUCTOR.getCraftedProduct(), DifficultyEffects.PIPECRAFT.getInt()/4);
		ItemStack w2 = ReikaItemHelper.getSizedItemStack(WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct(), 3);
		ShapedOreRecipe ir = new ShapedOreRecipe(w, "IGI", "SRS", "IgI", 'I', ItemStacks.steelingot, 'G', Block.glass, 'S', "ingotSilver", 'g', "ingotGold", 'R', Item.redstone);
		Object[] obj2 = {"WWW", "www", "WWW", 'W', Block.cloth, 'w', w};
		WorktableRecipes.getInstance().addRecipe(ir);
		WorktableRecipes.getInstance().addRecipe(w2, obj2);
		if (ConfigRegistry.TABLEMACHINES.getState()) {
			GameRegistry.addRecipe(ir);
			GameRegistry.addRecipe(w2, obj2);
		}

		ElectroTiles.GENERATOR.addOreCrafting("gts", "iGn", "ppp", 'n', "ingotNickel", 't', "ingotTin", 'p', ItemStacks.basepanel, 'g', "ingotCopper", 's', ItemStacks.steelingot, 'G', ItemStacks.generator, 'i', ItemStacks.impeller);
		ElectroTiles.MOTOR.addOreCrafting("scs", "gCg", "BcB", 'g', ItemStacks.goldcoil, 'c', "ingotCopper", 's', "ingotSilver", 'S', ItemStacks.steelingot, 'B', ItemStacks.basepanel, 'C', ItemStacks.shaftcore);

		if (ModList.THERMALEXPANSION.isLoaded()) {
			ItemStack is = WireType.SUPERCONDUCTOR.getCraftedProduct();
			ItemStack is2 = WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct();
			ItemWirePlacer item = (ItemWirePlacer)ElectroItems.WIRE.getItemInstance();
			FluidStack f1 = new FluidStack(FluidRegistry.getFluid("liquid nitrogen"), item.getCapacity(is));
			FluidStack f2 = new FluidStack(FluidRegistry.getFluid("cryotheum"), item.getCapacity(is));
			ThermalRecipeHelper.addFluidTransposerFill(is, item.getFilledSuperconductor(false), 200, f1);
			ThermalRecipeHelper.addFluidTransposerFill(is, item.getFilledSuperconductor(false), 200, f2);
			ThermalRecipeHelper.addFluidTransposerFill(is2, item.getFilledSuperconductor(true), 200, f1);
			ThermalRecipeHelper.addFluidTransposerFill(is2, item.getFilledSuperconductor(true), 200, f2);
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {

	}

	@Override
	public String getDisplayName() {
		return "ElectroCraft";
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
