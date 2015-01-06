/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.ModInteract.Lua.LuaMethod;
import Reika.ElectriCraft.Base.WiringTile;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetVoltage extends LuaMethod {

	public LuaGetVoltage() {
		super("getVoltage", WiringTile.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((WiringTile)te).getWireVoltage()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the wire voltage.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
