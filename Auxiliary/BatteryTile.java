package Reika.ElectriCraft.Auxiliary;

public interface BatteryTile {

	public String getDisplayEnergy();
	public long getStoredEnergy();

	public long getMaxEnergy();
	public String getFormattedCapacity();

}
