/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ElectriNetworkManager implements TickHandler {

	public static final ElectriNetworkManager instance = new ElectriNetworkManager();

	private final Collection<NetworkObject> discard = new ArrayList();

	private ElectriNetworkManager() {

	}

	public void scheduleNetworkDiscard(NetworkObject net) {
		discard.add(net);
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		Phase phase = (Phase)tickData[1];
		if (phase == Phase.START) {
			for (NetworkObject o : discard) {
				try {
					MinecraftForge.EVENT_BUS.unregister(o);
				}
				catch (Exception e) { //WHY
					ElectriCraft.logger.logError("Forge Event Bus unregistration error, network "+o+" could not be cleaned. If you see many of these messages, consider restarting soon.");
				}
			}
			discard.clear();
			if (world != null)
				MinecraftForge.EVENT_BUS.post(new ElectriNetworkTickEvent(world));
		}
		else if (phase == Phase.END) {
			if (world != null)
				MinecraftForge.EVENT_BUS.post(new ElectriNetworkRepathEvent(world));
		}
	}

	@Override
	public TickType getType() {
		return TickType.WORLD;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START || p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "Electri Network";
	}

}
