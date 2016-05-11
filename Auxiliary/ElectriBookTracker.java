/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import net.minecraft.item.ItemStack;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.RotaryCraft.Auxiliary.HandbookTracker;

public class ElectriBookTracker extends HandbookTracker {

	@Override
	public ItemStack getItem() {
		return ElectriItems.BOOK.getStackOf();
	}

	@Override
	public String getID() {
		return "ElectriCraft_Handbook";
	}

}
