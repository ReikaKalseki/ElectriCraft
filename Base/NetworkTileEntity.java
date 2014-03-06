/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Base;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.RotationalInduction.Network.WireNetwork;
import cpw.mods.fml.relauncher.Side;

public abstract class NetworkTileEntity extends InductionTileEntity {

	protected WireNetwork network;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTicksExisted() == 0) {
			this.findAndJoinNetwork(world, x, y, z);
			ReikaJavaLibrary.pConsole(network, Side.SERVER);
		}
	}

	public void findAndJoinNetwork(World world, int x, int y, int z) {
		network = new WireNetwork();
		network.addElement(this);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (this.canNetworkOnSide(dir)) {
				TileEntity te = this.getAdjacentTileEntity(dir);
				if (te instanceof NetworkTileEntity) {
					//ReikaJavaLibrary.pConsole(te, Side.SERVER);
					NetworkTileEntity n = (NetworkTileEntity)te;
					if (n.canNetworkOnSide(dir.getOpposite())) {
						WireNetwork w = n.network;
						if (w != null) {
							//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
							w.merge(network);
						}
					}
				}
			}
		}
	}

	public abstract boolean canNetworkOnSide(ForgeDirection dir);

	public final WireNetwork getNetwork() {
		return network;
	}

	public final void setNetwork(WireNetwork n) {
		network = n;
		network.addElement(this);
	}

	public final void removeFromNetwork() {
		network.removeElement(this);
	}

	public final void resetNetwork() {
		network = null;
	}

	public abstract int getVoltageLimit();

	public abstract int getCurrentLimit();

	public abstract void overCurrent();

	public abstract void overVoltage();

}
