/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.ElectriCraft.Auxiliary.Interfaces.CurrentThrottle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityResistorBase extends TileEntityWireComponent implements CurrentThrottle {

	private int selectedCurrent;

	public static enum ColorBand {
		BLACK(ReikaDyeHelper.BLACK),
		BROWN(ReikaDyeHelper.BROWN),
		RED(ReikaDyeHelper.RED),
		ORANGE(ReikaDyeHelper.ORANGE),
		YELLOW(ReikaDyeHelper.YELLOW),
		GREEN(ReikaDyeHelper.LIME),
		BLUE(ReikaDyeHelper.BLUE),
		PURPLE(ReikaDyeHelper.PURPLE),
		GRAY(ReikaDyeHelper.GRAY),
		WHITE(ReikaDyeHelper.WHITE);

		public final ReikaDyeHelper renderColor;

		public static final ColorBand[] bandList = values();

		private ColorBand(ReikaDyeHelper color) {
			renderColor = color;
		}

		public static ColorBand getBandFromColor(ReikaDyeHelper color) {
			for (int i = 0; i < bandList.length; i++) {
				ColorBand b = bandList[i];
				if (b.renderColor == color)
					return b;
			}
			return null;
		}
	}

	public abstract ColorBand[] getColorBands();
	protected abstract void setColorBands(ColorBand[] bands);

	@Override
	public final int getResistance() {
		return 0;
	}

	@Override
	public final int getCurrentLimit() {
		return selectedCurrent;
	}

	public final boolean setColor(ReikaDyeHelper color, int digit) {
		ColorBand band = color != null ? ColorBand.getBandFromColor(color) : null;
		if (band == null)
			return false;
		//ReikaJavaLibrary.pConsole(band);
		ColorBand[] bands = this.getColorBands();
		if (band.ordinal() > 7 && digit == bands.length)
			return false;
		this.assignBand(band, digit);
		selectedCurrent = this.calculateCurrentLimit();
		if (!worldObj.isRemote && network != null)
			network.updateWires();
		return true;
	}

	protected abstract int calculateCurrentLimit();

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void onNetworkChanged() {

	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		ColorBand[] bands = this.getColorBands();
		for (int i = 0; i < bands.length; i++) {
			int tag = i+1;
			int idx = NBT.getInteger("band"+tag);
			bands[i] = ColorBand.bandList[idx];
		}
		this.setColorBands(bands);

		selectedCurrent = NBT.getInteger("sel");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		ColorBand[] bands = this.getColorBands();
		for (int i = 0; i < bands.length; i++) {
			int tag = i+1;
			NBT.setInteger("band"+tag, bands[i].ordinal());
		}

		NBT.setInteger("sel", selectedCurrent);
	}

	@Override
	public final float getHeight() {
		return 0.75F;
	}

	@Override
	public final float getWidth() {
		return 0.5F;
	}

	@Override
	public final boolean canConnect() {
		return selectedCurrent > 0;
	}

	private void assignBand(ColorBand band, int digit) {
		ColorBand[] bands = this.getColorBands();
		bands[digit-1] = band;
		this.setColorBands(bands);
	}

	@SideOnly(Side.CLIENT)
	public final void setColor(ColorBand band, int digit) {
		this.assignBand(band, digit);
	}

}
