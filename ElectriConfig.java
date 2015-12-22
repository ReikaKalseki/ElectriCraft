/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import java.util.ArrayList;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Registry.ElectriOres;

public class ElectriConfig extends ControlledConfig {

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ElectriOres.class);
	private DataElement<Boolean>[] ores = new DataElement[entries.size()];

	public ElectriConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id, int cfg) {
		super(mod, option, id, cfg);

		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			ores[i] = this.registerAdditionalOption("Ore Control", name, true);
		}
	}

	public boolean isOreGenEnabled(ElectriOres ore) {
		return ores[ore.ordinal()].getData();
	}

}
