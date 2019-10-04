/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary.Interfaces;

import net.minecraft.item.ItemStack;

import Reika.ElectriCraft.Auxiliary.BatteryTracker;

public interface BatteryTile {

	public String getDisplayEnergy();
	public long getStoredEnergy();

	public long getMaxEnergy();
	public String getFormattedCapacity();

	public void setEnergyFromNBT(ItemStack is);

	public int getEnergyColor();
	public String getUnitName();
	public boolean isDecimalSystem();

	public BatteryTracker getTracker();

}
