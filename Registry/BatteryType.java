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

import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipeHandler.RecipeLevel;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

	private IIcon icon;

	private BatteryType(int cap, int v, int a) {
		maxCapacity = ReikaMathLibrary.longpow(2, cap);
		outputCurrent = a;
		outputVoltage = v;
	}

	public static final BatteryType[] batteryList = values();

	public String getName() {
		return StatCollector.translateToLocal("battery."+this.name().toLowerCase(Locale.ENGLISH));
	}

	public String getFormattedCapacity() {
		double base = ReikaMathLibrary.getThousandBase(maxCapacity);
		String exp = ReikaEngLibrary.getSIPrefix(maxCapacity);
		return String.format("%.3f%sJ", base, exp);
	}

	public void addCrafting() {
		ItemStack is = this.getCraftedProduct();
		ItemStack in = ElectriItems.CRYSTAL.getStackOfMetadata(this.ordinal());
		Object[] obj = {"ScS", "WCW", "SPS", 'W', Blocks.wool, 'c', this.getTopMaterial(), 'C', in, 'P', this.getBottomMaterial(), 'S', ItemStacks.steelingot};
		ShapedOreRecipe ir = new ShapedOreRecipe(is, obj);
		WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
		if (ConfigRegistry.TABLEMACHINES.getState()) {
			GameRegistry.addRecipe(ir);
		}
	}

	private Object getBottomMaterial() {
		switch(this) {
			case STAR:
				return ItemStacks.bedingot;
			case DIAMOND:
				return ItemStacks.tungsteningot;
			case LAPIS:
				return ItemStacks.silumin;
			default:
				return ItemStacks.basepanel;
		}
	}

	private Object getTopMaterial() {
		switch(this) {
			case GLOWSTONE:
			case LAPIS:
				return "ingotSilver";
			case ENDER:
			case STAR:
			case DIAMOND:
				return ItemStacks.redgoldingot;
			default:
				return "ingotCopper";
		}
	}

	public ItemStack getCraftedProduct() {
		return ElectriItems.BATTERY.getStackOfMetadata(this.ordinal());
	}

	@SideOnly(Side.CLIENT)
	public void loadIcon(IIconRegister ico) {
		icon = ico.registerIcon("electricraft:battery/"+this.name().toLowerCase(Locale.ENGLISH)+"_glow");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getGlowingIcon() {
		return icon;
	}

	public static String getDataForDisplay() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < batteryList.length; i++) {
			BatteryType type = batteryList[i];
			sb.append(type.getName()+" - Capacity: "+type.getFormattedCapacity()+"; Output: "+type.outputCurrent+"A @ "+type.outputVoltage+"V");
			sb.append("\n");
		}
		return sb.toString();
	}

}
