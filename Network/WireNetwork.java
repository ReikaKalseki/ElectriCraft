/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.ElectriNetworkManager;
import Reika.ElectriCraft.NetworkObject;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import Reika.ElectriCraft.Auxiliary.WrappedSource;
import Reika.ElectriCraft.Auxiliary.Interfaces.NetworkTile;
import Reika.ElectriCraft.Auxiliary.Interfaces.WireEmitter;
import Reika.ElectriCraft.Auxiliary.Interfaces.WireReceiver;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.TileEntities.TileEntityGenerator;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.API.Power.ShaftMerger;
import Reika.RotaryCraft.Auxiliary.PowerSourceList;
import Reika.RotaryCraft.Auxiliary.Interfaces.PowerSourceTracker;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class WireNetwork implements NetworkObject {

	private final HashMap<WorldLocation, WiringTile> wires = new HashMap();
	private final HashMap<WorldLocation, WireReceiver> sinks = new HashMap();
	private final HashMap<WorldLocation, WireEmitter> sources = new HashMap();
	private final HashMap<WorldLocation, WrappedSource> wrappers = new HashMap();
	private final HashMap<Coordinate, NetworkNode> nodes = new HashMap();
	private final Collection<WirePath> paths = new ArrayList();
	private final HashSet<WorldLocation> rifts = new HashSet();
	private final HashMap<WiringTile, Integer> pointVoltages = new HashMap();
	private final HashMap<WiringTile, Integer> pointCurrents = new HashMap();
	private final HashMap<WireReceiver, Integer> terminalVoltages = new HashMap();
	private final HashMap<WireReceiver, Integer> terminalCurrents = new HashMap();
	private final HashMap<WireReceiver, Integer> avgCurrents = new HashMap();
	private final HashMap<WireReceiver, Float> numSources = new HashMap();
	private final HashSet<Integer> dimIDs = new HashSet();
	private final HashSet<Integer> loadedDimIDs = new HashSet();

	private int interWireConnections;

	private boolean shorted = false;
	private boolean reUpdateThisTick;

	static final ForgeDirection[] dirs = ForgeDirection.values();

	public static final int TORQUE_PER_AMP = 8;

	private static final int MAX_PATHS = 500;
	private static final int MAX_INTERWIRE = 500;

	public WireNetwork() {
		ElectriNetworkManager.instance.addNetwork(this);

		/* Debugging
		try {
			ReikaJavaLibrary.pConsole("Main class: "+Arrays.toString(ElectriNetworkTickEvent.class.getConstructors()));
			ReikaJavaLibrary.pConsole("Super class: "+Arrays.toString(ElectriNetworkTickEvent.class.getSuperclass().getConstructors()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public void clearCache() {
		terminalCurrents.clear();
		terminalVoltages.clear();
		pointCurrents.clear();
		pointVoltages.clear();
		avgCurrents.clear();
		numSources.clear();
	}

	public boolean isMultiWorld() {
		return dimIDs.size() > 1;
	}

	private int getMaxInputVoltage() {
		int max = 0;
		for (WireEmitter e : sources.values()) {
			max = Math.max(max, e.getGenVoltage());
		}
		return max;
	}

	public boolean isLive() {
		return this.getMaxInputVoltage() > 0;
	}

	public int getNumberSources() {
		return sources.size();
	}

	public int getNumberSinks() {
		return sinks.size();
	}

	public void tick(ElectriNetworkTickEvent evt) {
		boolean flag = false;
		for (WirePath path : paths) {
			if (path.tick(evt)) {
				this.updateWires(false);
				flag = true;
			}
		}

		for (WrappedSource src : wrappers.values()) {
			if (src.needsUpdate()) {
				this.updateWires(false);
				flag = true;
			}
		}

		if (flag)
			return;

		for (WiringTile w : wires.values()) {
			if (w instanceof TileEntityWire) {
				TileEntityWire te = (TileEntityWire)w;
				int current = this.getPointCurrent(w);
				if (current > te.getMaxCurrent()) {
					te.overload(current);
				}
			}
		}

		if (this.isEmpty()) {
			this.clear(true);
			ElectriNetworkManager.instance.scheduleNetworkDiscard(this);
		}

		if (shorted) {
			shorted = false;
			this.updateWires(false);
		}
	}

	public void repath(ElectriNetworkRepathEvent evt) {
		if (reUpdateThisTick) {
			this.doRepath();
			reUpdateThisTick = false;
		}
	}

	private void doRepath() {
		this.recalculatePaths();
		for (WiringTile w : wires.values()) {
			w.onNetworkChanged();
		}
		for (WireEmitter w : sources.values()) {
			w.onNetworkChanged();
		}
		for (WireReceiver w : sinks.values()) {
			w.onNetworkChanged();
		}
	}

	public int getPointVoltage(WiringTile te) {
		if (shorted)
			return 0;
		Integer val = pointVoltages.get(te);
		if (val == null) {
			val = this.calcPointVoltage(te);
			pointVoltages.put(te, val);
		}
		return val.intValue();
	}

	public int getPointCurrent(WiringTile te) {
		if (shorted)
			return 0;
		Integer val = pointCurrents.get(te);
		if (val == null) {
			val = this.calcPointCurrent(te);
			pointCurrents.put(te, val);
		}
		return val.intValue();
	}

	private int calcPointVoltage(WiringTile te) {
		if (paths.isEmpty())
			return 0;
		int sv = Integer.MAX_VALUE;
		for (WirePath path : paths) {
			if (path.containsBlock(te) && path.start.getGenVoltage() > 0) {
				int v = path.getVoltageAt(te);
				if (v < sv)
					sv = v;
			}
		}
		return sv == Integer.MAX_VALUE ? 0 : sv;
	}

	private int calcPointCurrent(WiringTile te) {
		if (paths.isEmpty())
			return 0;
		int sa = 0;
		for (WirePath path : paths) {
			if (path.containsBlock(te)) {
				int a = path.getPathCurrent();
				sa += a;
			}
		}
		return sa;
	}

	@SubscribeEvent
	public void onAddWorld(WorldEvent.Load evt) {
		if (dimIDs.contains(evt.world.provider.dimensionId))
			loadedDimIDs.add(evt.world.provider.dimensionId);
	}

	@SubscribeEvent
	public void onRemoveWorld(WorldEvent.Unload evt) {
		loadedDimIDs.remove(evt.world.provider.dimensionId);
		if (loadedDimIDs.isEmpty())
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
			for (WiringTile wire : n.wires.values()) {
				wire.setNetwork(this);
				li.add(wire);
			}
			for (WireReceiver emitter : n.sinks.values()) {
				if (!li.contains(emitter)) {
					emitter.setNetwork(this);
					li.add(emitter);
				}
			}
			for (WireEmitter source : n.sources.values()) {
				if (!li.contains(source)) {
					source.setNetwork(this);
					li.add(source);
				}
			}
			for (Coordinate key : n.nodes.keySet()) {
				NetworkNode node = n.nodes.get(key);
				nodes.put(key, node);
			}

			interWireConnections += n.interWireConnections;

			n.clear(false);
		}
		this.updateWires(true);
	}

	private void clear(boolean clearTiles) {
		if (clearTiles) {
			for (WiringTile te : wires.values()) {
				te.resetNetwork();
			}
			for (WireReceiver te : sinks.values()) {
				te.resetNetwork();
			}
			for (WireEmitter te : sources.values()) {
				te.resetNetwork();
			}
		}

		wires.clear();
		sinks.clear();
		sources.clear();
		wrappers.clear();
		nodes.clear();
		paths.clear();
		rifts.clear();
		dimIDs.clear();
		loadedDimIDs.clear();
		this.clearCache();

		//ElectriNetworkManager.instance.scheduleNetworkDiscard(this);
	}

	public void addElement(NetworkTile te) {
		boolean changed = false;
		if (!te.isConnectable())
			return;
		if (te instanceof WireEmitter)
			changed |= this.addSource((WireEmitter)te);
		if (te instanceof WireReceiver) {
			changed |= !te.equals(sinks.put(this.getLocation(te), (WireReceiver)te));
		}
		if (te instanceof WiringTile) {
			WiringTile wire = (WiringTile)te;
			changed |= !te.equals(wires.put(this.getLocation(te), wire));
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
						nodes.put(new Coordinate(wire2.xCoord, wire2.yCoord, wire2.zCoord), new NetworkNode(this, wire2, sides));
						interWireConnections += sides.size()-2;
					}
				}
			}
		}
		if (changed) {
			dimIDs.add(te.getWorld().provider.dimensionId);
			//ReikaJavaLibrary.pConsole("Added "+te+" to "+this);
			this.updateWires(true);
		}
	}

	private boolean addSource(WireEmitter te) {
		boolean flag = !te.equals(sources.put(this.getLocation(te), te));
		if (te instanceof WrappedSource)
			flag |= !te.equals(wrappers.put(this.getLocation(te), (WrappedSource)te));
		return flag;
	}

	private WorldLocation getLocation(NetworkTile te) {
		return new WorldLocation(te.getWorld(), te.getX(), te.getY(), te.getZ());
	}

	private void removeSource(WireEmitter te) {
		sources.remove(te);
		if (te instanceof WrappedSource)
			wrappers.remove(te);
	}

	public void checkRiftConnections() {
		boolean flag = true;
		for (WorldLocation loc : rifts) {
			if (!(loc.getTileEntity() instanceof WorldRift)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			this.clear(true);
		}
	}

	public void updateWires() {
		this.updateWires(false);
	}

	void updateWires(boolean force) {
		if (force) {
			this.doRepath();
		}
		else {
			reUpdateThisTick = true;
		}
	}

	private void recalculatePaths() {
		paths.clear();
		rifts.clear();
		dimIDs.clear();
		loadedDimIDs.clear();
		interWireConnections = 0;
		this.clearCache();
		for (WireEmitter src : sources.values()) {
			for (WireReceiver sink : sinks.values()) {
				if (src != sink) {
					PathCalculator pc = new PathCalculator(src, sink, this);
					pc.calculatePaths();
					WirePath path = pc.getShortestPath();
					if (path != null) {
						paths.add(path);
						dimIDs.addAll(path.getDimensions());
						loadedDimIDs.addAll(dimIDs);
						this.validatePathLimit();
						rifts.addAll(pc.getRifts());
					}
				}
			}
		}
		ElectriCraft.logger.debug("Remapped network "+this);
	}

	private void validatePathLimit() {
		//ReikaJavaLibrary.spamConsole(paths.size()+"|"+nodes.size()+"/"+wires.size()+"#"+interWireConnections);
		if (paths.size() >= MAX_PATHS || interWireConnections > MAX_INTERWIRE/* || (wires.size() > 20 && nodes.size() >= wires.size()*3/4)*/) {
			//for (WirePath p : paths) {
			//	p.overload(true);
			//}
			for (WiringTile w : wires.values()) {
				if (w instanceof TileEntityWire)
					((TileEntityWire)w).overload(Integer.MAX_VALUE);
			}
		}
	}

	public float getNumberSourcesPer(WireReceiver te) {
		if (shorted)
			return 0;
		Float val = numSources.get(te);
		if (val == null) {
			val = this.calcNumberSourcesPer(te);
			numSources.put(te, val);
		}
		return val.floatValue();
	}

	public int getAverageCurrent(WireReceiver te) {
		if (shorted)
			return 0;
		Integer val = avgCurrents.get(te);
		if (val == null) {
			val = this.calcAvgCurrent(te);
			avgCurrents.put(te, val);
		}
		return val.intValue();
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
		for (WirePath path : paths) {
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pa = path.getPathCurrent();
				//ReikaJavaLibrary.pConsole(pa+" @ "+path+"/"+te, Side.SERVER);
				a += pa;
			}
		}
		return a;
	}

	private int calcAvgCurrent(WireReceiver te) {
		int a = 0;
		int c = 0;
		for (WirePath path : paths) {
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pa = path.getPathCurrent();
				a += pa;
				c++;
				//ReikaJavaLibrary.pConsole(pa+" @ "+path+" > "+te, Side.SERVER);
			}
		}
		//ReikaJavaLibrary.pConsole(a+":"+c+" @ "+te);
		return c > 0 ? a/c : 0;
	}

	private float calcNumberSourcesPer(WireReceiver te) {
		float f = 0;
		for (WirePath path : paths) {
			if (path.canConduct() && path.endsAt(te.getX(), te.getY(), te.getZ())) {
				f += 1F/this.getNumberPathsStartingAt(path.start);
			}
		}
		return f;
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
		boolean someV = false;
		for (WirePath path : paths) {
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pv = path.getTerminalVoltage();
				if (pv != 0) {
					someV = true;
					if (pv < v)
						v = pv;
				}
			}
		}
		return someV ? v : 0;
	}

	private int getAverageVoltageOfPaths(WireReceiver te) {
		int v = 0;
		int c = 0;
		if (paths.isEmpty())
			return 0;
		for (WirePath path : paths) {
			if (path.endsAt(te.getX(), te.getY(), te.getZ())) {
				int pv = path.getTerminalVoltage();
				v += pv;
				c++;
			}
		}
		return c > 0 ? v/c : 0;
	}

	public boolean hasNode(int x, int y, int z) {
		return nodes.containsKey(new Coordinate(x, y, z));
	}

	NetworkNode getNodeAt(int x, int y, int z) {
		return nodes.get(new Coordinate(x, y, z));
	}

	NetworkNode getNodeAt(WiringTile w) {
		return this.getNodeAt(w.xCoord, w.yCoord, w.zCoord);
	}

	public void shortNetwork() {
		shorted = true;
		this.updateWires(false);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append(this.getInputCurrent()+"A @ "+this.getNetworkVoltage()+"V");
		//sb.append(" ");
		//sb.append(wires.size()+" wires, "+sinks.size()+" emitters, "+sources.size()+" generators");
		sb.append(sources.size()+"SR/"+wires.size()+"W/"+sinks.size()+"SN-#");
		sb.append(paths);
		sb.append(";{"+this.hashCode()+"}");
		sb.append("$["+dimIDs+"]");
		return sb.toString();
	}

	public void removeElement(NetworkTileEntity te) {
		if (te instanceof WireEmitter)
			this.removeSource((WireEmitter)te);
		if (te instanceof WireReceiver)
			sinks.remove(te);
		if (te instanceof WiringTile)
			wires.remove(te);
		this.rebuild();
	}

	private void rebuild() {
		ArrayList<NetworkTile> li = new ArrayList();
		for (NetworkTile te : wires.values()) {
			li.add(te);
		}
		for (NetworkTile te : sinks.values()) {
			if (!li.contains(te))
				li.add(te);
		}
		for (NetworkTile te : sources.values()) {
			if (!li.contains(te))
				li.add(te);
		}
		this.clear(true);

		for (NetworkTile te : li) {
			te.findAndJoinNetwork(te.getWorld(), te.getX(), te.getY(), te.getZ());
		}
	}

	ArrayList<WirePath> getPathsStartingAt(WireEmitter start) {
		ArrayList<WirePath> li = new ArrayList();
		for (WirePath path : paths) {
			if (path.startsAt(start.getX(), start.getY(), start.getZ())) {
				li.add(path);
			}
		}
		return li;
	}

	ArrayList<WirePath> getPathsEndingAt(WireReceiver end) {
		ArrayList<WirePath> li = new ArrayList();
		for (WirePath path : paths) {
			if (path.endsAt(end.getX(), end.getY(), end.getZ())) {
				li.add(path);
			}
		}
		return li;
	}

	public int getNumberPathsStartingAt(WireEmitter start) {
		return this.getPathsStartingAt(start).size();
	}

	public PowerSourceList getInputSources(PowerSourceTracker io, ShaftMerger caller) {
		return this.getInputSources(io, caller, null);
	}

	public PowerSourceList getInputSources(PowerSourceTracker io, ShaftMerger caller, WireReceiver terminus) {
		PowerSourceList p = new PowerSourceList();
		if (terminus != null) {
			for (WirePath path : this.getPathsEndingAt(terminus)) {
				if (path.canConduct() && path.start instanceof TileEntityGenerator) {
					p.addAll(((TileEntityGenerator)path.start).getPowerSources(io, caller));
				}
			}
		}
		else {
			for (WireEmitter w : sources.values()) {
				if (w instanceof TileEntityGenerator) {
					p.addAll(((TileEntityGenerator)w).getPowerSources(io, caller));
				}
			}
		}
		return p;
	}

	public int getTotalCurrent() {
		int val = 0;
		for (WirePath p : paths) {
			val += p.getPathCurrent();
		}
		return val;
	}

}
