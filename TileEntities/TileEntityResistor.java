package Reika.ElectriCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityResistor extends WiringTile {

	private int selectedCurrent;

	/** Use these to control current limits; change by right-clicking with dye */
	private ColorBand b1;
	private ColorBand b2;
	private ColorBand multiplier;

	public static enum ColorBand {
		BLACK(ReikaDyeHelper.BLACK),
		BROWN(ReikaDyeHelper.BROWN),
		RED(ReikaDyeHelper.RED),
		ORANGE(ReikaDyeHelper.ORANGE),
		YELLOW(ReikaDyeHelper.YELLOW),
		GREEN(ReikaDyeHelper.LIME),
		BLUE(ReikaDyeHelper.BLUE),
		PURPLE(ReikaDyeHelper.MAGENTA);

		public final ReikaDyeHelper renderColor;

		public static final ColorBand[] bandList = values();

		private ColorBand(ReikaDyeHelper color) {
			renderColor = color;
		}

		public final int getValue() {
			return this.ordinal();
		}
	}

	@Override
	public int getResistance() {
		return 0;
	}

	@Override
	public int getCurrentLimit() {
		return selectedCurrent;
	}

	public void setColors(ColorBand b1, ColorBand b2, ColorBand multiplier) {
		network.updateWires();
	}

	@Override
	public void overCurrent() {

	}

	@Override
	public int getIndex() {
		return ElectriTiles.LIMITER.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void onNetworkChanged() {

	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		colors = NBT.getIntArray("color");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setIntArray("color", colors);
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing() || dir == getFacing.getOpposite();
	}

}
