/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import net.minecraftforge.common.util.ForgeDirection;

public interface WireReceiver extends WireTerminus {

	public boolean canReceivePowerFromSide(ForgeDirection dir);

	public boolean canReceivePower();

}