package Reika.ElectriCraft.Base;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.Auxiliary.BatteryTile;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;


@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public abstract class BatteryBlock extends Block implements IWailaDataProvider {

	private final IIcon[] bottomTex = new IIcon[1];
	private final IIcon[] sideTex = new IIcon[1];
	private final IIcon[] topTex = new IIcon[1];

	protected BatteryBlock(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(10);
	}

	public abstract ElectriTiles getTile();
	public abstract ElectriItems getItem();

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		if (!player.capabilities.isCreativeMode && this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public final void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		if (world.getBlock(x, y, z) == this)
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getDrops(world, x, y, z, meta, 0));
	}

	private final boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		return true;
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		BatteryTile te = (BatteryTile)world.getTileEntity(x, y, z);
		long e = te.getStoredEnergy();
		ItemStack is = this.getItem().getStackOfMetadata(meta);
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setLong("nrg", e);
		li.add(is);
		return li;
	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		BatteryTile te = (BatteryTile)world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack is = this.getItem().getStackOfMetadata(meta);
		return is;
	}

	@Override
	public final boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public final int getComparatorInputOverride(World world, int x, int y, int z, int par5)
	{
		return ((TileEntityBase)world.getTileEntity(x, y, z)).getRedstoneOverride();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public abstract TileEntity createTileEntity(World world, int meta);

	@Override
	public final void registerBlockIcons(IIconRegister ico) {/*
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			BatteryType b = BatteryType.batteryList[i];
			String s = b.name().toLowerCase();
			sideTex[i] = ico.registerIcon("electricraft:battery/"+s);
			s = "";
			topTex[i] = ico.registerIcon("electricraft:battery/"+s+"_top");
			bottomTex[i] = ico.registerIcon("electricraft:battery/"+s+"_bottom");
		}*/

		String s = this.getTextureSubstring();
		sideTex[0] = ico.registerIcon("electricraft:battery/"+s);
		s = "";
		topTex[0] = ico.registerIcon("electricraft:battery/"+s+"_top");
		bottomTex[0] = ico.registerIcon("electricraft:battery/"+s+"_bottom");
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (s == 0)
			return bottomTex[meta];
		else if (s == 1)
			return topTex[meta];
		else
			return sideTex[meta];
	}

	protected abstract String getTextureSubstring();

	@Override
	@ModDependent(ModList.WAILA)
	public final ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return null;
	}

	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		World world = acc.getWorld();
		TileEntity te = acc.getTileEntity();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			currenttip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		BatteryTile te = (BatteryTile)acc.getTileEntity();
		((TileEntityBase)te).syncAllData(false);
		tip.add(String.format("Stored Energy: %s", te.getDisplayEnergy()));
		tip.add(String.format("Capacity: %s", te.getFormattedCapacity()));
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		currenttip.add(s2+s1+"ElectriCraft");*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
