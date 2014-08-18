/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Network;

import Reika.ElectriCraft.Base.WiringTile;

import java.util.ArrayList;

import net.minecraftforge.common.util.ForgeDirection;

public final class NetworkNode {

	public final int x;
	public final int y;
	public final int z;

	private final WiringTile wire;

	private final WireNetwork network;

	private final ArrayList<ForgeDirection> connections = new ArrayList();

	public NetworkNode(WireNetwork w, WiringTile te, ArrayList sides) {
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof NetworkNode) {
			NetworkNode n = (NetworkNode)o;
			return n.wire == wire && n.network == network && n.connections.equals(connections);
		}
		return false;
	}

	public ArrayList<ForgeDirection> getDirections() {
		ArrayList<ForgeDirection> li = new ArrayList();
		li.addAll(connections);
		return li;
	}

}
