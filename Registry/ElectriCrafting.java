/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public enum ElectriCrafting {

	BLUEDUST("dustLapis"),
	DIAMONDDUST("dustDiamond"),
	QUARTZDUST("dustNetherQuartz"),
	CRYSTALDUST();

	public static final ElectriCrafting[] craftingList = values();

	public final String oreDictName;

	private ElectriCrafting() {
		this(null);
	}

	private ElectriCrafting(String dict) {
		oreDictName = dict;
	}

	public ItemStack getItem() {
		return ElectriItems.CRAFTING.getStackOfMetadata(this.ordinal());
	}

	public boolean hasOreName() {
		return oreDictName != null && !oreDictName.isEmpty();
	}

	public String getName() {
		return StatCollector.translateToLocal("electricrafting."+this.name().toLowerCase(Locale.ENGLISH));
	}

	public ItemStack getItem(int amt) {
		return ReikaItemHelper.getSizedItemStack(this.getItem(), amt);
	}
}
