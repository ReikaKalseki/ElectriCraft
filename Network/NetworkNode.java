/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Network;

import java.util.ArrayList;

import net.minecraftforge.common.ForgeDirection;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public final class NetworkNode {

	public final int x;
	public final int y;
	public final int z;

	private final TileEntityWire wire;

	private final WireNetwork network;

	private final ArrayList<ForgeDirection> connections = new ArrayList();

	public NetworkNode(WireNetwork w, TileEntityWire te, ArrayList sides) {
		network = w;
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;

		wire = te;
		connections.addAll(sides);
	}

	public boolean connectsToSide(ForgeDirection dir) {
		return connections.contains(dir);
	}

	@Override
	public String toString() {
		return "<"+x+":"+y+":"+z+">";
	}

}
