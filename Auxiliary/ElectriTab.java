/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.GUI.RegistryEnumCreativeTab;
import Reika.ElectriCraft.Registry.ElectriTiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ElectriTab extends RegistryEnumCreativeTab {

	public ElectriTab(String tabID) {
		super(tabID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return ElectriTiles.WIRE.getCraftedProduct();
	}

}
