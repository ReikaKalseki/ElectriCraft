/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriItemBase;

public class ItemElectriBook extends ElectriItemBase {

	public ItemElectriBook(int tex) {
		super(tex);
		maxStackSize = 1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer ep)
	{
		ep.openGui(ElectriCraft.instance, 10, world, 0, 0, 0);
		return itemstack;
	}

}
