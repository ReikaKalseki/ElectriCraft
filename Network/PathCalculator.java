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
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.RotationalInduction.Registry.InductionTiles;
import Reika.RotationalInduction.TileEntities.TileEntityGenerator;
import Reika.RotationalInduction.TileEntities.TileEntityMotor;

public class PathCalculator {

	private final TileEntityGenerator start;
	private final TileEntityMotor end;
	private final WireNetwork net;

	private ArrayList<WirePath> paths = new ArrayList();

	public PathCalculator(TileEntityGenerator start, TileEntityMotor end, WireNetwork w) {
		this.start = start;
		this.end = end;
		net = w;
		if (start == null || end == null)
			throw new IllegalArgumentException("Cannot connect null points!");
		if (start.worldObj != end.worldObj)
			throw new IllegalArgumentException("Cannot connect points across dimensions!");
	}

	public void calculatePaths() {
		//ReikaJavaLibrary.pConsole("START", Side.SERVER);
		int x = start.xCoord;
		int y = start.yCoord;
		int z = start.zCoord;
		World world = start.worldObj;
		//li.add(Arrays.asList(x, y, z));
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = WireNetwork.dirs[i];
			LinkedList<List<Integer>> li = new LinkedList();
			if (start.canNetworkOnSide(dir)) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				if (this.isEnd(world, dx, dy, dz)) {
					if (end.canNetworkOnSide(dir.getOpposite())) {
						//li.addLast(Arrays.asList(dx, dy, dz));
						paths.add(new WirePath(world, li, start, end, net));
						//li.removeLast();
						return;
					}
				}
				else {
					InductionTiles t = InductionTiles.getTE(world, dx, dy, dz);
					if (t == InductionTiles.WIRE) {
						this.recursiveCalculate(world, dx, dy, dz, li);
					}
				}
			}
		}
		//ReikaJavaLibrary.pConsole(paths, Side.SERVER);
	}

	private void recursiveCalculate(World world, int x, int y, int z, LinkedList li) {
		if (li.contains(Arrays.asList(x, y, z))) {
			return;
		}
		//ReikaJavaLibrary.pConsole(x+", "+y+", "+z, Side.SERVER);
		li.addLast(Arrays.asList(x, y, z));
		//ReikaJavaLibrary.pConsole("<<"+li.size()+">>"+(x+0.5)+","+(y+0.5)+","+(z+0.5));
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = WireNetwork.dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (this.isEnd(world, dx, dy, dz)) {
				if (end.canNetworkOnSide(dir.getOpposite())) {
					//li.addLast(Arrays.asList(dx, dy, dz));
					paths.add(new WirePath(world, li, start, end, net));
					//ReikaJavaLibrary.pConsole(li.size(), Side.SERVER);
					//ReikaJavaLibrary.pConsole("Create("+li.size()+")", Side.SERVER);
					//li.removeLast();
					return;
				}
			}
			else {
				InductionTiles t = InductionTiles.getTE(world, dx, dy, dz);
				if (t == InductionTiles.WIRE) {
					this.recursiveCalculate(world, dx, dy, dz, li);
					//ReikaJavaLibrary.pConsole(dir+"@"+x+","+y+","+z+" :"+li.size(), Side.SERVER);
				}
			}
		}
		li.removeLast();
		//ReikaJavaLibrary.pConsole(">>"+li.size()+"<<"+(x+0.5)+","+(y+0.5)+","+(z+0.5));
	}

	private boolean isEnd(World world, int x, int y, int z) {
		return end.worldObj == world && x == end.xCoord && y == end.yCoord && z == end.zCoord;
	}

	public ArrayList<WirePath> getCalculatedPaths() {
		return paths;
	}

}
