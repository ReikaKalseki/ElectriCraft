/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriBlock;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityMotor;
import Reika.ElectriCraft.TileEntities.TileEntityRelay;
import Reika.ElectriCraft.TileEntities.TileEntityResistor;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.RotaryCraft.Auxiliary.RotaryAux;
import Reika.RotaryCraft.Auxiliary.Interfaces.NBTMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Registry.ItemRegistry;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockElectricMachine extends ElectriBlock implements IWailaDataProvider {

	public BlockElectricMachine(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return ElectriTiles.createTEFromIDAndMetadata(this, meta);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return ElectriTiles.getTE(world, x, y, z).getCraftedProduct();
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return null;
	}


	@Override
	public int damageDropped(int par1)
	{
		return ElectriTiles.getMachineFromIDandMetadata(this, par1).ordinal();
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 1;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlockToAir(x, y, z);
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		if (ep.capabilities.isCreativeMode)
			return false;
		return RotaryAux.canHarvestSteelMachine(ep);
	}

	@Override
	public final void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		TileEntity te = world.getTileEntity(x, y, z);
		ElectriTiles m = ElectriTiles.getTE(world, x, y, z);
		if (m != null) {
			ItemStack is = m.getCraftedProduct();
			if (m.hasNBTVariants()) {
				NBTTagCompound nbt = ((NBTMachine)te).getTagsToWriteToStack();
				is.stackTagCompound = (NBTTagCompound)(nbt != null ? nbt.copy() : null);
			}
			ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
		}
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		ElectriTiles t = ElectriTiles.getTE(world, x, y, z);
		if (t != null) {
			if (t.isSpecialWiringPiece()) {
				AxisAlignedBB box = ((TileEntityWireComponent)world.getTileEntity(x, y, z)).getAABB();
				return box;
			}
			else if (t == ElectriTiles.TRANSFORMER) {
				AxisAlignedBB box = ((TileEntityTransformer)world.getTileEntity(x, y, z)).getAABB();
				return box;
			}
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float a, float b, float c) {
		ElectriTiles e = ElectriTiles.getTE(world, x, y, z);
		ItemStack is = ep.getCurrentEquippedItem();
		ItemRegistry ir = ItemRegistry.getEntry(is);
		if (ir != null && ir.overridesRightClick(is))
			return false;
		if (e == ElectriTiles.RESISTOR && ReikaDyeHelper.isDyeItem(is)) {
			TileEntityResistor te = (TileEntityResistor)world.getTileEntity(x, y, z);
			ForgeDirection dir = te.getFacing();
			float inc = dir.offsetX != 0 ? a : c;
			if (dir.offsetX > 0 || dir.offsetZ > 0)
				inc = 1F-inc;
			if (te.isFlipped && dir.offsetZ != 0) {
				inc = 1-inc;
			}
			int band = 0;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(0.125, 0.25, inc))
				band = 1;
			else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.3125, 0.4375, inc))
				band = 2;
			else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.5, 0.625, inc))
				band = 3;
			if (band > 0) {
				if (te.setColor(ReikaDyeHelper.getColorFromItem(is), band)) {
					if (!ep.capabilities.isCreativeMode)
						is.stackSize--;
					return true;
				}
				else {
					return false;
				}
			}
		}
		if (e == ElectriTiles.MOTOR) {
			TileEntityMotor te = (TileEntityMotor)world.getTileEntity(x, y, z);
			if (is != null && te.upgrade(is)) {
				if (!ep.capabilities.isCreativeMode)
					is.stackSize--;
				return true;
			}
		}
		if (e == ElectriTiles.TRANSFORMER) {
			ep.openGui(ElectriCraft.instance, 0, world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		/*
		World world = acc.getWorld();
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
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		//if (/*LegacyWailaHelper.cacheAndReturn(acc)*/!tip.isEmpty())
		//	return tip;
		TileEntity te = acc.getTileEntity();
		if (te instanceof TileEntityResistor) {
			int limit = ((TileEntityResistor) te).getCurrentLimit();
			tip.add(String.format("Current Limit: %dA", limit));
		}
		if (te instanceof TileEntityRelay) {
			tip.add(((TileEntityRelay) te).isEnabled() ? "Connected" : "Disconnected");
		}
		if (te instanceof TileEntityTransformer) {
			tip.add(String.format("Ratio: %s", ((TileEntityTransformer)te).getRatioForDisplay()));
		}
		if (te instanceof TemperatureTE) {
			tip.add(String.format("Temperature: %dC", ((TemperatureTE)te).getTemperature()));
		}
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
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
