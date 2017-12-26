package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item {

	public ItemKeyPanel(){
		super();
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			IPasswordConvertible.BLOCKS.forEach((pc) -> {
				if(BlockUtils.getBlock(worldIn, pos) == ((IPasswordConvertible)pc).getOriginalBlock())
				{
					if(((IPasswordConvertible)pc).convert(playerIn, worldIn, pos) && !playerIn.capabilities.isCreativeMode)
						stack.stackSize--;
				}
			});
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}
}
