package Reika.ElectriCraft.Auxiliary;

import net.minecraft.item.ItemStack;

public interface BatteryTile {

	public String getDisplayEnergy();
	public long getStoredEnergy();

	public long getMaxEnergy();
	public String getFormattedCapacity();

	public void setEnergyFromNBT(ItemStack is);

}
