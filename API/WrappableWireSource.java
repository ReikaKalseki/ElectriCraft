/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.API;

import net.minecraftforge.common.util.ForgeDirection;

import Reika.RotaryCraft.API.Power.ShaftMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.PowerSourceTracker;

public interface WrappableWireSource extends ShaftMachine, PowerSourceTracker {

	public boolean canConnectToSide(ForgeDirection dir);

	public boolean isFunctional();

	public boolean hasPowerStatusChangedSinceLastTick();

}
