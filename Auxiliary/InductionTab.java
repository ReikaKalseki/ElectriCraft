/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Auxiliary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import Reika.RotationalInduction.Registry.InductionTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InductionTab extends CreativeTabs {

	public InductionTab(int position, String tabID) {
		super(position, tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return InductionTiles.WIRE.getCraftedProduct();
	}

	@Override
	public String getTranslatedTabLabel() {
		return "Rotational Induction";
	}

}
