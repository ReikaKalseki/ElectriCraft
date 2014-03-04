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
import Reika.RotaryCraft.API.ShaftPowerEmitter;
import Reika.RotationalInduction.Base.NetworkTileEntity;
import Reika.RotationalInduction.Registry.InductionTiles;

public class TileEntityMotor extends NetworkTileEntity implements ShaftPowerEmitter {

	private int omega;
	private int torque;
	private long power;

	private int iotick;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		omega = network.getCurrentPerOutput();
		torque = network.getNetworkVoltage();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getIndex() {
		return InductionTiles.MOTOR.ordinal();
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public int getVoltageLimit() {
		return 0;
	}

	@Override
	public int getCurrentLimit() {
		return 0;
	}

	@Override
	public void overCurrent() {

	}

	@Override
	public void overVoltage() {

	}

	@Override
	public int getOmega() {
		return omega;
	}

	@Override
	public int getTorque() {
		return torque;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public boolean canWriteToBlock(int x, int y, int z) {
		return Math.abs(x-xCoord) + Math.abs(y-yCoord) + Math.abs(z-zCoord) == 1;
	}

	@Override
	public boolean isEmitting() {
		return true;
	}
}
