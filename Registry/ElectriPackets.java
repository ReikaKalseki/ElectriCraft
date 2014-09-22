/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import Reika.DragonAPI.Auxiliary.PacketTypes;

public enum ElectriPackets {

	RFCABLE(1),
	TRANSFORMER(2);

	public final int numInts;
	public final PacketTypes type;

	private ElectriPackets() {
		this(0);
	}

	private ElectriPackets(int size) {
		this(size, PacketTypes.DATA);
	}

	private ElectriPackets(int size, PacketTypes t) {
		numInts = size;
		type = t;
	}

	public static final ElectriPackets getEnum(int id) {
		ElectriPackets[] list = values();
		id = Math.max(0, Math.min(id, list.length-1));
		return list[id];
	}

	public boolean hasData() {
		return numInts > 0;
	}

}
