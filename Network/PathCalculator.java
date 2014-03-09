package Reika.RotationalInduction.Network;

import java.util.ArrayList;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.ArrayMap;
import Reika.RotationalInduction.Registry.InductionTiles;
import Reika.RotationalInduction.TileEntities.TileEntityGenerator;
import Reika.RotationalInduction.TileEntities.TileEntityMotor;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public class PathCalculator {

	private ArrayList<WirePath> paths = new ArrayList();

	private ArrayMap<ArrayList<ForgeDirection>> branchPoints = new ArrayMap(3);

	private final TileEntityGenerator start;
	private final TileEntityMotor end;
	private final WireNetwork net;

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
		int x = start.xCoord;
		int y = start.yCoord;
		int z = start.zCoord;
		World world = start.worldObj;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = WireNetwork.dirs[i];
			if (start.canNetworkOnSide(dir)) {
				int dx = x+dir.offsetX;
				int dy = y+dir.offsetY;
				int dz = z+dir.offsetZ;
				WirePath path = new WirePath();
				this.recursiveMap(world, dx, dy, dz, path);
				if (path.isValid()) {
					paths.add(path);
				}
			}
		}
	}

	private void recursiveMap(World world, int x, int y, int z, WirePath path) {
		if (path.containsBlock(x, y, z))
			return;
		if (this.isEnd(world, x, y, z)) {

		}
		else {
			InductionTiles t = InductionTiles.getTE(world, x, y, z);
			if (t == InductionTiles.WIRE) {
				path.addNode((TileEntityWire)world.getBlockTileEntity(x, y, z));
				if (net.hasNode(x, y, z)) {
					if (!branchPoints.containsKeyV(x, y, z))
						branchPoints.putV(net.getNodeAt(x, y, z).getDirections(), x, y, z);
				}
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = WireNetwork.dirs[i];
					int dx = x+dir.offsetX;
					int dy = y+dir.offsetY;
					int dz = z+dir.offsetZ;
				}
			}
		}
	}

	private boolean isEnd(World world, int x, int y, int z) {
		return end.worldObj == world && x == end.xCoord && y == end.yCoord && z == end.zCoord;
	}

	public ArrayList<WirePath> getCalculatedPaths() {
		return paths;
	}

}
