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

import net.minecraft.item.ItemStack;

public interface BatteryTile {

	public String getDisplayEnergy();
	public long getStoredEnergy();

	public long getMaxEnergy();
	public String getFormattedCapacity();

	public void setEnergyFromNBT(ItemStack is);

}
