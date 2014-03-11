/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.TileEntities;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.ElectroCraft.Base.ConverterTile;
import Reika.ElectroCraft.Network.WireNetwork;
import Reika.ElectroCraft.Registry.ElectroTiles;

public class TileEntityGenerator extends ConverterTile implements ShaftPowerReceiver {

	private int lastomega;
	private int lasttorque;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote)
			if (omega != lastomega || torque != lasttorque)
				network.updateWires();

		lastomega = omega;
		lasttorque = torque;
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	@Override
	public int getIndex() {
		return ElectroTiles.GENERATOR.ordinal();
	}

	public int getGenVoltage() {
		return omega*WireNetwork.TORQUE_PER_AMP;
	}

	public int getGenCurrent() {
		return torque/WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing().getOpposite();
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFromBlock(int x, int y, int z) {
		ForgeDirection dir = this.getFacing();
		return x == xCoord+dir.offsetX && y == yCoord && z == zCoord+dir.offsetZ;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
		network.updateWires();
	}
}
