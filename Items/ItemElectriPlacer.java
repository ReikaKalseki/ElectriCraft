/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Auxiliary.ConversionTile;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.ElectriCraft.TileEntities.TileEntityWirelessCharger;
import Reika.RotaryCraft.API.Power.ShaftMachine;
import Reika.RotaryCraft.Auxiliary.RotaryAux;
import Reika.RotaryCraft.Auxiliary.Interfaces.NBTMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElectriPlacer extends Item {

	public ItemElectriPlacer(int tex) {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		maxStackSize = 64;
		this.setCreativeTab(ElectriCraft.tabElectri);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (!this.checkValidBounds(is, ep, world, x, y, z))
			return false;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		ElectriTiles m = ElectriTiles.TEList[is.getItemDamage()];
		if (m == ElectriTiles.MOTOR && (ElectriTiles.getTE(world, x, y+1, z) == m || ElectriTiles.getTE(world, x, y-1, z) == m))
			return false;
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else {
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, m.getBlock(), this.getMeta(m, is), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, m.getPlaceSound(), 1F, 1.5F);
		ElectriTileEntity te = (ElectriTileEntity)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		te.setBlockMetadata(m.is6Sided() ? RotaryAux.get6SidedMetadataFromPlayerLook(ep) : RotaryAux.get4SidedMetadataFromPlayerLook(ep));
		te.isFlipped = RotaryAux.shouldSetFlipped(world, x, y, z);
		if (te instanceof ShaftMachine) {
			ShaftMachine sm = (ShaftMachine)te;
			sm.setIORenderAlpha(512);
		}
		if (te instanceof NBTMachine) {
			((NBTMachine)te).setDataFromItemStackTag(is.stackTagCompound);
		}
		if (te instanceof TileEntityWireComponent) {
			ForgeDirection dir = ReikaEntityHelper.getDirectionFromEntityLook(ep, false);
			//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
			((TileEntityWireComponent)te).setFacing(dir);
		}
		if (te instanceof TileEntityTransformer) {
			ForgeDirection dir = ReikaDirectionHelper.getRightBy90(ReikaEntityHelper.getDirectionFromEntityLook(ep, false));
			//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
			((TileEntityTransformer)te).setFacing(dir);
		}
		if (te instanceof ConversionTile) {
			ForgeDirection dir = ReikaEntityHelper.getDirectionFromEntityLook(ep, false);
			((ConversionTile)te).setFacing(dir);
		}

		return true;
	}

	private int getMeta(ElectriTiles m, ItemStack is) {
		if (m == ElectriTiles.WIRELESSPAD) {
			int tier = is.stackTagCompound != null ? is.stackTagCompound.getInteger("tier") : 0;
			return tier;
		}
		return m.getBlockMetadata();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
			ElectriTiles t = ElectriTiles.TEList[i];
			if (!t.hasCustomItem() && t.isAvailableInCreativeInventory()) {
				TileEntity te = t.createTEInstanceForRender();
				ItemStack item = t.getCraftedProduct();
				if (t == ElectriTiles.WIRELESSPAD) {
					for (int k = 0; k < TileEntityWirelessCharger.ChargerTiers.tierList.length; k++) {
						ItemStack is = item.copy();
						is.stackTagCompound = new NBTTagCompound();
						is.stackTagCompound.setInteger("tier", k);
						par3List.add(is);
					}
					return;
				}
				else if (t.hasNBTVariants()) {
					ArrayList<NBTTagCompound> li = ((NBTMachine)te).getCreativeModeVariants();
					for (int k = 0; k < li.size(); k++) {
						NBTTagCompound NBT = li.get(k);
						ItemStack is = item.copy();
						is.stackTagCompound = NBT;
						par3List.add(is);
					}
				}
				par3List.add(item);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean verbose) {
		int i = is.getItemDamage();
		ElectriTiles m = ElectriTiles.TEList[i];
		TileEntity te = m.createTEInstanceForRender();
		if (m.hasNBTVariants() && is.stackTagCompound != null) {
			li.addAll(((NBTMachine)te).getDisplayTags(is.stackTagCompound));
		}
		if (m == ElectriTiles.WIRELESSPAD && is.stackTagCompound != null) {
			li.add(TileEntityWirelessCharger.ChargerTiers.tierList[is.stackTagCompound.getInteger("tier")].getLocalizedName());
		}
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		return y > 0 && y < world.provider.getHeight()-1;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ElectriItems ir = ElectriItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

}
