/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectroCraft.ElectroCraft;
import Reika.ElectroCraft.Registry.ElectroBlocks;
import Reika.ElectroCraft.Registry.ElectroOres;

public class BlockElectroOre extends Block {

	private Icon[] icons = new Icon[ElectroOres.oreList.length];

	public BlockElectroOre(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setResistance(5);
		this.setHardness(2);
		this.setCreativeTab(ElectroCraft.tabElectro);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		//ItemStack is = new ItemStack(ElectroBlocks.ORE.getBlockID(), 1, metadata);
		ElectroOres ore = ElectroOres.getOre(blockID, metadata);
		li.addAll(ore.getOreDrop(metadata));
		if (!ore.dropsSelf(metadata))
			ReikaWorldHelper.splitAndSpawnXP(world, x+0.5F, y+0.5F, z+0.5F, this.droppedXP(ore));
		return li;
	}

	private int droppedXP(ElectroOres ore) {
		return ReikaRandomHelper.doWithChance(ore.xpDropped) ? 1 : 0;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		ElectroOres ore = ElectroOres.getOre(world, x, y, z);
		return super.removeBlockByPlayer(world, player, x, y, z);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition tgt, World world, int x, int y, int z)
	{
		ElectroOres ore = ElectroOres.getOre(world, x, y, z);
		return new ItemStack(ElectroBlocks.ORE.getBlockID(), 1, ore.getBlockMetadata());
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < ElectroOres.oreList.length; i++) {
			icons[i] = ico.registerIcon(ElectroOres.oreList[i].getTextureName());
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return icons[meta];
	}

}
