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

import java.util.LinkedList;

import Reika.RotationalInduction.TileEntities.TileEntityWire;

public final class WirePath {

	private final LinkedList<TileEntityWire> nodes = new LinkedList();

	public WirePath() {

	}

	void addNode(TileEntityWire n) {
		nodes.add(n);
	}

	public int getLength() {
		return nodes.size();
	}

	public int getResistance() {
		int r = 0;
		for (int i = 0; i < nodes.size(); i++) {
			TileEntityWire wire = nodes.get(i);
			r += wire.getWireType().resistance;
		}
		return r;
	}

}
