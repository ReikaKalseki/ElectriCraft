/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.Auxiliary.Overloadable;
import Reika.ElectriCraft.Auxiliary.WireEmitter;
import Reika.ElectriCraft.Auxiliary.WireReceiver;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.Interfaces.Screwdriverable;
import Reika.RotaryCraft.Auxiliary.Interfaces.PipeConnector;
import Reika.RotaryCraft.Auxiliary.Interfaces.TemperatureTE;
import Reika.RotaryCraft.Base.TileEntity.TileEntityPiping.Flow;
import Reika.RotaryCraft.Registry.MachineRegistry;

public class TileEntityTransformer extends NetworkTileEntity implements WireEmitter, WireReceiver, Screwdriverable, TemperatureTE,
IFluidHandler, PipeConnector, Overloadable {

	private int Vin = 0;
	private int Ain = 0;

	private int Vout = 0;
	private int Aout = 0;

	private int Vold = 0;
	private int Aold = 0;

	private int n1 = 1;
	private int n2 = 1;

	//private double ratio = 1;

	private ForgeDirection facing;

	public static final int MAXTEMP = 1000;
	public static final int MAXCURRENT = 4096;

	private int temperature;
	private StepTimer tempTimer = new StepTimer(20);

	private final HybridTank tank = new HybridTank("transformer", 200);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && network != null) {
			Vin = network.getTerminalVoltage(this);
			Ain = network.getTerminalCurrent(this);
			boolean chg = Vin != Vold || Ain != Aold;
			if (this.getTicksExisted() == 0 || chg) {
				this.calculateOutput();
				network.updateWires();
			}
			Vold = Vin;
			Aold = Ain;
			if (Ain > this.getMaxCurrent() || Aout > this.getMaxCurrent())
				this.overload(Math.max(Ain, Aout));
		}

		tempTimer.update();
		if (tempTimer.checkCap()) {
			this.updateTemperature(world, x, y, z, meta);
		}
	}

	@Override
	public int getMaxCurrent() {
		return MAXCURRENT;
	}

	@Override
	public void overload(int current) {
		worldObj.createExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, 12, true);
	}

	public double getRatio() {
		return (double)n2/n1;//ratio;
	}

	public boolean isStepUp() {
		return n2 > n1;//ratio > 1;
	}

	public int getN1() {
		return n1;
	}

	public int getN2() {
		return n2;
	}

	public void setRatio(int n1, int n2) {
		if (n1 == 0 || n2 == 0)
			return;
		//this.setRatio((double)n2/n1);

		boolean f1 = n1 != this.n1;
		boolean f2 = n2 != this.n2;
		this.n1 = n1;
		this.n2 = n2;
		this.onRatioChanged(f1 || f2);
	}
	/*
	private void setRatio(double r) {
		boolean chg = ratio != r;
		ratio = r;
		this.onRatioChanged(chg);
	}*/

	private void onRatioChanged(boolean full) {
		this.calculateOutput();
		if (full && network != null)
			network.updateWires();
	}

	public String getRatioForDisplay() {/*
		int r1 = 1;
		int r2 = 1;
		double ratio = this.getRatio();
		if (ratio != 0 && ratio != 1) {
			r1 = Math.max(1, (int)(1D/ratio));
			r2 = Math.max(1, (int)(ratio));
		}*/
		return String.format("%d:%d", n1, n2);
	}

	private void calculateOutput() {
		double ratio = this.getRatio();
		if (ratio == 0) {
			Vout = Aout = 0;
		}
		else {
			Vout = (int)(Vin*ratio);
			Aout = (int)(Ain/ratio*this.getEfficiency());
		}
	}

	public double getEfficiency() {
		double r = this.getRatio();
		if (r < 1)
			r = 1D/r;
		return 1/Math.pow(r, 0.1);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.TRANSFORMER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing() || dir == this.getFacing().getOpposite();
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);
		NBT.setInteger("volt", Vin);
		NBT.setInteger("amp", Ain);
		NBT.setInteger("volt2", Vout);
		NBT.setInteger("amp2", Aout);

		//NBT.setDouble("ratio", ratio);

		NBT.setInteger("n1", n1);
		NBT.setInteger("n2", n2);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("temp", temperature);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);
		Vin = NBT.getInteger("volt");
		Ain = NBT.getInteger("amp");
		Vout = NBT.getInteger("volt2");
		Aout = NBT.getInteger("amp2");

		n1 = NBT.getInteger("n1");
		n2 = NBT.getInteger("n2");

		//ratio = NBT.getDouble("ratio");

		this.setFacing(dirs[NBT.getInteger("face")]);

		temperature = NBT.getInteger("temp");
	}

	@Override
	public boolean canReceivePowerFromSide(ForgeDirection dir) {
		return dir == this.getFacing().getOpposite();
	}

	@Override
	public boolean canReceivePower() {
		return true;
	}

	@Override
	public int getGenVoltage() {
		return Vout;
	}

	@Override
	public int getGenCurrent() {
		return Aout;
	}

	@Override
	public boolean canEmitPowerToSide(ForgeDirection dir) {
		return dir == this.getFacing();
	}

	@Override
	public boolean canEmitPower() {
		return true;
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	public final void setFacing(ForgeDirection dir) {
		facing = dir;
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		this.incrementFacing();
		return true;
	}

	protected void incrementFacing() {
		int o = this.getFacing().ordinal();
		if (o == 5)
			this.setFacing(dirs[2]);
		else
			this.setFacing(dirs[o+1]);
		this.rebuildNetwork();
	}

	public AxisAlignedBB getAABB() {
		int dx = Math.abs(this.getFacing().offsetX);
		int dz = Math.abs(this.getFacing().offsetZ);
		double d = 0.2875;
		double d2 = 0.0625;
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord);
		box.minX += dz*d;
		box.maxX -= dz*d;
		box.minZ += dx*d;
		box.maxZ -= dx*d;

		box.minX += dx*d2;
		box.maxX -= dx*d2;
		box.minZ += dz*d2;
		box.maxZ -= dz*d2;
		return box;
	}

	@Override
	public void updateTemperature(World world, int x, int y, int z, int meta) {
		if (Aout > 0 && Vout > 0) {
			double ratio = this.getRatio();
			double r = ratio >= 0 ? ratio : 1/ratio;
			double d = tank.getLevel() >= 50 ? 0.035 : 0.35;
			temperature += d*Math.sqrt(Ain*Vin)+Math.max(0, ReikaMathLibrary.logbase(r+1, 2));
		}
		if (tank.getLevel() >= 50) {
			tank.removeLiquid(50);
			//temperature *= 0.75;
		}
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z);
		if (temperature > Tamb) {
			temperature -= (temperature-Tamb)/5;
		}
		else {
			temperature += (temperature-Tamb)/5;
		}
		if (temperature - Tamb <= 4 && temperature > Tamb)
			temperature--;
		if (temperature > MAXTEMP) {
			temperature = MAXTEMP;
			this.overheat(world, x, y, z);
		}
		if (temperature < Tamb)
			temperature = Tamb;
	}

	@Override
	public void addTemperature(int temp) {
		temperature += temp;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public int getThermalDamage() {
		return temperature > 600 ? 8 : temperature > 200 ? 4 : 0;
	}

	@Override
	public void overheat(World world, int x, int y, int z) {
		worldObj.setBlockToAir(x, y, z);
		worldObj.newExplosion(null, x+0.5, y+0.5, z+0.5, 3, true, true);
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry m, ForgeDirection side) {
		return this.canConnectToPipe(m) && this.canIntake(side);
	}

	private boolean canIntake(ForgeDirection side) {
		return side != this.getFacing() && side != this.getFacing().getOpposite() && side.offsetY == 0;
	}

	@Override
	public Flow getFlowForSide(ForgeDirection side) {
		return this.canIntake(side) ? Flow.INPUT : Flow.NONE;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.canIntake(from) && fluid.equals(FluidRegistry.getFluid("rc liquid nitrogen"));
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[0];
	}

	@Override
	public boolean canBeCooledWithFins() {
		return true;
	}

	public void setTemperature(int temp) {
		temperature = temp;
	}

	@Override
	public boolean allowExternalHeating() {
		return false;
	}

	@Override
	public int getMaxTemperature() {
		return MAXTEMP;
	}

}
