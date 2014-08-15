/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Items;

import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Auxiliary.ConversionTile;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.ShaftMachine;
import Reika.RotaryCraft.Auxiliary.RotaryAux;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, m.getBlock(), m.getBlockMetadata(), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, m.getPlaceSound(), 1F, 1.5F);
		ElectriTileEntity te = (ElectriTileEntity)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		te.setBlockMetadata(RotaryAux.get4SidedMetadataFromPlayerLook(ep));
		te.isFlipped = RotaryAux.shouldSetFlipped(world, x, y, z);
		if (te instanceof ShaftMachine) {
			ShaftMachine sm = (ShaftMachine)te;
			sm.setIORenderAlpha(512);
		}
		if (te instanceof TileEntityWireComponent) {
			ForgeDirection dir = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false);
			//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
			((TileEntityWireComponent)te).setFacing(dir);
		}
		if (te instanceof ConversionTile) {
			ForgeDirection dir = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false);
			((ConversionTile)te).setFacing(dir);
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
			ElectriTiles t = ElectriTiles.TEList[i];
			if (!t.hasCustomItem() && t.isAvailableInCreativeInventory()) {
				ItemStack item = new ItemStack(par1, 1, i);
				par3List.add(item);
			}
		}
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		return true;
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