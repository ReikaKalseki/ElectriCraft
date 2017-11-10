package Reika.ElectriCraft.API;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.RotaryCraft.API.Power.ShaftMachine;

public interface WrappableWireSource extends ShaftMachine {

	public boolean canConnectToSide(ForgeDirection dir);

	public boolean isFunctional();

	public boolean hasPowerStatusChangedSinceLastTick();

}
