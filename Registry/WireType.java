/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.ElectriCraft.Auxiliary.ElectriStacks;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipeHandler.RecipeLevel;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.DifficultyEffects;
import cpw.mods.fml.common.registry.GameRegistry;

public enum WireType {

	STEEL(			16, 				64, ItemStacks.steelingot),
	TIN(			64, 				32, ElectriStacks.tinIngot, ModOreList.TIN),
	NICKEL(			256, 				16, ElectriStacks.nickelIngot, ModOreList.NICKEL),
	ALUMINUM(		1024, 				8, 	ElectriStacks.aluminumIngot, ModOreList.ALUMINUM),
	COPPER(			4096, 				2, 	ElectriStacks.copperIngot, ModOreList.COPPER),
	SILVER(			32768, 				1, 	ElectriStacks.silverIngot, ModOreList.SILVER),
	GOLD(			65536, 				4, 	new ItemStack(Items.gold_ingot)),
	PLATINUM(		131072, 			16, ElectriStacks.platinumIngot, ModOreList.PLATINUM),
	SUPERCONDUCTOR(	Integer.MAX_VALUE, 	0, 	null);

	private final ItemStack material;
	private final String[] oreTypes;

	public final int maxCurrent;
	public final int resistance;

	public static final int INS_OFFSET = 16;

	public static final WireType[] wireList = values();

	private WireType(int max, int res, ItemStack mat, ModOreList... ores) {
		if (mat != null && mat.getItem() == null)
			throw new IllegalArgumentException("Null wire item!");
		material = mat;
		resistance = res;
		maxCurrent = max;
		ArrayList<String> li = new ArrayList();
		if (ores != null) {
			for (int i = 0; i < ores.length; i++) {
				String s = ores[i].getProductOreDictName();
				li.add(s);
			}
		}
		oreTypes = new String[li.size()];
		for (int i = 0; i < li.size(); i++) {
			oreTypes[i] = li.get(i);
		}
	}

	public static WireType getTypeFromWireDamage(int dmg) {
		return wireList[dmg%WireType.INS_OFFSET];
	}

	public String getIconTexture() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	public ItemStack getCraftedProduct() {
		return ElectriItems.WIRE.getStackOfMetadata(this.ordinal());
	}

	public ItemStack getCraftedInsulatedProduct() {
		return ElectriItems.WIRE.getStackOfMetadata(this.ordinal()+INS_OFFSET);
	}

	private ArrayList<ItemStack> getAllValidCraftingIngots() {
		ArrayList<ItemStack> li = new ArrayList();
		li.add(material);
		for (int i = 0; i < oreTypes.length; i++) {
			String s = oreTypes[i];
			ArrayList<ItemStack> li2 = OreDictionary.getOres(s);
			for (ItemStack is2 : li2) {
				if (!ReikaItemHelper.collectionContainsItemStack(li, is2))
					li.add(is2);
			}
		}
		return li;
	}

	public void addCrafting() {
		if (material == null)
			return;
		int amt = DifficultyEffects.PIPECRAFT.getInt();
		ItemStack is = ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), amt);
		ItemStack is2 = ReikaItemHelper.getSizedItemStack(this.getCraftedInsulatedProduct(), amt);
		ArrayList<ItemStack> li = this.getAllValidCraftingIngots();
		for (ItemStack in : li) {
			Object[] obj2 = {"WIW", "WIW", "WIW", 'W', Blocks.wool, 'I', in};
			Object[] obj = {"I", "I", "I", 'I', in};
			WorktableRecipes.getInstance().addRecipe(is, RecipeLevel.CORE, obj);
			WorktableRecipes.getInstance().addRecipe(is2, RecipeLevel.CORE, obj2);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
				GameRegistry.addRecipe(is2, obj2);
			}
		}
	}

	public static String getLimitsForDisplay() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType type = WireType.wireList[i];
			if (type.resistance > 0) {
				sb.append(ReikaStringParser.capFirstChar(type.name())+" - Resistance: "+type.resistance+" V/m;   Limit: "+type.maxCurrent+" A");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public boolean hasGlowLayer() {
		return this == SUPERCONDUCTOR;
	}

}
