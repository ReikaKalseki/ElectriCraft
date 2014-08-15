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

import Reika.ChromatiCraft.API.SpaceRift;
import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.ElectriCraft.Auxiliary.WireEmitter;
import Reika.ElectriCraft.Auxiliary.WireReceiver;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.Registry.ElectriTiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PathCalculator {

	private final WireEmitter start;
	private final WireReceiver end;
	private final WireNetwork net;

	private ArrayList<WirePath> paths = new ArrayList();

	public PathCalculator(WireEmitter start, WireReceiver end, WireNetwork w) {
		this.start = start;
		this.end = end;
		net = w;
		this.verify();
		//ReikaJavaLibrary.pConsole(start, end instanceof TileEntityBattery);
	}

	private void verify() {
		if (start == end)
			throw new IllegalArgumentException("Cannot connect an object to itself!");
		if (start == null || end == null)
			throw new IllegalArgumentException("Cannot connect null points!");
		if (start.getWorld() != end.getWorld())
			throw new IllegalArgumentException("Cannot connect points across dimensions!");
	}

	public void calculatePaths() {
		//ReikaJavaLibrary.pConsole("START", Side.SERVER);
		int x = start.getX();
		int y = start.getY();
		int z = start.getZ();
		World world = start.getWorld();
		//li.add(Arrays.asList(x, y, z));
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = WireNetwork.dirs[i];
			LinkedList<List<Integer>> li = new LinkedList();
			if (start.canEmitPowerToSide(dir)) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (this.isEnd(world, dx, dy, dz)) {
					if (end.canReceivePower() && end.canReceivePowerFromSide(dir.getOpposite())) {
						//li.addLast(Arrays.asList(dx, dy, dz));
						paths.add(new WirePath(world, li, start, end, net));
						//li.removeLast();
						return;
					}
				}
				else {
					ElectriTiles t = ElectriTiles.getTE(world, dx, dy, dz);
					if (t != null && t.isWiringPiece()) {
						WiringTile tile = (WiringTile)world.getTileEntity(dx, dy, dz);
						if (tile.canNetworkOnSide(dir.getOpposite()))
							this.recursiveCalculate(world, dx, dy, dz, li);
					}
					else {
						TileEntity te = world.getTileEntity(dx, dy, dz);
						if (te instanceof SpaceRift) {
							SpaceRift sr = (SpaceRift)te;
							WorldLocation loc = sr.getLinkTarget();
							if (loc != null) {
								TileEntity other = sr.getTileEntityFrom(dir);
								if (other instanceof WiringTile) {
									if (((WiringTile)other).canNetworkOnSide(dir.getOpposite())) {
										dx = loc.xCoord+dir.offsetX;
										dy = loc.yCoord+dir.offsetY;
										dz = loc.zCoord+dir.offsetZ;
										this.recursiveCalculate(world, dx, dy, dz, li);
									}
								}
							}
						}
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(paths, Side.SERVER);
	}
	//check direction code!!
	private void recursiveCalculate(World world, int x, int y, int z, LinkedList li) {
		if (li.contains(Arrays.asList(x, y, z))) {
			return;
		}
		//ReikaJavaLibrary.pConsole(x+", "+y+", "+z, Side.SERVER);
		li.addLast(Arrays.asList(x, y, z));
		WiringTile te = (WiringTile)world.getTileEntity(x, y, z);
		//ReikaJavaLibrary.pConsole("<<"+li.size()+">>"+(x+0.5)+","+(y+0.5)+","+(z+0.5));
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = WireNetwork.dirs[i];
			if (te.canNetworkOnSide(dir)) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (this.isEnd(world, dx, dy, dz)) {
					if (end.canReceivePower() && end.canReceivePowerFromSide(dir.getOpposite())) {
						//li.addLast(Arrays.asList(dx, dy, dz));
						paths.add(new WirePath(world, li, start, end, net));
						//ReikaJavaLibrary.pConsole(li.size(), Side.SERVER);
						//ReikaJavaLibrary.pConsole("Create("+li.size()+")", Side.SERVER);
						//li.removeLast();
						return;
					}
				}
				else {
					ElectriTiles t = ElectriTiles.getTE(world, dx, dy, dz);
					if (t != null && t.isWiringPiece()) {
						WiringTile tile = (WiringTile)world.getTileEntity(dx, dy, dz);
						if (this.tileCanConnect(tile) && tile.canNetworkOnSide(dir.getOpposite()))
							this.recursiveCalculate(world, dx, dy, dz, li);
						//ReikaJavaLibrary.pConsole(dir+"@"+x+","+y+","+z+" :"+li.size(), Side.SERVER);
					}
					else {
						TileEntity te2 = world.getTileEntity(dx, dy, dz);
						if (te2 instanceof SpaceRift) {
							SpaceRift sr = (SpaceRift)te2;
							WorldLocation loc = sr.getLinkTarget();
							if (loc != null) {
								TileEntity other = sr.getTileEntityFrom(dir);
								if (other instanceof WiringTile) {
									if (((WiringTile)other).canNetworkOnSide(dir.getOpposite())) {
										dx = loc.xCoord+dir.offsetX;
										dy = loc.yCoord+dir.offsetY;
										dz = loc.zCoord+dir.offsetZ;
										this.recursiveCalculate(world, dx, dy, dz, li);
									}
								}
							}
						}
					}
				}
			}
		}
		li.removeLast();
		//ReikaJavaLibrary.pConsole(">>"+li.size()+"<<"+(x+0.5)+","+(y+0.5)+","+(z+0.5));
	}

	private boolean tileCanConnect(WiringTile tile) {
		return tile instanceof TileEntityWireComponent ? ((TileEntityWireComponent)tile).canConnect() : true;
	}

	private boolean isEnd(World world, int x, int y, int z) {
		return end.getWorld() == world && x == end.getX() && y == end.getY() && z == end.getZ();
	}

	ArrayList<WirePath> getCalculatedPaths() {
		return paths;
	}

	public WirePath getShortestPath() {
		int index = -1;
		int length = Integer.MAX_VALUE;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			int l = path.getLength();
			if (l < length) {
				length = l;
				index = i;
			}
		}
		return index >= 0 ? paths.get(index) : null;
	}

}