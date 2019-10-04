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
public class LuaGetEUCapacity extends LuaMethod {

	public LuaGetEUCapacity() {
		super("getFullCapacity", TileEntityEUBattery.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[]{((TileEntityEUBattery)te).getMaxEnergy()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the EU capacity.\nArgs: None\nReturns: Capacity";
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
