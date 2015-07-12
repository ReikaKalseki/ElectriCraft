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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriBlock;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.API.Interfaces.Fillable;
import Reika.RotaryCraft.Entities.EntityDischarge;
import Reika.RotaryCraft.Registry.SoundRegistry;

@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockWire extends ElectriBlock implements IWailaDataProvider {

	private static final int nWires = ReikaJavaLibrary.getEnumLengthWithoutInitializing(WireType.class);

	private static final IIcon[] textures = new IIcon[nWires];
	private static final IIcon[] insulTextures = new IIcon[nWires];

	private static final IIcon[] texturesEnd = new IIcon[nWires];
	private static final IIcon[] insulTexturesEnd = new IIcon[nWires];

	public BlockWire(Material par2Material) {
		super(par2Material);
		this.setStepSound(soundTypeCloth);
		this.setHardness(0.05F);
		this.setResistance(2F);
		//this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
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
	public Item getItemDropped(int id, Random r, int fortune) {
		return null;
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

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList li = new ArrayList();
		TileEntityWire te = (TileEntityWire)world.getTileEntity(x, y, z);
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
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType wire = WireType.wireList[i];
			textures[i] = ico.registerIcon("ElectriCraft:wire/"+wire.getIconTexture());
			insulTextures[i] = ico.registerIcon("ElectriCraft:wire/"+wire.getIconTexture()+"_ins");

			texturesEnd[i] = ico.registerIcon("ElectriCraft:wire/"+wire.getIconTexture()+"_end");
			insulTexturesEnd[i] = ico.registerIcon("ElectriCraft:wire/"+wire.getIconTexture()+"_ins_end");
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return textures[meta];
	}

	public static IIcon getInsulatedEndTexture(WireType type) {
		return insulTexturesEnd[type.ordinal()];
	}

	public static IIcon getEndTexture(WireType type) {
		return texturesEnd[type.ordinal()];
	}

	public static IIcon getInsulatedTexture(WireType type) {
		return insulTextures[type.ordinal()];
	}

	public static IIcon getTexture(WireType type) {
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		TileEntityWire te = (TileEntityWire)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityWire te = (TileEntityWire)world.getTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.25;
		TileEntityWire te = (TileEntityWire)world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		float minx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.WEST) ? 0 : 0.3F;
		float maxx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.EAST) ? 1 : 0.7F;
		float minz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.SOUTH) ? 0 : 0.3F;
		float maxz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.NORTH) ? 1 : 0.7F;
		float miny = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.UP) ? 0 : 0.3F;
		float maxy = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.DOWN) ? 1 : 0.7F;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x+minx, y+miny, z+minz, x+maxx, y+maxy, z+maxz);
		this.setBounds(box, x, y, z);
		return box;
	}

	protected final void setBounds(AxisAlignedBB box, int x, int y, int z) {
		this.setBlockBounds((float)box.minX-x, (float)box.minY-y, (float)box.minZ-z, (float)box.maxX-x, (float)box.maxY-y, (float)box.maxZ-z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (!world.isRemote) {
			if (!(e instanceof EntityPlayer) || (e instanceof EntityPlayer && !((EntityPlayer)e).capabilities.isCreativeMode)) {
				TileEntityWire te = (TileEntityWire)world.getTileEntity(x, y, z);
				WireNetwork net = te.getNetwork();
				if (!te.insulated && net != null) {
					if (net.isLive()) {
						int v = net.getPointVoltage(te);
						EntityDischarge ed = new EntityDischarge(world, x+0.5, y+0.5, z+0.5, v, e.posX, e.posY, e.posZ);
						world.spawnEntityInWorld(ed);
						if (!(e instanceof EntityLivingBase) || !ReikaEntityHelper.isEntityWearingFullSuitOf((EntityLivingBase)e, ArmorMaterial.CHAIN))
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
		TileEntityWire te = (TileEntityWire)world.getTileEntity(x, y, z);
		ItemStack is = te.insulated ? te.getWireType().getCraftedInsulatedProduct() : te.getWireType().getCraftedProduct();
		if (te.getWireType() == WireType.SUPERCONDUCTOR) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setBoolean("fluid", true);
			is.stackTagCompound.setInteger("lvl", 25);
		}
		return is;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntityWire te = (TileEntityWire)acc.getTileEntity();
		if (te instanceof TileEntityWire) {
			te.syncAllData(false);
			tip.add(String.format("Point Voltage: %dV", te.getWireVoltage()));
			tip.add(String.format("Point Current: %dA", te.getWireCurrent()));
		}
		return tip;
	}

	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
