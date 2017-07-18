package Reika.ElectriCraft.TileEntities;

import java.util.Locale;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Registry.BlockRegistry;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;


public class TileEntityWirelessCharger extends ElectriTileEntity implements IEnergyHandler {

	//private ForgeDirection facing = ForgeDirection.UP;

	public ForgeDirection getFacing() {
		//return facing != null ? facing : ForgeDirection.UP;
		switch (this.getBlockMetadata()) {
			case 0:
				return ForgeDirection.WEST;
			case 1:
				return ForgeDirection.EAST;
			case 2:
				return ForgeDirection.NORTH;
			case 3:
				return ForgeDirection.SOUTH;
			case 4:
				return ForgeDirection.UP;
			case 5:
				return ForgeDirection.DOWN;
		}
		return ForgeDirection.UP;
	}

	private IEnergyReceiver getTileEntity() {
		ForgeDirection dir = this.getFacing();
		int x = xCoord+dir.offsetX*2;
		int y = yCoord+dir.offsetY*2;
		int z = zCoord+dir.offsetZ*2;
		TileEntity te = worldObj.getTileEntity(x, y, z);
		return te instanceof IEnergyReceiver ? (IEnergyReceiver)te : null;
	}

	public ChargerTiers getTier() {
		return ChargerTiers.tierList[this.getRealBlockMetadata()];
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.WIRELESSPAD;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		//NBT.setInteger("dir", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		//facing = dirs[NBT.getInteger("facing")];
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from != this.getFacing();
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		IEnergyReceiver ier = this.getTileEntity();
		float f = this.getTier().efficiency;
		maxReceive = Math.min(maxReceive, this.getTier().maxThroughput);
		return ier != null ? (int)(ier.receiveEnergy(this.getFacing().getOpposite(), (int)(maxReceive*f), simulate)/f) : 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		IEnergyReceiver ier = this.getTileEntity();
		return ier != null ? ier.getEnergyStored(this.getFacing().getOpposite()) : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		IEnergyReceiver ier = this.getTileEntity();
		return ier != null ? ier.getMaxEnergyStored(this.getFacing().getOpposite()) : 0;
	}

	public static enum ChargerTiers {
		BASIC(0.4F, 80),
		IMPROVED(0.6F, 800),
		ADVANCED(0.8F, 6000),
		HIGHTECH(0.9F, 40000),
		SUPERCONDUCTING(1F, Integer.MAX_VALUE);

		public final float efficiency;
		public final int maxThroughput;

		public static final ChargerTiers[] tierList = values();

		private ChargerTiers(float f, int th) {
			efficiency = f;
			maxThroughput = th;
		}

		public Object[] getRecipe() {
			Object[] sides = {Blocks.glass, Blocks.glass, Blocks.glass, BlockRegistry.BLASTGLASS.getBlockInstance(), BlockRegistry.BLASTGLASS.getBlockInstance()};
			Object[] cores = {Items.redstone, Items.gold_ingot, Items.diamond, ItemStacks.redgoldingot, ItemStacks.enderium.getItem()};
			Object[] ret = {"AEA", "SCS", "PRP", 'A', ItemStacks.steelingot, 'E', Items.ender_pearl, 'P', ItemStacks.basepanel, 'R', ItemStacks.silicon, 'S', sides[this.ordinal()], 'C', cores[this.ordinal()]};
			return ret;
		}

		public String getLocalizedName() {
			return StatCollector.translateToLocal("electrichargepad."+this.name().toLowerCase(Locale.ENGLISH));
		}

		public static String getDataForDisplay() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tierList.length; i++) {
				ChargerTiers type = tierList[i];
				if (type == ChargerTiers.SUPERCONDUCTING)
					sb.append(type.getLocalizedName()+": No limit @ 100% efficiency");
				else
					sb.append(String.format("%s: Max %d RF/t @ %.0f%% efficiency", type.getLocalizedName(), type.maxThroughput, 100*type.efficiency));
				sb.append("\n\n");
			}
			return sb.toString();
		}
	}

}
