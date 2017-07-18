package Reika.ElectriCraft.Blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityWirelessCharger;
import Reika.ElectriCraft.TileEntities.TileEntityWirelessCharger.ChargerTiers;


public class BlockChargePad extends BlockContainer {

	private IIcon frontIcon;
	private IIcon backIcon;
	private IIcon[] sideIcons;

	public static ChargerTiers itemRender;

	public BlockChargePad(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(10);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWirelessCharger();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s == 0 ? backIcon : s == 1 ? frontIcon : sideIcons[itemRender != null ? itemRender.ordinal() : 0];
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		int meta = iba.getBlockMetadata(x, y, z);
		TileEntityWirelessCharger te = (TileEntityWirelessCharger)iba.getTileEntity(x, y, z);
		int back = te.getFacing().getOpposite().ordinal();
		int front = te.getFacing().ordinal();
		return s == back ? backIcon : s == front ? frontIcon : sideIcons[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		frontIcon = ico.registerIcon("electricraft:wireless/front");
		backIcon = ico.registerIcon("electricraft:wireless/back");
		sideIcons = new IIcon[TileEntityWirelessCharger.ChargerTiers.tierList.length];
		for (int i = 0; i < sideIcons.length; i++) {
			sideIcons[i] = ico.registerIcon("electricraft:wireless/side_"+i);
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs cr, List li) {
		for (int i = 0; i < TileEntityWirelessCharger.ChargerTiers.tierList.length; i++) {
			li.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harv) {
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
		ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", meta);
		li.add(is);
		return li;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", meta);
		return is;
	}

}
