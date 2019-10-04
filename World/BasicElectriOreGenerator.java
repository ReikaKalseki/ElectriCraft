/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.World;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import Reika.DragonAPI.Interfaces.OreGenerator;
import Reika.DragonAPI.Interfaces.Registry.OreEnum;
import Reika.ElectriCraft.Registry.ElectriOptions;
import Reika.ElectriCraft.Registry.ElectriOres;

public class BasicElectriOreGenerator implements OreGenerator {

	@Override
	public void generateOre(OreEnum ore, Random random, World world, int chunkX, int chunkZ) {
		ElectriOres e = (ElectriOres)ore;
		if (random.nextInt(ElectriOptions.DISCRETE.getValue()) == 0) {
			this.generate(e, world, random, chunkX*16, chunkZ*16);
		}
	}

	private void generate(ElectriOres ore, World world, Random random, int chunkX, int chunkZ) {
		//ReikaJavaLibrary.pConsole("Generating "+ore);
		//ReikaJavaLibrary.pConsole(chunkX+", "+chunkZ);
		Block id = ore.getBlock();
		int meta = ore.getBlockMetadata();
		int passes = ore.perChunk*ElectriOptions.DISCRETE.getValue();
		for (int i = 0; i < passes; i++) {
			int posX = chunkX+random.nextInt(16);
			int posZ = chunkZ+random.nextInt(16);
			int posY = ore.minY+random.nextInt(ore.maxY-ore.minY+1);


			if (ore.canGenAt(world, posX, posY, posZ)) {
				if ((new WorldGenMinable(id, meta, ore.veinSize, ore.getReplaceableBlock())).generate(world, random, posX, posY, posZ))
					;//ReikaJavaLibrary.pConsole(ore+" @ "+posX+", "+posY+", "+posZ, ore == ElectriOres.MAGNETITE);
			}
		}
	}

}
