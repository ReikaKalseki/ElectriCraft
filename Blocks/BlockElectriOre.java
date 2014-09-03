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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import Reika.ElectriCraft.Registry.ElectriOres;

public class BlockElectriOre extends Block {

	private IIcon[] icons = new IIcon[ElectriOres.oreList.length];

	public BlockElectriOre(Material par2Material) {
		super(par2Material);
		this.setResistance(5);
		this.setHardness(2);
		this.setCreativeTab(ElectriCraft.tabElectri);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		//ItemStack is = new ItemStack(ElectriBlocks.ORE.getBlock(), 1, metadata);
		ElectriOres ore = ElectriOres.getOre(this, metadata);
		li.addAll(ore.getOreDrop(metadata));
		if (!ore.dropsSelf(metadata))
			ReikaWorldHelper.splitAndSpawnXP(world, x+0.5F, y+0.5F, z+0.5F, this.droppedXP(ore));
		return li;
	}

	private int droppedXP(ElectriOres ore) {
		return ReikaRandomHelper.doWithChance(ore.xpDropped) ? 1 : 0;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv)
	{
		ElectriOres ore = ElectriOres.getOre(world, x, y, z);
		return super.removedByPlayer(world, player, x, y, z, harv);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition tgt, World world, int x, int y, int z)
	{
		ElectriOres ore = ElectriOres.getOre(world, x, y, z);
		return new ItemStack(ElectriBlocks.ORE.getBlockInstance(), 1, ore.getBlockMetadata());
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			icons[i] = ico.registerIcon(ElectriOres.oreList[i].getTextureName());
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

}
