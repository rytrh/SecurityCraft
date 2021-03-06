package net.geforcemods.securitycraft.misc;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Simple wrapper class for LookingGlass camera views
 * that SecurityCraft uses. Provides easy access to the
 * view's coordinates and a formatted string for storage
 * in HashMaps, as well as a few helpful methods.
 *
 * @version 1.1.0
 *
 * @author Geforce
 */
public class CameraView { //TODO: is this still needed? could switch to net.minecraft.util.math.GlobalPos

	public int x = 0;
	public int y = 0;
	public int z = 0;
	public ResourceLocation dimension = World.field_234918_g_.func_240901_a_();

	public CameraView(int x, int y, int z, RegistryKey<World> dim) {
		this.x = x;
		this.y = y;
		this.z = z;

		if(dim != null)
			dimension = dim.func_240901_a_();
	}

	public CameraView(int x, int y, int z, ResourceLocation dim) {
		this.x = x;
		this.y = y;
		this.z = z;

		if(dim != null)
			dimension = dim;
	}

	public CameraView(BlockPos pos, RegistryKey<World> dim) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();

		if(dim != null)
			dimension = dim.func_240901_a_();
	}


	/**
	 * @return The position of this view in BlockPos form.
	 */
	public BlockPos getLocation() {
		return new BlockPos(x, y, z);
	}

	/**
	 * Checks to see if the given coordinates are the same
	 * as this view's coordinates.
	 *
	 * @param coordinates a String[] which contains the x, y, and z coordinates, and the dimension ID of the view
	 * @return true if the x, y, z and dimension match, false otherwise
	 */
	public boolean checkCoordinates(String[] coordinates) {
		int xPos = Integer.parseInt(coordinates[0]);
		int yPos = Integer.parseInt(coordinates[1]);
		int zPos = Integer.parseInt(coordinates[2]);
		ResourceLocation dim = new ResourceLocation(coordinates.length == 4 ? coordinates[3] : "");

		return (x == xPos && y == yPos && z == zPos && dimension.equals(dim));
	}

	/**
	 * @return A formatted string of this view's location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public String toNBTString() {
		return x + " " + y + " " + z + " " + dimension;
	}

}
