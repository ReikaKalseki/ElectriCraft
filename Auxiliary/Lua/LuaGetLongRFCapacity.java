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
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFBattery;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetLongRFCapacity extends LuaMethod {

	public LuaGetLongRFCapacity() {
		super("getFullCapacity", TileEntityRFBattery.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((TileEntityRFBattery)te).getMaxEnergy()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the real RF capacity.\nArgs: None\nReturns: Capacity";
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
