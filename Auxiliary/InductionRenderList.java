/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Auxiliary;

import java.util.HashMap;

import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.RotationalInduction.Induction;
import Reika.RotationalInduction.Base.InductionTERenderer;
import Reika.RotationalInduction.Registry.InductionTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InductionRenderList {

	private static HashMap<InductionTiles, InductionTERenderer> renders = new HashMap<InductionTiles, InductionTERenderer>();
	private static HashMap<InductionTiles, InductionTiles> overrides = new HashMap<InductionTiles, InductionTiles>();

	public static boolean addRender(InductionTiles m, InductionTERenderer r) {
		if (!renders.containsValue(r)) {
			renders.put(m, r);
			return true;
		}
		else {
			InductionTiles parent = ReikaJavaLibrary.getHashMapKeyByValue(renders, r);
			overrides.put(m, parent);
			return false;
		}
	}

	public static InductionTERenderer getRenderForMachine(InductionTiles m) {
		if (overrides.containsKey(m))
			return renders.get(overrides.get(m));
		return renders.get(m);
	}

	public static String getRenderTexture(InductionTiles m, RenderFetcher te) {
		return getRenderForMachine(m).getImageFileName(te);
	}

	public static InductionTERenderer instantiateRenderer(InductionTiles m) {
		try {
			InductionTERenderer r = (InductionTERenderer)Class.forName(m.getRenderer()).newInstance();
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
			throw new RegistrationException(Induction.instance, "No class found for Renderer "+m.getRenderer()+"!");
		}
	}

}
