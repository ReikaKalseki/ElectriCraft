package Reika.ElectriCraft.TileEntities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Auxiliary.BatteryTile;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;
import cofh.api.energy.IEnergyHandler;

public class TileEntityRFBattery extends ElectriTileEntity implements BatteryTile, IEnergyHandler {

	private long energy;
	public static final long CAPACITY = 1200000000000L;//1099511627775L;//;

	@Override
	public String getDisplayEnergy() {
		return formatNumber(energy);
	}

	@Override
	public long getStoredEnergy() {
		return energy;
	}

	@Override
	public long getMaxEnergy() {
		return CAPACITY;
	}

	@Override
	public String getFormattedCapacity() {
		return formatNumber(CAPACITY);
	}

	private static String formatNumber(long num) {
		return String.format("%.3f %sRF", ReikaMathLibrary.getThousandBase(num), ReikaEngLibrary.getSIPrefix(num));
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.RFBATTERY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canConnectEnergy(ForgeDirection dir) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection dir, int amt, boolean simulate) {
		long ret = dir != ForgeDirection.UP ? Math.min(amt, CAPACITY-energy) : 0;
		if (ret > 0 && !simulate) {
			energy += ret;
		}
		return (int)ret;
	}

	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean simulate) {
		long ret = dir == ForgeDirection.UP ? Math.min(amt, energy) : 0;
		if (ret > 0 && !simulate) {
			energy -= ret;
		}
		return (int)ret;
	}

	@Override
	public int getEnergyStored(ForgeDirection dir) {
		return (int)this.getStoredEnergy();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection dir) {
		return (int)CAPACITY; //uhhh...
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setLong("e", energy);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		energy = NBT.getLong("e");
	}

	public void setEnergyFromNBT(ItemStack is) {
		if (is.getItem() == ElectriItems.RFBATTERY.getItemInstance()) {
			if (is.stackTagCompound != null)
				energy = is.stackTagCompound.getLong("nrg");
			else
				energy = 0;
		}
		else {
			energy = 0;
		}
	}

}
