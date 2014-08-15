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

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ThermalRecipeHelper;
import Reika.ElectriCraft.Items.ItemWirePlacer;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriCrafting;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriOres;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;
import Reika.RotaryCraft.API.GrinderAPI;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.BlockRegistry;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.DifficultyEffects;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class ElectriRecipes {

	public static void loadOreDict() {
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			ElectriOres ore = ElectriOres.oreList[i];
			OreDictionary.registerOre(ore.getDictionaryName(), ore.getOreBlock());
			OreDictionary.registerOre(ore.getProductDictionaryName(), ore.getProduct());
		}

		for (int i = 0; i < ElectriCrafting.craftingList.length; i++) {
			ElectriCrafting c = ElectriCrafting.craftingList[i];
			if (c.hasOreName()) {
				ItemStack is = c.getItem();
				String s = c.oreDictName;
				OreDictionary.registerOre(s, is);
			}
		}

		OreDictionary.registerOre("dustGlowstone", Items.glowstone_dust);
	}

	public static void addRecipes() {
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType wire = WireType.wireList[i];
			wire.addCrafting();
		}
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			BatteryType bat = BatteryType.batteryList[i];
			bat.addCrafting();
		}
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			ElectriOres ore = ElectriOres.oreList[i];
			ReikaRecipeHelper.addSmelting(ore.getOreBlock(), ore.getProduct(), ore.xpDropped);
		}

		GrinderAPI.addRecipe(new ItemStack(Items.diamond), ElectriCrafting.DIAMONDDUST.getItem());
		GrinderAPI.addRecipe(ReikaItemHelper.lapisDye, ElectriCrafting.BLUEDUST.getItem());
		GrinderAPI.addRecipe(new ItemStack(Items.quartz), ElectriCrafting.QUARTZDUST.getItem());

		ReikaRecipeHelper.addSmelting(ElectriCrafting.CRYSTALDUST.getItem(), ElectriItems.CRYSTAL.getStackOf(), 1F);
		GameRegistry.addRecipe(new ShapelessOreRecipe(ElectriCrafting.CRYSTALDUST.getItem(2), ElectriCrafting.BLUEDUST.oreDictName, ElectriCrafting.DIAMONDDUST.oreDictName, ElectriCrafting.QUARTZDUST.oreDictName, "dustGlowstone"));

		Object[] ctr = {Blocks.glowstone, Blocks.lapis_block, Items.ender_eye, Items.diamond, Items.nether_star};
		for (int i = 1; i < 6; i++) {
			ItemStack cry = ElectriItems.CRYSTAL.getStackOfMetadata(i-1);
			ItemStack is = ElectriItems.CRYSTAL.getStackOfMetadata(i);
			GameRegistry.addRecipe(is, "RCR", "CIC", "RCR", 'R', Items.redstone, 'C', cry, 'I', ctr[i-1]);
		}

		ItemStack w = ReikaItemHelper.getSizedItemStack(WireType.SUPERCONDUCTOR.getCraftedProduct(), DifficultyEffects.PIPECRAFT.getInt()/4);
		ItemStack w2 = ReikaItemHelper.getSizedItemStack(WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct(), 3);
		ShapedOreRecipe ir = new ShapedOreRecipe(w, "IGI", "SRS", "IgI", 'I', ItemStacks.steelingot, 'G', Blocks.glass, 'S', "ingotSilver", 'g', "ingotGold", 'R', Items.redstone);
		Object[] obj2 = {"WwW", "WwW", "WwW", 'W', Blocks.wool, 'w', w};
		WorktableRecipes.getInstance().addRecipe(ir);
		WorktableRecipes.getInstance().addRecipe(w2, obj2);
		if (ConfigRegistry.TABLEMACHINES.getState()) {
			GameRegistry.addRecipe(ir);
			GameRegistry.addRecipe(w2, obj2);
		}

		ElectriTiles.GENERATOR.addOreCrafting("gts", "iGn", "ppp", 'n', "ingotNickel", 't', "ingotTin", 'p', ItemStacks.basepanel, 'g', "ingotCopper", 's', ItemStacks.steelingot, 'G', ItemStacks.generator, 'i', ItemStacks.impeller);
		ElectriTiles.MOTOR.addOreCrafting("scs", "gCg", "BcB", 'g', ItemStacks.goldcoil, 'c', "ingotCopper", 's', "ingotSilver", 'S', ItemStacks.steelingot, 'B', ItemStacks.basepanel, 'C', ItemStacks.shaftcore);
		ElectriTiles.RELAY.addSizedOreCrafting(4, "SCS", "CPC", 'C', "ingotCopper", 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot);
		ElectriTiles.RESISTOR.addSizedCrafting(4, "SCS", "PCP", 'C', ItemStacks.coaldust, 'S', ItemStacks.steelingot, 'P', ItemStacks.basepanel);

		ElectriTiles.CABLE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "RDR", "BGB", "RER", 'D', Items.diamond, 'R', Blocks.redstone_block, 'G', Blocks.gold_block, 'E', Items.ender_pearl, 'B', BlockRegistry.BLASTGLASS.getStackOf());

		if (ModList.THERMALEXPANSION.isLoaded()) {
			ItemStack is = WireType.SUPERCONDUCTOR.getCraftedProduct();
			ItemStack is2 = WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct();
			ItemWirePlacer item = (ItemWirePlacer)ElectriItems.WIRE.getItemInstance();
			FluidStack f1 = new FluidStack(FluidRegistry.getFluid("liquid nitrogen"), item.getCapacity(is));
			FluidStack f2 = new FluidStack(FluidRegistry.getFluid("cryotheum"), item.getCapacity(is));
			ThermalRecipeHelper.addFluidTransposerFill(is, item.getFilledSuperconductor(false), 200, f1);
			ThermalRecipeHelper.addFluidTransposerFill(is, item.getFilledSuperconductor(false), 200, f2);
			ThermalRecipeHelper.addFluidTransposerFill(is2, item.getFilledSuperconductor(true), 200, f1);
			ThermalRecipeHelper.addFluidTransposerFill(is2, item.getFilledSuperconductor(true), 200, f2);
		}
	}

}