/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.Base.NetworkBlock;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.TileEntities.TileEntityBattery;

public class BlockElectricBattery extends NetworkBlock implements IWailaBlock {

	private final Icon[] bottomTex = new Icon[BatteryType.batteryList.length];
	private final Icon[] sideTex = new Icon[BatteryType.batteryList.length];
	private final Icon[] topTex = new Icon[BatteryType.batteryList.length];

	public BlockElectricBattery(int par1, Material par2Material) {
		super(par1, par2Material);
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
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			BatteryType b = BatteryType.batteryList[i];
			String s = b.name().toLowerCase();
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
	public Icon getIcon(int s, int meta) {
		if (s == 0)
			return bottomTex[meta];
		else if (s == 1)
			return topTex[meta];
		else
			return sideTex[meta];
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		if (!player.capabilities.isCreativeMode && this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlock(x, y, z, 0);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		if (world.getBlockId(x, y, z) == blockID)
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getBlockDropped(world, x, y, z, meta, 0));
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		TileEntityBattery te = (TileEntityBattery)world.getBlockTileEntity(x, y, z);
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
		TileEntityBattery te = (TileEntityBattery)world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack is = ElectriItems.BATTERY.getStackOfMetadata(meta);
		return is;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		TileEntityBattery te = (TileEntityBattery)acc.getTileEntity();
		tip.add(String.format("Stored Energy: %s", te.getDisplayEnergy()));
		tip.add(String.format("Capacity: %s", te.getBatteryType().getFormattedCapacity()));
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		return tip;
	}

}
