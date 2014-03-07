/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotationalInduction.Auxiliary.InductorStacks;
import cpw.mods.fml.common.registry.GameRegistry;

public enum WireType {

	STEEL(0, ItemStacks.steelingot),
	TIN(0, InductorStacks.tinIngot, ModOreList.TIN, ModOreList.NETHERTIN),
	NICKEL(0, InductorStacks.nickelIngot, ModOreList.NICKEL, ModOreList.NETHERNICKEL),
	ALUMINUM(0, InductorStacks.aluminumIngot, ModOreList.ALUMINUM),
	COPPER(0, InductorStacks.copperIngot, ModOreList.COPPER, ModOreList.NETHERCOPPER),
	SILVER(0, InductorStacks.silverIngot, ModOreList.SILVER, ModOreList.NETHERSILVER),
	GOLD(0, new ItemStack(Item.ingotGold)),
	PLATINUM(0, InductorStacks.platinumIngot, ModOreList.PLATINUM, ModOreList.NETHERPLATINUM),
	SUPERCONDUCTOR(Integer.MAX_VALUE, InductorStacks.superconductor);

	private final ItemStack material;
	private final String[] oreTypes;

	public final int maxCurrent;

	public static final int INS_OFFSET = 16;

	public static final WireType[] wireList = values();

	private WireType(int max, ItemStack mat, ModOreList... ores) {
		material = mat;
		maxCurrent = max;
		ArrayList<String> li = new ArrayList();
		if (ores != null) {
			for (int i = 0; i < ores.length; i++) {
				String[] s = ores[i].getOreDictIngots();
				for (int k = 0; k < s.length; k++) {
					li.add(s[k]);
				}
			}
		}
		oreTypes = new String[li.size()];
		for (int i = 0; i < li.size(); i++) {
			oreTypes[i] = li.get(i);
		}
	}

	public String getIconTexture() {
		return this.name().toLowerCase();
	}

	public ItemStack getCraftedProduct() {
		return InductionItems.WIRE.getStackOfMetadata(this.ordinal());
	}

	public ItemStack getCraftedInsulatedProduct() {
		return InductionItems.WIRE.getStackOfMetadata(this.ordinal()+INS_OFFSET);
	}

	private ArrayList<ItemStack> getAllValidCraftingIngots() {
		ArrayList<ItemStack> li = new ArrayList();
		li.add(material);
		for (int i = 0; i < oreTypes.length; i++) {
			String s = oreTypes[i];
			li.addAll(OreDictionary.getOres(s));
		}
		return li;
	}

	public void addCrafting() {
		ItemStack is = ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), 8);
		ItemStack is2 = ReikaItemHelper.getSizedItemStack(this.getCraftedInsulatedProduct(), 8);
		ArrayList<ItemStack> li = this.getAllValidCraftingIngots();
		for (int i = 0; i < li.size(); i++) {
			ItemStack in = li.get(i);
			Object[] obj = {"WWW", "III", "WWW", 'W', Block.cloth, 'I', in};
			Object[] obj2 = {"III", 'I', in};
			WorktableRecipes.getInstance().addRecipe(is, obj2);
			WorktableRecipes.getInstance().addRecipe(is2, obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
				GameRegistry.addRecipe(is2, obj2);
			}
		}
	}

}
