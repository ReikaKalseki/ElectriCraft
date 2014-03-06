package Reika.RotationalInduction.Auxiliary;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class InductionNetworkTickEvent extends Event {

	private static int currentIndex = 0;

	public final World networkWorld;
	public final long worldTime;
	public final long systemTime;
	public final int tickIndex;

	public InductionNetworkTickEvent(World world) {
		networkWorld = world;
		tickIndex = currentIndex;
		worldTime = world.getTotalWorldTime();
		systemTime = System.currentTimeMillis();
	}

}
