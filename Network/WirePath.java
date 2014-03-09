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

	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	public int getResistance() {
		int r = 0;
		for (int i = 0; i < nodes.size(); i++) {
			TileEntityWire wire = nodes.get(i);
			r += wire.getWireType().resistance;
		}
		return r;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		for (int i = 0; i < nodes.size(); i++) {
			TileEntityWire w = nodes.get(i);
			sb.append("[");
			sb.append(w.xCoord);
			sb.append(":");
			sb.append(w.yCoord);
			sb.append(":");
			sb.append(w.zCoord);
			sb.append("]");
		}
		sb.append(">");
		return sb.toString();
	}

	public boolean containsBlock(int x, int y, int z) {
		for (int i = 0; i < nodes.size(); i++) {
			TileEntityWire w = nodes.get(i);
			if (x == w.xCoord && y == w.yCoord && z == w.zCoord)
				return true;
		}
		return false;
	}

	public boolean isValid() {
		return !this.isEmpty() && this.hasEndPoints();
	}

	private boolean hasEndPoints() {
		return false;
	}

}
