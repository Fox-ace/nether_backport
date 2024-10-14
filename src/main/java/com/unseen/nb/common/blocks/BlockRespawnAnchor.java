package com.unseen.nb.common.blocks;

import com.unseen.nb.common.blocks.base.BlockBase;
import com.unseen.nb.common.capabilities.CapabilityRespawnAnchor;
import com.unseen.nb.init.ModSoundHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class BlockRespawnAnchor extends BlockBase
{
    public static final PropertyInteger CHARGES = PropertyInteger.create("charges", 0, 4);

    public BlockRespawnAnchor(String name, Material material, float hardness, float resistance, SoundType soundType)
    {
        super(name, material, hardness, resistance, soundType);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGES, 0));
    }

    @Override
    public int getLightValue(IBlockState state)
    { return state.getValue(CHARGES) > 0 ? 3 + ((state.getValue(CHARGES) - 1) * 4): 0; }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        boolean inNether = worldIn.provider.getDimension() == -1;

        if (!inNether && false)
        {
            worldIn.setBlockToAir(pos);
            worldIn.newExplosion((Entity)null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, true);
            return true;
        }
        else
        {
            int i = state.getValue(CHARGES);

            if (i < 4 && playerIn.getHeldItem(hand).getItem() == Item.getItemFromBlock(Blocks.GLOWSTONE))
            {
                worldIn.setBlockState(pos, state.withProperty(CHARGES, i + 1), 2);
                //Shrink Glowstone Value
                ItemStack stack = playerIn.getHeldItem(hand);
                if (!playerIn.isCreative()) stack.shrink(1);
                worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, ModSoundHandler.RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.7F + 0.3F, false);
                return true;
            }
            else if (i > 0)
            {
                if (playerIn.hasCapability(CapabilityRespawnAnchor.RESPAWN_ANCHOR_CAP, null))
                {
                    CapabilityRespawnAnchor.ICapabilityRespawnAnchor capWindCharge = playerIn.getCapability(CapabilityRespawnAnchor.RESPAWN_ANCHOR_CAP, null);

                    worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, ModSoundHandler.RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.PLAYERS, 1.0F, worldIn.rand.nextFloat() * 0.7F + 0.3F, false);
                    playerIn.sendStatusMessage(new TextComponentTranslation("tile.respawn_anchor.point_set", new Object[0]), true);
                    /* The player's Respawn Point gets scrambled, as it isn't used in the Anchor's logic. */
                    playerIn.setSpawnPoint(null, false);
                    capWindCharge.setUsedAnchor(true);
                    capWindCharge.setAnchorPos(pos);
                    capWindCharge.setPlayerSpawnPos(pos);
                    capWindCharge.setAnchorDim(worldIn.provider.getDimension());
                }
            }
            return true;
        }
    }

    public IBlockState getStateFromMeta(int meta) { return this.getDefaultState().withProperty(CHARGES, meta); }

    public int getMetaFromState(IBlockState state)
    {
        return (Integer) state.getValue(CHARGES);
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CHARGES});
    }

}