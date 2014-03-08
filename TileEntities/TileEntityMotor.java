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
import Reika.RotationalInduction.Base.ConverterTile;
import Reika.RotationalInduction.Network.WireNetwork;
import Reika.RotationalInduction.Registry.InductionTiles;

public class TileEntityMotor extends ConverterTile implements ShaftPowerEmitter {

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		torque = 0;//network.getCurrentPerOutput()*WireNetwork.TORQUE_PER_AMP;
		omega = network.getNetworkVoltage()/WireNetwork.TORQUE_PER_AMP;
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
		return dir == this.getFacing();
	}

	@Override
	public boolean canWriteToBlock(int x, int y, int z) {
		ForgeDirection dir = this.getFacing().getOpposite();
		return x == xCoord+dir.offsetX && y == yCoord && z == zCoord+dir.offsetZ;
	}

	@Override
	public boolean isEmitting() {
		return true;
	}
}
