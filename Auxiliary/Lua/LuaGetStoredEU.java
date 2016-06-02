/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityEUBattery;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetStoredEU extends LuaMethod {

	public LuaGetStoredEU() {
		super("getFullStoredEnergy", TileEntityEUBattery.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
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
