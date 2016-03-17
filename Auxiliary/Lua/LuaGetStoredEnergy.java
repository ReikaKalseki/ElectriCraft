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
import Reika.ElectriCraft.TileEntities.TileEntityBattery;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetStoredEnergy extends LuaMethod {

	public LuaGetStoredEnergy() {
		super("getStoredEnergy", TileEntityBattery.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((TileEntityBattery)te).getStoredEnergy(), ((TileEntityBattery)te).getMaxEnergy()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the stored energy.";
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
