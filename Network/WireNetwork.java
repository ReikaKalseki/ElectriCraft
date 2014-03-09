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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import Reika.RotationalInduction.Auxiliary.InductionNetworkTickEvent;
import Reika.RotationalInduction.Base.NetworkTileEntity;
import Reika.RotationalInduction.TileEntities.TileEntityGenerator;
import Reika.RotationalInduction.TileEntities.TileEntityMotor;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public final class WireNetwork {

	private ArrayList<TileEntityWire> wires = new ArrayList();
	private ArrayList<TileEntityMotor> sinks = new ArrayList();
	private ArrayList<TileEntityGenerator> sources = new ArrayList();
	private HashMap<List<Integer>, NetworkNode> nodes = new HashMap();
	private ArrayList<WirePath> paths = new ArrayList();

	static final ForgeDirection[] dirs = ForgeDirection.values();

	public static final int TORQUE_PER_AMP = 8;

	public WireNetwork() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private int getMaxInputVoltage() {
		int max = 0;
		for (int i = 0; i < sources.size(); i++) {
			TileEntityGenerator e = sources.get(i);
			max = Math.max(max, e.getGenVoltage());
		}
		return max;
	}

	public int getNetworkVoltage() {
		return this.getMaxInputVoltage();
	}
	/*
	public int getInputCurrent() {
		int max = this.getMaxInputVoltage();
		int current = 0;
		for (int i = 0; i < sources.size(); i++) {
			TileEntityGenerator e = sources.get(i);
			if (max == e.getGenVoltage()) {
				current += e.getGenCurrent();
			}
		}
		return current;
	}
	 */
	public int getNumberMotors() {
		return sinks.size();
	}

	@ForgeSubscribe
	public void tick(InductionNetworkTickEvent evt) {
		int v = this.getNetworkVoltage();
		int a = 0;//this.getInputCurrent();
		for (int i = 0; i < wires.size(); i++) {
			TileEntityWire wire = wires.get(i);
			if (a > wire.getCurrentLimit())
				wire.overCurrent();
		}
		for (int i = 0; i < sinks.size(); i++) {
			TileEntityMotor sink = sinks.get(i);
			if (a > sink.getCurrentLimit())
				sink.overCurrent();
		}
		for (int i = 0; i < sources.size(); i++) {
			TileEntityGenerator source = sources.get(i);
			if (a > source.getCurrentLimit())
				source.overCurrent();
		}
	}

	public void merge(WireNetwork n) {
		if (n != this) {
			for (int i = 0; i < n.wires.size(); i++) {
				TileEntityWire wire = n.wires.get(i);
				wire.setNetwork(this);
			}
			for (int i = 0; i < n.sinks.size(); i++) {
				TileEntityMotor emitter = n.sinks.get(i);
				emitter.setNetwork(this);
			}
			for (int i = 0; i < n.sources.size(); i++) {
				TileEntityGenerator source = n.sources.get(i);
				source.setNetwork(this);
			}
		}
	}

	private void clear() {
		for (int i = 0; i < wires.size(); i++) {
			wires.get(i).resetNetwork();
		}
		for (int i = 0; i < sinks.size(); i++) {
			sinks.get(i).resetNetwork();
		}
		for (int i = 0; i < sources.size(); i++) {
			sources.get(i).resetNetwork();
		}

		wires.clear();
		sinks.clear();
		sources.clear();
		nodes.clear();
		paths.clear();

		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public void addElement(NetworkTileEntity te) {
		if (te instanceof TileEntityGenerator)
			sources.add((TileEntityGenerator)te);
		else if (te instanceof TileEntityMotor)
			sinks.add((TileEntityMotor)te);
		else {
			TileEntityWire wire = (TileEntityWire)te;
			wires.add(wire);
			for (int k = 0; k < 6; k++) {
				ForgeDirection side = dirs[k];
				TileEntity adj2 = wire.getAdjacentTileEntity(side);
				if (adj2 instanceof TileEntityWire) {
					TileEntityWire wire2 = (TileEntityWire)adj2;
					ArrayList<ForgeDirection> sides = new ArrayList();
					for (int i = 0; i < 6; i++) {
						ForgeDirection dir = dirs[i];
						TileEntity adj = wire2.getAdjacentTileEntity(dir);
						if (adj instanceof NetworkTileEntity) {
							NetworkTileEntity nw = (NetworkTileEntity)adj;
							if (((NetworkTileEntity) adj).canNetworkOnSide(dir.getOpposite()))
								sides.add(dir);
						}
					}
					if (sides.size() > 2 && !this.hasNode(wire2.xCoord, wire2.yCoord, wire2.zCoord)) {
						nodes.put(Arrays.asList(wire2.xCoord, wire2.yCoord, wire2.zCoord), new NetworkNode(this, wire2, sides));
					}
				}
			}
		}
		this.recalculatePaths();
	}

	private void recalculatePaths() {
		paths.clear();
		for (int i = 0; i < sources.size(); i++) {
			for (int k = 0; k < sinks.size(); k++) {
				PathCalculator pc = new PathCalculator(sources.get(i), sinks.get(k), this);
				pc.calculatePaths();
				paths.addAll(pc.getCalculatedPaths());
			}
		}
	}

	public boolean hasNode(int x, int y, int z) {
		return nodes.containsKey(Arrays.asList(x, y, z));
	}

	NetworkNode getNodeAt(int x, int y, int z) {
		return nodes.get(Arrays.asList(x, y, z));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append(this.getInputCurrent()+"A @ "+this.getNetworkVoltage()+"V");
		//sb.append(" ");
		//sb.append(wires.size()+" wires, "+sinks.size()+" emitters, "+sources.size()+" generators");
		sb.append(paths);
		sb.append(";{"+this.hashCode()+"}");
		return sb.toString();
	}

	public void removeElement(NetworkTileEntity te) {
		if (te instanceof TileEntityGenerator)
			sources.remove(te);
		else if (te instanceof TileEntityMotor)
			sinks.remove(te);
		else
			wires.remove(te);
		this.rebuild();
	}

	private void rebuild() {
		ArrayList<NetworkTileEntity> li = new ArrayList();
		for (int i = 0; i < wires.size(); i++) {
			li.add(wires.get(i));
		}
		for (int i = 0; i < sinks.size(); i++) {
			li.add(sinks.get(i));
		}
		for (int i = 0; i < sources.size(); i++) {
			li.add(sources.get(i));
		}
		this.clear();

		for (int i = 0; i < li.size(); i++) {
			NetworkTileEntity te = li.get(i);
			te.findAndJoinNetwork(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}
	}

}
