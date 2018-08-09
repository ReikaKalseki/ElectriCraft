package Reika.ElectriCraft.Auxiliary;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ElectriCraft.API.WrappableWireSource;
import Reika.ElectriCraft.Auxiliary.Interfaces.WireEmitter;
import Reika.ElectriCraft.Network.WireNetwork;


public final class WrappedSource implements WireEmitter {

	private final WrappableWireSource source;

	public WrappedSource(WrappableWireSource src) {
		source = src;
		if (!(src instanceof TileEntity)) {
			throw new IllegalArgumentException("You cannot wrap non-tile sources!");
		}
		if (!src.getClass().getName().startsWith("Reika"))
			throw new IllegalArgumentException("This class is for internal use only!");
	}

	@Override
	public void findAndJoinNetwork(World world, int x, int y, int z) {}
	@Override
	public WireNetwork getNetwork() {return null;}
	@Override
	public void setNetwork(WireNetwork n) {}
	@Override
	public void removeFromNetwork() {}
	@Override
	public void resetNetwork() {}
	@Override
	public void onNetworkChanged() {}

	@Override
	public boolean isConnectable() {
		return this.canEmitPower();
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return this.canEmitPowerToSide(dir);
	}

	@Override
	public World getWorld() {
		return ((TileEntity)source).worldObj;
	}

	@Override
	public int getX() {
		return ((TileEntity)source).xCoord;
	}

	@Override
	public int getY() {
		return ((TileEntity)source).yCoord;
	}

	@Override
	public int getZ() {
		return ((TileEntity)source).zCoord;
	}

	@Override
	public int getGenVoltage() {
		return source.getOmega()*WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public int getGenCurrent() {
		return source.getTorque()/WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public boolean canEmitPowerToSide(ForgeDirection dir) {
		return source.canConnectToSide(dir);
	}

	@Override
	public boolean canEmitPower() {
		return source.isFunctional();
	}

	public boolean needsUpdate() {
		return source.hasPowerStatusChangedSinceLastTick();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WrappedSource && ((WrappedSource)o).source.equals(source);
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

}
