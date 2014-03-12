package Reika.ElectriCraft.Base;


public abstract class WiringTile extends NetworkTileEntity {

	public abstract int getResistance();

	public abstract void onNetworkChanged();

}
