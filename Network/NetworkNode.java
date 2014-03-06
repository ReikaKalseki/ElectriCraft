package Reika.RotationalInduction.Network;

public class NetworkNode {

	public final int x;
	public final int y;
	public final int z;

	private final WireNetwork network;

	public NetworkNode(WireNetwork w, int x, int y, int z) {
		network = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
