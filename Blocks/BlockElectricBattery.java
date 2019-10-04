/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Auxiliary.Interfaces.BatteryTile;
import Reika.ElectriCraft.Base.NetworkBlock;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.TileEntities.TileEntityBattery;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockElectricBattery extends NetworkBlock implements IWailaDataProvider {

	private final IIcon[] bottomTex = new IIcon[BatteryType.batteryList.length];
	private final IIcon[] sideTex = new IIcon[BatteryType.batteryList.length];
	private final IIcon[] topTex = new IIcon[BatteryType.batteryList.length];

	public BlockElectricBattery(Material par2Material) {
		super(par2Material);
		this.setHardness(2);
		this.setResistance(10);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityBattery();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			BatteryType b = BatteryType.batteryList[i];
			String s = b.name().toLowerCase(Locale.ENGLISH);
			sideTex[i] = ico.registerIcon("electricraft:battery/"+s);
			s = "";
			topTex[i] = ico.registerIcon("electricraft:battery/"+s+"_top");
			bottomTex[i] = ico.registerIcon("electricraft:battery/"+s+"_bottom");
		}
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

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		if (!player.capabilities.isCreativeMode && this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		if (world.getBlock(x, y, z) == this)
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getDrops(world, x, y, z, meta, 0));
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		TileEntityBattery te = (TileEntityBattery)world.getTileEntity(x, y, z);
		long e = te.getStoredEnergy();
		ItemStack is = ElectriItems.BATTERY.getStackOfMetadata(meta);
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setLong("nrg", e);
		li.add(is);
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		TileEntityBattery te = (TileEntityBattery)world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack is = ElectriItems.BATTERY.getStackOfMetadata(meta);
		return is;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return null;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		World world = acc.getWorld();
		TileEntity te = acc.getTileEntity();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			currenttip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}
		 */
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		BatteryTile te = (BatteryTile)acc.getTileEntity();
		((TileEntityBase)te).syncAllData(false);
		tip.add(String.format("Stored Energy: %s", te.getDisplayEnergy()));
		tip.add(String.format("Capacity: %s", te.getFormattedCapacity()));
		tip.add(te.getTracker().getAverageFlow(0)+" J/t");
		tip.add(te.getTracker().getTimeUntilFullOrEmpty(te));
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		currenttip.add(s2+s1+"ElectriCraft");*/
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

	@Override
	public int getRenderType() {
		return ElectriCraft.proxy.batteryRender;
	}

}
