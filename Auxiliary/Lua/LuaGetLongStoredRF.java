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
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFBattery;

@ModTileDependent(value = {"cofh.api.energy.IEnergyProvider", "cofh.api.energy.IEnergyReceiver"})
public class LuaGetLongStoredRF extends LuaMethod {

	public LuaGetLongStoredRF() {
		super("getFullStoredEnergy", TileEntityRFBattery.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
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
