/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import Reika.ElectriCraft.Registry.ElectriTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ElectriTab extends CreativeTabs {

	public ElectriTab(int position, String tabID) {
		super(position, tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return ElectriTiles.WIRE.getCraftedProduct();
	}

	@Override
	public String getTranslatedTabLabel() {
		return "Rotational Electri";
	}

}
