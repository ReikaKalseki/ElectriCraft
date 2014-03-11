/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Auxiliary;

import java.util.HashMap;

import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectroCraft.ElectroCraft;
import Reika.ElectroCraft.Base.ElectroTERenderer;
import Reika.ElectroCraft.Registry.ElectroTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElectroRenderList {

	private static HashMap<ElectroTiles, ElectroTERenderer> renders = new HashMap<ElectroTiles, ElectroTERenderer>();
	private static HashMap<ElectroTiles, ElectroTiles> overrides = new HashMap<ElectroTiles, ElectroTiles>();

	public static boolean addRender(ElectroTiles m, ElectroTERenderer r) {
		if (!renders.containsValue(r)) {
			renders.put(m, r);
			return true;
		}
		else {
			ElectroTiles parent = ReikaJavaLibrary.getHashMapKeyByValue(renders, r);
			overrides.put(m, parent);
			return false;
		}
	}

	public static ElectroTERenderer getRenderForMachine(ElectroTiles m) {
		if (overrides.containsKey(m))
			return renders.get(overrides.get(m));
		return renders.get(m);
	}

	public static String getRenderTexture(ElectroTiles m, RenderFetcher te) {
		return getRenderForMachine(m).getImageFileName(te);
	}

	public static ElectroTERenderer instantiateRenderer(ElectroTiles m) {
		try {
			ElectroTERenderer r = (ElectroTERenderer)Class.forName(m.getRenderer()).newInstance();
			if (addRender(m, r))
				return r;
			else
				return renders.get(overrides.get(m));
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Tried to call nonexistent render "+m.getRenderer()+"!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Tried to call illegal render "+m.getRenderer()+"!");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RegistrationException(ElectroCraft.instance, "No class found for Renderer "+m.getRenderer()+"!");
		}
	}

}
