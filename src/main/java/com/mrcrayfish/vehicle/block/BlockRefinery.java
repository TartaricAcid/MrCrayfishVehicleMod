package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.ItemJerryCan;
import com.mrcrayfish.vehicle.tileentity.TileEntityRefinery;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockRefinery extends BlockRotatedObject
{
    public BlockRefinery()
    {
        super(Material.ANVIL, "refinery");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if(stack.getItem() instanceof ItemJerryCan)
            {
                ItemJerryCan jerryCan = (ItemJerryCan) stack.getItem();

                if(jerryCan.isFull(stack))
                    return false;

                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                {
                    IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if(handler != null)
                    {
                        FluidStack fluidStack = handler.drain(50, true);
                        if(fluidStack != null)
                        {
                            int remaining = jerryCan.fill(stack, fluidStack.amount);
                            if(remaining > 0)
                            {
                                fluidStack.amount = remaining;
                                handler.fill(fluidStack, true);
                            }
                        }
                    }
                }
                return false;
            }
            else if(stack.getItem() == Items.BUCKET)
            {
                if(FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing))
                {
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if(tileEntity instanceof TileEntityRefinery)
                    {
                        ((TileEntityRefinery) tileEntity).syncFueliumAmountToClients();
                    }
                }
                return true;
            }

            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof TileEntityRefinery)
            {
                playerIn.openGui(VehicleMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityRefinery();
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}