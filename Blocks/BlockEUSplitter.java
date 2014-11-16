package Reika.ElectriCraft.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityEUSplitter;

public class BlockEUSplitter extends Block {

	private IIcon outIcon;
	private IIcon inIcon;

	public BlockEUSplitter(Material m) {
		super(m);
		this.setHardness(2F);
		this.setResistance(10F);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
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
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("electricraft:eusplit");
		inIcon = ico.registerIcon("electricraft:eusplit_in");
		outIcon = ico.registerIcon("electricraft:eusplit_out");
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(s) {
		case 4: //side when as item
			return inIcon;
		case 1:
			return outIcon;
		default:
			return blockIcon;
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		TileEntityEUSplitter te = (TileEntityEUSplitter)iba.getTileEntity(x, y, z);
		boolean in = te.getFacing().ordinal() == s;
		boolean out = te.emitsTo(s);
		return in ? inIcon : out ? outIcon : blockIcon;
	}

}
