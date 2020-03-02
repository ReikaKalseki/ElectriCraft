/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Base.TileEntityResistorBase;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityPreciseResistor extends TileEntityResistorBase {

	/** Use these to control current limits; change by right-clicking with dye */
	private ColorBand[] bands = {ColorBand.BLACK, ColorBand.BLACK, ColorBand.BLACK, ColorBand.BLACK};

	@Override
	protected int calculateCurrentLimit() {
		int digit1 = bands[0].ordinal();
		int digit2 = bands[1].ordinal();
		int digit3 = bands[2].ordinal();
		int multiplier = bands[3].ordinal();
		int base = Integer.parseInt(String.format("%d%d%d", digit1, digit2, digit3));
		return base*ReikaMathLibrary.intpow2(10, multiplier);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.PRECISERESISTOR;
	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	public ColorBand[] getColorBands() {
		return bands;
	}

	@Override
	protected void setColorBands(ColorBand[] bands) {
		this.bands = bands;
	}

}
