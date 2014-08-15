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

import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public enum BatteryType {

	REDSTONE(	20,		256, 		32),
	GLOWSTONE(	24, 	512, 		64),
	LAPIS(		28, 	2048, 		128),
	ENDER(		33, 	4096, 		256),
	DIAMOND(	40, 	16384, 		1024),
	STAR(		48, 	65536,		4096);

	public final long maxCapacity;
	public final int outputVoltage;
	public final int outputCurrent;

	private BatteryType(int cap, int v, int a) {
		maxCapacity = ReikaMathLibrary.longpow(2, cap);
		outputCurrent = a;
		outputVoltage = v;
	}

	public static final BatteryType[] batteryList = values();

	public String getName() {
		return StatCollector.translateToLocal("battery."+this.name().toLowerCase());
	}

	public String getFormattedCapacity() {
		double base = ReikaMathLibrary.getThousandBase(maxCapacity);
		String exp = ReikaEngLibrary.getSIPrefix(maxCapacity);
		return String.format("%.3f%sJ", base, exp);
	}

	public void addCrafting() {
		ItemStack is = this.getCraftedProduct();
		ItemStack in = ElectriItems.CRYSTAL.getStackOfMetadata(this.ordinal());
		Object[] obj = {"ScS", "WCW", "SPS", 'W', Blocks.wool, 'c', "ingotCopper", 'C', in, 'P', ItemStacks.basepanel, 'S', ItemStacks.steelingot};
		ShapedOreRecipe ir = new ShapedOreRecipe(is, obj);
		WorktableRecipes.getInstance().addRecipe(ir);
		if (ConfigRegistry.TABLEMACHINES.getState()) {
			GameRegistry.addRecipe(ir);
		}
	}

	public ItemStack getCraftedProduct() {
		return ElectriItems.BATTERY.getStackOfMetadata(this.ordinal());
	}

}