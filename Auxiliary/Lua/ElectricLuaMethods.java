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
import Reika.DragonAPI.ModRegistry.PowerTypes;

public class ElectricLuaMethods {

	private static final LuaMethod getVoltage = new LuaGetVoltage();
	private static final LuaMethod getCurrent = new LuaGetCurrent();
	private static final LuaMethod getElectricPower = new LuaGetElectricPower();
	private static final LuaMethod getStorage = new LuaGetStoredEnergy();

	private static final LuaMethod getRFStorage;
	private static final LuaMethod getRFCapacity;
	private static final LuaMethod getEUStorage;
	private static final LuaMethod getEUCapacity;

	static {
		if (PowerTypes.RF.isLoaded()) {
			getRFStorage = new LuaGetLongStoredRF();
			getRFCapacity = new LuaGetLongRFCapacity();
		}
		else {
			getRFStorage = null;
			getRFCapacity = null;
		}

		if (PowerTypes.EU.isLoaded()) {
			getEUStorage = new LuaGetStoredEU();
			getEUCapacity = new LuaGetEUCapacity();
		}
		else {
			getEUStorage = null;
			getEUCapacity = null;
		}
	}

}
