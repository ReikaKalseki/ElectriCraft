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
import java.util.Random;

import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriBlock;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.API.Fillable;
import Reika.RotaryCraft.Entities.EntityDischarge;
import Reika.RotaryCraft.Registry.SoundRegistry;

public class BlockWire extends ElectriBlock implements IWailaBlock {

	private static final Icon[] textures = new Icon[WireType.wireList.length];
	private static final Icon[] insulTextures = new Icon[WireType.wireList.length];

	private static final Icon[] texturesEnd = new Icon[WireType.wireList.length];
	private static final Icon[] insulTexturesEnd = new Icon[WireType.wireList.length];

	public BlockWire(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setStepSound(soundClothFootstep);
		this.setHardness(0.5F);
		this.setResistance(4F);
		this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityWire();
	}

	@Override
	public final int getRenderType() {
		return ElectriCraft.proxy.wireRender;
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
			textures[i] = ico.registerIcon("ElectriCraft:"+wire.getIconTexture());
			insulTextures[i] = ico.registerIcon("ElectriCraft:"+wire.getIconTexture()+"_ins");

			texturesEnd[i] = ico.registerIcon("ElectriCraft:"+wire.getIconTexture()+"_end");
			insulTexturesEnd[i] = ico.registerIcon("ElectriCraft:"+wire.getIconTexture()+"_ins_end");
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
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		float minx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.WEST) ? 0 : 0.375F;
		float maxx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.EAST) ? 1 : 0.625F;
		float minz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.SOUTH) ? 0 : 0.375F;
		float maxz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.NORTH) ? 1 : 0.625F;
		float miny = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.UP) ? 0 : 0.375F;
		float maxy = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.DOWN) ? 1 : 0.625F;
		return AxisAlignedBB.getAABBPool().getAABB(x+minx, y+miny, z+minz, x+maxx, y+maxy, z+maxz);
	}

	public void getBlockRenderBounds() {

	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!world.isRemote) {
			if (!(e instanceof EntityPlayer) || (e instanceof EntityPlayer && !((EntityPlayer)e).capabilities.isCreativeMode)) {
				TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
				WireNetwork net = te.getNetwork();
				if (!te.insulated && net != null) {
					if (net.isLive()) {
						int v = net.getPointVoltage(te);
						EntityDischarge ed = new EntityDischarge(world, x+0.5, y+0.5, z+0.5, v, e.posX, e.posY, e.posZ);
						world.spawnEntityInWorld(ed);
						e.attackEntityFrom(RotaryCraft.shock, v > 10000 ? 20 : v > 1000 ? 10 : v > 100 ? 5 : v > 10 ? 1 : 0);
						if (e instanceof EntityCreeper) {
							world.createExplosion(e, e.posX, e.posY, e.posZ, 3F, true);
							e.attackEntityFrom(DamageSource.magic, Integer.MAX_VALUE);
						}
						if (v > 100) {
							e.addVelocity((e.posX-x)/4D, 0.33, (e.posZ-z)/4D);
							e.velocityChanged = true;
						}
						SoundRegistry.SPARK.playSoundAtBlock(world, x, y, z, 1.5F, 1F);
						te.getNetwork().shortNetwork();
					}
				}
			}
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		ItemStack is = te.insulated ? te.getWireType().getCraftedInsulatedProduct() : te.getWireType().getCraftedProduct();
		if (te.getWireType() == WireType.SUPERCONDUCTOR) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setBoolean("fluid", true);
			is.stackTagCompound.setInteger("lvl", 25);
		}
		return is;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntityWire te = (TileEntityWire)acc.getTileEntity();
		tip.add(String.format("Point Voltage: %dV", te.getWireVoltage()));
		tip.add(String.format("Point Current: %dA", te.getWireCurrent()));
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

}
