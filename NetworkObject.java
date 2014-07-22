/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;
import net.minecraftforge.event.ForgeSubscribe;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkTickEvent;


public interface NetworkObject {

	@ForgeSubscribe
	public void tick(ElectriNetworkTickEvent evt);

}
