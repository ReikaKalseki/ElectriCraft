/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;


public interface NetworkObject {

	public void tick(ElectriNetworkTickEvent evt);

	public void repath(ElectriNetworkRepathEvent evt);

}
