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

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public enum InductionCrafting {

	EMPTY();

	public final String itemName;

	public static final InductionCrafting[] partList = values();

	private InductionCrafting() {
		itemName = StatCollector.translateToLocal("crafting."+this.name().toLowerCase());
	}

	public ItemStack getItem() {
		return InductionItems.CRAFTING.getStackOfMetadata(this.ordinal());
	}

	public void addRecipe(Object... o) {
		GameRegistry.addRecipe(this.getItem(), o);
	}

	public void addSizedRecipe(int size, Object... o) {
		GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getItem(), size), o);
	}

	public void addShapelessRecipe(Object... o) {
		GameRegistry.addShapelessRecipe(this.getItem(), o);
	}

}
