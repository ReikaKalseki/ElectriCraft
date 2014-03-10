/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.API.Fillable;
import Reika.RotaryCraft.Entities.EntityDischarge;
import Reika.RotaryCraft.Registry.SoundRegistry;
import Reika.RotationalInduction.Induction;
import Reika.RotationalInduction.Base.InductionBlock;
import Reika.RotationalInduction.Registry.WireType;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public class BlockWire extends InductionBlock {

	private static final Icon[] textures = new Icon[WireType.wireList.length];
	private static final Icon[] insulTextures = new Icon[WireType.wireList.length];

	private static final Icon[] texturesEnd = new Icon[WireType.wireList.length];
	private static final Icon[] insulTexturesEnd = new Icon[WireType.wireList.length];

	public BlockWire(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setStepSound(soundClothFootstep);
		this.setHardness(0.5F);
		this.setResistance(4F);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityWire();
	}

	@Override
	public final int getRenderType() {
		return Induction.proxy.wireRender;
	}

	@Override
	public int idDropped(int id, Random r, int fortune) {
		return 0;
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

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		ItemStack is = te.insulated ? te.getWireType().getCraftedInsulatedProduct() : te.getWireType().getCraftedProduct();
		if (is.getItemDamage()%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setBoolean("fluid", true);
			is.stackTagCompound.setInteger("lvl", ((Fillable)is.getItem()).getCapacity(is));
		}
		li.add(is);
		return li;
	}

	@Override
	public void registerIcons(IconRegister ico) {
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType wire = WireType.wireList[i];
			textures[i] = ico.registerIcon("rotationalinduction:"+wire.getIconTexture());
			insulTextures[i] = ico.registerIcon("rotationalinduction:"+wire.getIconTexture()+"_ins");

			texturesEnd[i] = ico.registerIcon("rotationalinduction:"+wire.getIconTexture()+"_end");
			insulTexturesEnd[i] = ico.registerIcon("rotationalinduction:"+wire.getIconTexture()+"_ins_end");
		}
	}

	@Override
	public Icon getIcon(int s, int meta) {
		return textures[meta];
	}

	public static Icon getInsulatedEndTexture(WireType type) {
		return insulTexturesEnd[type.ordinal()];
	}

	public static Icon getEndTexture(WireType type) {
		return texturesEnd[type.ordinal()];
	}

	public static Icon getInsulatedTexture(WireType type) {
		return insulTextures[type.ordinal()];
	}

	public static Icon getTexture(WireType type) {
		return textures[type.ordinal()];
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		return pass == 0;
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.25;
		return ReikaAABBHelper.getBlockAABB(x, y, z).contract(d, d, d);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!world.isRemote) {
			if (!(e instanceof EntityPlayer) || (e instanceof EntityPlayer && !((EntityPlayer)e).capabilities.isCreativeMode)) {
				TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
				if (!te.insulated && te.getNetwork() != null) {
					if (te.getNetwork().isLive()) {
						EntityDischarge ed = new EntityDischarge(world, x+0.5, y+0.5, z+0.5, 3600, e.posX, e.posY, e.posZ);
						world.spawnEntityInWorld(ed);
						e.attackEntityFrom(RotaryCraft.shock, 5);
						if (e instanceof EntityCreeper) {
							world.createExplosion(e, e.posX, e.posY, e.posZ, 3F, true);
							e.attackEntityFrom(DamageSource.magic, Integer.MAX_VALUE);
						}
						e.addVelocity((e.posX-x)/4D, 0.33, (e.posZ-z)/4D);
						e.velocityChanged = true;
						SoundRegistry.SPARK.playSoundAtBlock(world, x, y, z, 1.5F, 1F);
						te.getNetwork().shortNetwork();
					}
				}
			}
		}
	}

}
