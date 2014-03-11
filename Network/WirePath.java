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

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.World;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkTickEvent;
import Reika.ElectriCraft.TileEntities.TileEntityGenerator;
import Reika.ElectriCraft.TileEntities.TileEntityMotor;
import Reika.ElectriCraft.TileEntities.TileEntityWire;

public final class WirePath {

	private final LinkedList<TileEntityWire> nodes = new LinkedList();
	private final TileEntityGenerator start;
	private final TileEntityMotor end;
	private final WireNetwork net;

	public final int resistance;

	public WirePath(World world, LinkedList<List<Integer>> points, TileEntityGenerator start, TileEntityMotor end, WireNetwork net) {
		for (int i = 0; i < points.size(); i++) {
			List<Integer> li = points.get(i);
			int x = li.get(0);
			int y = li.get(1);
			int z = li.get(2);
			TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
			nodes.addLast(te);
		}
		this.start = start;
		this.end = end;
		this.net = net;
		resistance = this.calculateResistance();
		//ReikaJavaLibrary.pConsole(points, Side.SERVER);
	}

	public int getLength() {
		return nodes.size();
	}

	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	private int calculateResistance() {
		int r = 0;
		for (int i = 0; i < nodes.size(); i++) {
			TileEntityWire wire = nodes.get(i);
			r += wire.getWireType().resistance;
		}
		return r;
	}

	public int getVoltageAt(TileEntityWire wire) {
		return start.getGenVoltage() > 0 ? start.getGenVoltage()-this.getResistanceTo(wire) : 0;
	}

	private int getResistanceTo(TileEntityWire wire) {
		return this.getResistanceTo(nodes.indexOf(wire));
	}

	private int getResistanceTo(int index) {
		int r = 0;
		for (int i = 0; i < index; i++) {
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

	public boolean containsBlock(TileEntityWire te) {
		return nodes.contains(te);
	}

	void tick(ElectriNetworkTickEvent evt) {
		//int current = this.getPathCurrent();

	}

	public boolean startsAt(int x, int y, int z) {
		return start.xCoord == x && y == start.yCoord && z == start.zCoord;
	}

	public boolean endsAt(int x, int y, int z) {
		return end.xCoord == x && y == end.yCoord && z == end.zCoord;
	}

	public int getTerminalVoltage() {
		return start.getGenVoltage() > 0 ? start.getGenVoltage()-this.getVoltageLoss() : 0;
	}

	public int getVoltageLoss() {
		return resistance;
	}

	public int getPathCurrent() {
		return start.getGenCurrent()/net.getNumberPathsStartingAt(start);
	}

}
