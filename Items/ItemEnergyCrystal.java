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

import net.minecraft.item.ItemStack;
import Reika.ElectriCraft.Base.ElectriItemBase;
import Reika.ElectriCraft.Registry.BatteryType;


public class ItemEnergyCrystal extends ElectriItemBase {

	public ItemEnergyCrystal(int tex) {
		super(tex);
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return is.getItemDamage() >= BatteryType.batteryList.length-1;
	}

}
