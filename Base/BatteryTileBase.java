package Reika.ElectriCraft.Base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ElectriCraft.Auxiliary.BatteryTracker;
import Reika.ElectriCraft.Auxiliary.Interfaces.BatteryTile;

public abstract class BatteryTileBase extends ElectriTileEntity implements BatteryTile {

	private final BatteryTracker tracker = new BatteryTracker();

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		tracker.update(this);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public final BatteryTracker getTracker() {
		return tracker;
	}

	@Override
	public final int getRedstoneOverride() {
		//return (int)Math.round(15D*ReikaMathLibrary.logbase(this.getStoredEnergy(), 2)/ReikaMathLibrary.logbase(this.getMaxEnergy(), 2));
		return (int)Math.round(15D*Math.pow(this.getStoredEnergy()/(double)this.getMaxEnergy(), 0.1));
	}

	public final void setEnergyFromNBT(ItemStack is) {
		if (is.getItem() == this.getPlacerItem()) {
			if (is.stackTagCompound != null)
				this.setEnergy(is.stackTagCompound.getLong("nrg"));
			else
				this.setEnergy(0);
		}
		else {
			this.setEnergy(0);
		}
	}

	protected abstract Item getPlacerItem();

	protected abstract void setEnergy(long val);
}
