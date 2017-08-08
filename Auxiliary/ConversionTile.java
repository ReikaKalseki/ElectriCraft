/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.RotaryCraft.API.Power.ShaftMachine;

public interface ConversionTile extends ShaftMachine {

	public void setFacing(ForgeDirection dir);

	public ForgeDirection getFacing();

}
