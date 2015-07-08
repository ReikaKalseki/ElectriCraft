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
import net.minecraftforge.common.DimensionManager;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ElectriNetworkManager implements TickHandler {

	public static final ElectriNetworkManager instance = new ElectriNetworkManager();

	private final Collection<NetworkObject> networks = new ArrayList();
	private final Collection<NetworkObject> discard = new ArrayList();

	private ElectriNetworkManager() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		Phase phase = (Phase)tickData[0];
		World world = DimensionManager.getWorld(0);
		if (phase == Phase.START) {
			if (!discard.isEmpty()) {
				networks.removeAll(discard);
				discard.clear();
			}
			if (world != null) {
				ElectriNetworkTickEvent evt = new ElectriNetworkTickEvent(world);
				for (NetworkObject net : networks) {
					net.tick(evt);
				}
			}
		}
		else if (phase == Phase.END) {
			if (world != null) {
				ElectriNetworkRepathEvent evt = new ElectriNetworkRepathEvent(world);
				for (NetworkObject net : networks) {
					net.repath(evt);
				}
			}
		}
	}

	@Override
	public TickType getType() {
		return TickType.SERVER;
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START || p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "Electri Network";
	}

	public void addNetwork(NetworkObject net) {
		networks.add(net);
	}

	public void scheduleNetworkDiscard(NetworkObject net) {
		discard.add(net);
	}

}
