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

import Reika.RotaryCraft.API.ShaftMachine;

import net.minecraftforge.common.util.ForgeDirection;

public interface ConversionTile extends ShaftMachine {

	public void setFacing(ForgeDirection dir);

	public ForgeDirection getFacing();

}