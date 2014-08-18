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

import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.NetworkObject;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkTickEvent;
import Reika.ElectriCraft.Auxiliary.NetworkTile;
import Reika.ElectriCraft.Auxiliary.WireEmitter;
import Reika.ElectriCraft.Auxiliary.WireReceiver;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.TileEntities.TileEntityWire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class WireNetwork implements NetworkObject {

	private ArrayList<WiringTile> wires = new ArrayList();
	private ArrayList<WireReceiver> sinks = new ArrayList();
	private ArrayList<WireEmitter> sources = new ArrayList();
	private HashMap<List<Integer>, NetworkNode> nodes = new HashMap();
	private ArrayList<WirePath> paths = new ArrayList();
	private HashMap<TileEntityWire, Integer> pointVoltages = new HashMap();
	private HashMap<TileEntityWire, Integer> pointCurrents = new HashMap();
	private HashMap<WireReceiver, Integer> terminalVoltages = new HashMap();
	private HashMap<WireReceiver, Integer> terminalCurrents = new HashMap();

	private boolean shorted = false;

	static final ForgeDirection[] dirs = ForgeDirection.values();

	public static final int TORQUE_PER_AMP = 8;

	public WireNetwork() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void clearCache() {
		terminalCurrents.clear();
		terminalVoltages.clear();
		pointCurrents.clear();
		pointVoltages.clear();
	}

	private int getMaxInputVoltage() {
		int max = 0;
		for (int i = 0; i < sources.size(); i++) {
			WireEmitter e = sources.get(i);
			max = Math.max(max, e.getGenVoltage());
		}
		return max;
	}

	public boolean isLive() {
		return this.getMaxInputVoltage() > 0;
	}

	public int getNumberMotors() {
		return sinks.size();
	}

	@SubscribeEvent
	public void tick(ElectriNetworkTickEvent evt) {
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			path.tick(evt);
		}
		for (int i = 0; i < wires.size(); i++) {
			WiringTile w = wires.get(i);
			if (w instanceof TileEntityWire) {
				int current = this.getPointCurrent((TileEntityWire)w);
				if (current > w.getCurrentLimit()) {
					w.overCurrent();
				}
			}
		}

		if (this.isEmpty())
			this.clear(true);

		shorted = false;
	}

	public int getPointVoltage(TileEntityWire te) {
		if (shorted)
			return 0;
		Integer val = pointVoltages.get(te);
		if (val == null) {
			val = this.calcPointVoltage(te);
			pointVoltages.put(te, val);
		}
		return val.intValue();
	}

	public int getPointCurrent(TileEntityWire te) {
		if (shorted)
			return 0;
		Integer val = pointCurrents.get(te);
		if (val == null) {
			val = this.calcPointCurrent(te);
			pointCurrents.put(te, val);
		}
		return val.intValue();
	}

	private int calcPointVoltage(TileEntityWire te) {
		if (paths.isEmpty())
			return 0;
		int sv = 0;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			if (path.containsBlock(te)) {
				int v = path.getVoltageAt(te);
				if (v > sv)
					sv = v;
			}
		}
		return sv;
	}

	private int calcPointCurrent(TileEntityWire te) {
		if (paths.isEmpty())
			return 0;
		int sa = 0;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			if (path.containsBlock(te)) {
				int a = path.getPathCurrent();
				sa += a;
			}
		}
		return sa;
	}

	@SubscribeEvent
	public void onRemoveWorld(WorldEvent.Unload evt) {
		this.clear(true);
	}

	public boolean isEmpty() {
		return wires.isEmpty() && sinks.isEmpty() && sources.isEmpty();
	}

	public int tickRate() {
		return 1;
	}

	public void merge(WireNetwork n) {
		if (n != this) {
			ArrayList<NetworkTile> li = new ArrayList();
			for (int i = 0; i < n.wires.size(); i++) {
				WiringTile wire = n.wires.get(i);
				wire.setNetwork(this);
				li.add(wire);
			}
			for (int i = 0; i < n.sinks.size(); i++) {
				WireReceiver emitter = n.sinks.get(i);
				if (!li.contains(emitter)) {
					emitter.setNetwork(this);
					li.add(emitter);
				}
			}
			for (int i = 0; i < n.sources.size(); i++) {
				WireEmitter source = n.sources.get(i);
				if (!li.contains(source)) {
					source.setNetwork(this);
					li.add(source);
				}
			}
			for (List<Integer> key : n.nodes.keySet()) {
				NetworkNode node = n.nodes.get(key);
				nodes.put(key, node);
			}
			n.clear(false);
		}
		this.updateWires();
	}

	private void clear(boolean clearTiles) {
		if (clearTiles) {
			for (int i = 0; i < wires.size(); i++) {
				wires.get(i).resetNetwork();
			}
			for (int i = 0; i < sinks.size(); i++) {
				sinks.get(i).resetNetwork();
			}
			for (int i = 0; i < sources.size(); i++) {
				sources.get(i).resetNetwork();
			}
		}

		wires.clear();
		sinks.clear();
		sources.clear();
		nodes.clear();
		paths.clear();
		this.clearCache();

		try {
			MinecraftForge.EVENT_BUS.unregister(this);
		}
		catch (Exception e) { //randomly??
			e.printStackTrace();
		}
	}

	public void addElement(NetworkTile te) {
		if (te instanceof WireEmitter)
			sources.add((WireEmitter)te);
		if (te instanceof WireReceiver)
			sinks.add((WireReceiver)te);
		if (te instanceof WiringTile) {
			WiringTile wire = (WiringTile)te;
			wires.add(wire);
			for (int k = 0; k < 6; k++) {
				ForgeDirection side = dirs[k];
				TileEntity adj2 = wire.getAdjacentTileEntity(side);
				if (adj2 instanceof WiringTile) {
					WiringTile wire2 = (WiringTile)adj2;
					ArrayList<ForgeDirection> sides = new ArrayList();
					for (int i = 0; i < 6; i++) {
						ForgeDirection dir = dirs[i];
						TileEntity adj = wire2.getAdjacentTileEntity(dir);
						if (adj instanceof NetworkTile) {
							NetworkTile nw = (NetworkTile)adj;
							if (((NetworkTile)adj).canNetworkOnSide(dir.getOpposite()))
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
		this.updateWires();
	}

	public void updateWires() {
		this.recalculatePaths();
		for (int i = 0; i < wires.size(); i++) {
			wires.get(i).onNetworkChanged();
		}
	}

	private void recalculatePaths() {
		paths.clear();
		this.clearCache();
		for (int i = 0; i < sources.size(); i++) {
			for (int k = 0; k < sinks.size(); k++) {
				WireEmitter src = sources.get(i);
				WireReceiver sink = sinks.get(k);
				if (src != sink) {
					PathCalculator pc = new PathCalculator(src, sink, this);
					pc.calculatePaths();
					WirePath path = pc.getShortestPath();
					if (path != null)
						paths.add(path);
				}
			}
		}
		ElectriCraft.logger.debug("Remapped network "+this);
	}

	public int getTerminalCurrent(WireReceiver te) {
		if (shorted)
			return 0;
		Integer val = terminalCurrents.get(te);
		if (val == null) {
			val = this.calcTerminalCurrent(te);
			terminalCurrents.put(te, val);
		}
		return val.intValue();
	}

	public int getTerminalVoltage(WireReceiver te) {
		if (shorted)
			return 0;
		Integer val = terminalVoltages.get(te);
		if (val == null) {
			val = this.calcTerminalVoltage(te);
			terminalVoltages.put(te, val);
		}
		return val.intValue();
	}

	private int calcTerminalCurrent(WireReceiver te) {
		int a = 0;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pa = path.getPathCurrent();
				a += pa;
			}
		}
		return a;
	}

	private int calcTerminalVoltage(WireReceiver te) {
		return this.getLowestVoltageOfPaths(te);
	}

	public int getNumberPaths() {
		return paths.size();
	}

	private int getLowestVoltageOfPaths(WireReceiver te) {
		int v = Integer.MAX_VALUE;
		if (paths.isEmpty())
			return 0;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pv = path.getTerminalVoltage();
				if (pv < v)
					v = pv;
			}
		}
		return v;
	}

	private int getAverageVoltageOfPaths(WireReceiver te) {
		int v = 0;
		int c = 0;
		if (paths.isEmpty())
			return 0;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pv = path.getTerminalVoltage();
				v += pv;
				c++;
			}
		}
		return c > 0 ? v/c : 0;
	}

	public boolean hasNode(int x, int y, int z) {
		return nodes.containsKey(Arrays.asList(x, y, z));
	}

	NetworkNode getNodeAt(int x, int y, int z) {
		return nodes.get(Arrays.asList(x, y, z));
	}

	public void shortNetwork() {
		shorted = true;
		this.updateWires();
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
		if (te instanceof WireEmitter)
			sources.remove(te);
		if (te instanceof WireReceiver)
			sinks.remove(te);
		if (te instanceof WiringTile)
			wires.remove(te);
		this.rebuild();
	}

	private void rebuild() {
		ArrayList<NetworkTile> li = new ArrayList();
		for (int i = 0; i < wires.size(); i++) {
			li.add(wires.get(i));
		}
		for (int i = 0; i < sinks.size(); i++) {
			if (!li.contains(sinks.get(i)))
				li.add(sinks.get(i));
		}
		for (int i = 0; i < sources.size(); i++) {
			if (!li.contains(sources.get(i)))
				li.add(sources.get(i));
		}
		this.clear(true);

		for (int i = 0; i < li.size(); i++) {
			NetworkTile te = li.get(i);
			te.findAndJoinNetwork(te.getWorld(), te.getX(), te.getY(), te.getZ());
		}
	}

	ArrayList<WirePath> getPathsStartingAt(WireEmitter start) {
		ArrayList<WirePath> li = new ArrayList();
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			if (path.startsAt(start.getX(), start.getY(), start.getZ())) {
				li.add(path);
			}
		}
		return li;
	}

	public int getNumberPathsStartingAt(WireEmitter start) {
		return this.getPathsStartingAt(start).size();
	}

}
