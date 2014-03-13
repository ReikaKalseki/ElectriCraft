package Reika.ElectriCraft.Base;

import net.minecraftforge.common.ForgeDirection;

public abstract class TileEntityWireComponent extends WiringTile {

	private ForgeDirection facing;

	@Override
	public final boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing() || dir == this.getFacing().getOpposite();
	}

	public final ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	public final void setFacing(ForgeDirection dir) {
		facing = dir;
	}

}
