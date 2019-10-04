/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod.ModTileDependent;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityEUBattery;

@ModTileDependent(value = "ic2.api.energy.tile.IEnergyTile")
public class LuaGetStoredEU extends LuaMethod {

	public LuaGetStoredEU() {
		super("getFullStoredEnergy", TileEntityEUBattery.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[]{((TileEntityEUBattery)te).getStoredEnergy()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the stored EU value.\nArgs: None\nReturns: Energy";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.LONG;
	}

}
