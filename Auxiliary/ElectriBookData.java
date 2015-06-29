/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import Reika.DragonAPI.Instantiable.Data.Maps.ArrayMap;
import Reika.ElectriCraft.Registry.ElectriBook;

public class ElectriBookData {

	private static final ArrayMap<ElectriBook> tabMappings = new ArrayMap(2);

	private static void mapHandbook() {
		for (int i = 0; i < ElectriBook.tabList.length; i++) {
			ElectriBook h = ElectriBook.tabList[i];
			tabMappings.putV(h, h.getScreen(), h.getPage());
		}
	}

	public static ElectriBook getMapping(int screen, int page) {
		return tabMappings.getV(screen, page);
	}

	static {
		mapHandbook();
	}
}
