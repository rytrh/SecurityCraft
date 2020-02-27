package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionFloat;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntitySecurityCamera extends CustomizableSCTE {

	private final float CAMERA_SPEED = 0.0180F;
	public float cameraRotation = 0.0F;
	public boolean addToRotation = true;
	public boolean down = false;
	public float lastPitch = Float.MAX_VALUE;
	public float lastYaw = Float.MAX_VALUE;
	private OptionFloat rotationSpeedOption = new OptionFloat("rotationSpeed", CAMERA_SPEED, 0.0100F, 0.0250F, 0.001F);
	private OptionBoolean shouldRotateOption = new OptionBoolean("shouldRotate", true);
	private OptionDouble customRotationOption = new OptionDouble(this, "customRotation", (double)cameraRotation, 1.55D, -1.55D, (double)rotationSpeedOption.asFloat(), true);

	@Override
	public void update(){
		super.update();

		if(!shouldRotateOption.asBoolean())
		{
			cameraRotation = (float)customRotationOption.asDouble();
			return;
		}

		if(addToRotation && cameraRotation <= 1.55F)
			cameraRotation += rotationSpeedOption.asFloat();
		else
			addToRotation = false;

		if(!addToRotation && cameraRotation >= -1.55F)
			cameraRotation -= rotationSpeedOption.asFloat();
		else
			addToRotation = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setFloat("LastPitch", lastPitch);
		tag.setFloat("LastYaw", lastYaw);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		lastPitch = tag.getFloat("LastPitch");
		lastYaw = tag.getFloat("LastYaw");
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, blockType, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module)
	{
		world.notifyNeighborsOfStateChange(pos, blockType, false);
	}

	@Override
	public EnumCustomModules[] acceptedModules(){
		return new EnumCustomModules[] { EnumCustomModules.REDSTONE, EnumCustomModules.SMART };
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ rotationSpeedOption, shouldRotateOption, customRotationOption };
	}

	@Override
	public void onLoad()
	{
		super.onLoad();

		if(world != null && world.getBlockState(pos).getBlock() instanceof BlockSecurityCamera)
			down = world.getBlockState(pos).getValue(BlockSecurityCamera.FACING) == EnumFacing.DOWN;
	}
}