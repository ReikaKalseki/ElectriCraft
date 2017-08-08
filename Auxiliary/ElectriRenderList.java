/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import java.util.HashMap;

import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.Registry.ElectriTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElectriRenderList {

	private static HashMap<ElectriTiles, ElectriTERenderer> renders = new HashMap<ElectriTiles, ElectriTERenderer>();
	private static HashMap<ElectriTiles, ElectriTiles> overrides = new HashMap<ElectriTiles, ElectriTiles>();

	public static boolean addRender(ElectriTiles m, ElectriTERenderer r) {
		if (!renders.containsValue(r)) {
			renders.put(m, r);
			return true;
		}
		else {
			ElectriTiles parent = ReikaJavaLibrary.getHashMapKeyByValue(renders, r);
			overrides.put(m, parent);
			return false;
		}
	}

	public static ElectriTERenderer getRenderForMachine(ElectriTiles m) {
		if (overrides.containsKey(m))
			return renders.get(overrides.get(m));
		return renders.get(m);
	}

	public static String getRenderTexture(ElectriTiles m, RenderFetcher te) {
		return getRenderForMachine(m).getImageFileName(te);
	}

	public static ElectriTERenderer instantiateRenderer(ElectriTiles m) {
		try {
			ElectriTERenderer r = (ElectriTERenderer)Class.forName(m.getRenderer()).newInstance();
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
			throw new RegistrationException(ElectriCraft.instance, "No class found for Renderer "+m.getRenderer()+"!");
		}
	}

}
