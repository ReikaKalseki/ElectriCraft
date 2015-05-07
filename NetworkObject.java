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

import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public interface NetworkObject {

	@SubscribeEvent
	public void tick(ElectriNetworkTickEvent evt);

	@SubscribeEvent
	public void repath(ElectriNetworkRepathEvent evt);

}
