/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Auxiliary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import Reika.ElectroCraft.Registry.ElectroTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ElectroTab extends CreativeTabs {

	public ElectroTab(int position, String tabID) {
		super(position, tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return ElectroTiles.WIRE.getCraftedProduct();
	}

	@Override
	public String getTranslatedTabLabel() {
		return "Rotational Electro";
	}

}
