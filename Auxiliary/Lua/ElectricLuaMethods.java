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

import Reika.DragonAPI.ModInteract.Lua.LuaMethod;

public class ElectricLuaMethods {

	private static final LuaMethod getVoltage = new LuaGetVoltage();
	private static final LuaMethod getCurrent = new LuaGetCurrent();
	private static final LuaMethod getElectricPower = new LuaGetElectricPower();
	private static final LuaMethod getStorage = new LuaGetStoredEnergy();

}
