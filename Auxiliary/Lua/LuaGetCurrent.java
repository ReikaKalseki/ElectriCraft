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
import Reika.ElectriCraft.Base.WiringTile;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetCurrent extends LuaMethod {

	public LuaGetCurrent() {
		super("getCurrent", WiringTile.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((WiringTile)te).getWireCurrent()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the wire current.";
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
