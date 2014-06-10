/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.ElectriCraft.Auxiliary.ElectriStacks;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.DifficultyEffects;
import cpw.mods.fml.common.registry.GameRegistry;

public enum WireType {

	STEEL(			16, 				64, ItemStacks.steelingot),
	TIN(			64, 				32, ElectriStacks.tinIngot, ModOreList.TIN, ModOreList.NETHERTIN),
	NICKEL(			256, 				16, ElectriStacks.nickelIngot, ModOreList.NICKEL, ModOreList.NETHERNICKEL),
	ALUMINUM(		1024, 				8, 	ElectriStacks.aluminumIngot, ModOreList.ALUMINUM),
	COPPER(			4096, 				2, 	ElectriStacks.copperIngot, ModOreList.COPPER, ModOreList.NETHERCOPPER),
	SILVER(			32768, 				1, 	ElectriStacks.silverIngot, ModOreList.SILVER, ModOreList.NETHERSILVER),
	GOLD(			65536, 				4, 	new ItemStack(Item.ingotGold)),
	PLATINUM(		131072, 			16, ElectriStacks.platinumIngot, ModOreList.PLATINUM, ModOreList.NETHERPLATINUM),
	SUPERCONDUCTOR(	Integer.MAX_VALUE, 	0, 	null);

	private final ItemStack material;
	private final String[] oreTypes;

	public final int maxCurrent;
	public final int resistance;

	public static final int INS_OFFSET = 16;

	public static final WireType[] wireList = values();

	private WireType(int max, int res, ItemStack mat, ModOreList... ores) {
		material = mat;
		resistance = res;
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

	public static WireType getTypeFromWireDamage(int dmg) {
		return wireList[dmg%WireType.INS_OFFSET];
	}

	public String getIconTexture() {
		return this.name().toLowerCase();
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
			li.addAll(OreDictionary.getOres(s));
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
		for (int i = 0; i < li.size(); i++) {
			ItemStack in = li.get(i);
			Object[] obj2 = {"WIW", "WIW", "WIW", 'W', Block.cloth, 'I', in};
			Object[] obj = {"I", "I", "I", 'I', in};
			WorktableRecipes.getInstance().addRecipe(is, obj);
			WorktableRecipes.getInstance().addRecipe(is2, obj2);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
				GameRegistry.addRecipe(is2, obj2);
			}
		}
	}

}
