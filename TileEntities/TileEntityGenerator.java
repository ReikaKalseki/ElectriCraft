/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.TileEntities;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotationalInduction.Auxiliary.WireNetwork;
import Reika.RotationalInduction.Base.ConverterTile;
import Reika.RotationalInduction.Registry.InductionTiles;

public class TileEntityGenerator extends ConverterTile implements ShaftPowerReceiver {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return InductionTiles.GENERATOR.ordinal();
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
	}
}
