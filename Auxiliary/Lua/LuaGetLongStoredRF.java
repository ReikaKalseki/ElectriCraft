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
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFBattery;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetLongStoredRF extends LuaMethod {

	public LuaGetLongStoredRF() {
		super("getFullStoredEnergy", TileEntityRFBattery.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((TileEntityRFBattery)te).getStoredEnergy()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the real stored RF value.\nArgs: None\nReturns: Energy";
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
