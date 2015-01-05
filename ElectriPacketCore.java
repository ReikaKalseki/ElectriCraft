/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Interfaces.IPacketHandler;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Registry.ElectriPackets;
import Reika.ElectriCraft.TileEntities.TileEntityRFCable;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;


public class ElectriPacketCore implements IPacketHandler {

	public void handleData(PacketObj packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = packet.getDataIn();
		int control = Integer.MIN_VALUE;
		int len;
		ElectriPackets pack = null;
		int[] data = new int[0];
		long longdata = 0;
		float floatdata = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		boolean readinglong = false;
		String stringdata = null;
		UUID id = null;
		//System.out.print(packet.length);
		try {
			//ReikaJavaLibrary.pConsole(inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt());
			PacketTypes packetType = packet.getType();
			switch(packetType) {
			case SOUND:
				return;
			case STRING:
				stringdata = packet.readString();
				control = inputStream.readInt();
				//pack = ReactorPackets.getEnum(control);
				break;
			case DATA:
				control = inputStream.readInt();
				pack = ElectriPackets.getEnum(control);
				len = pack.numInts;
				data = new int[len];
				readinglong = false;//pack.isLongPacket();
				//if (!readinglong) {
				for (int i = 0; i < len; i++)
					data[i] = inputStream.readInt();
				//}
				//else
				//	longdata = inputStream.readLong();
				break;
			case UPDATE:
				control = inputStream.readInt();
				//pack = ReactorPackets.getEnum(control);
				break;
			case FLOAT:
				control = inputStream.readInt();
				//pack = ReactorPackets.getEnum(control);
				floatdata = inputStream.readFloat();
				break;
			case SYNC:
				String name = packet.readString();
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				int value = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
				return;
			case TANK:
				String tank = packet.readString();
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				int level = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
				return;
			case RAW:
				control = inputStream.readInt();
				len = 1;
				data = new int[len];
				for (int i = 0; i < len; i++)
					data[i] = inputStream.readInt();
				break;
			case NBT:
				break;
			case STRINGINT:
				stringdata = packet.readString();
				control = inputStream.readInt();
				data = new int[1];
				for (int i = 0; i < data.length; i++)
					data[i] = inputStream.readInt();
				break;
			case UUID:
				control = inputStream.readInt();
				pack = ElectriPackets.getEnum(control);
				long l1 = inputStream.readLong(); //most
				long l2 = inputStream.readLong(); //least
				id = new UUID(l1, l2);
				break;
			}
			if (packetType.hasCoordinates()) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		try {
			switch (pack) {
			case RFCABLE:
				((TileEntityRFCable)te).setRFLimit(data[0]);
				break;
			case TRANSFORMER:
				TileEntityTransformer tf = (TileEntityTransformer)te;
				tf.setRatio(data[0], data[1]);
				break;
			}
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Machine/item was deleted before its packet could be received!");
			ReikaChatHelper.writeString("Machine/item was deleted before its packet could be received!");
			e.printStackTrace();
		}
	}
}
