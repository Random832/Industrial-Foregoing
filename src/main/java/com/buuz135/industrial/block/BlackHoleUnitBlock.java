/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleUnitTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlackHoleUnitBlock extends CustomOrientedBlock<BlackHoleUnitTile> {

    public BlackHoleUnitBlock() {
        super("black_hole_unit", BlackHoleUnitTile.class, Material.ROCK, 0, 0);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.getTileEntity(pos) instanceof BlackHoleUnitTile) {
            BlackHoleUnitTile tile = (BlackHoleUnitTile) world.getTileEntity(pos);
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
            if (tile.getAmount() > 0) {
                if (!stack.hasTag()) stack.setTag(new NBTTagCompound());
                stack.getTag().setInt(BlackHoleUnitTile.NBT_AMOUNT, tile.getAmount());
                stack.getTag().setString(BlackHoleUnitTile.NBT_ITEMSTACK, tile.getItemStack().getItem().getRegistryName().toString());
                stack.getTag().setInt(BlackHoleUnitTile.NBT_META, tile.getItemStack().getMetadata());
                if (tile.getItemStack().hasTag())
                    stack.getTag().setTag(BlackHoleUnitTile.NBT_ITEM_NBT, tile.getItemStack().getTag());
            }
            float f = 0.7F;
            float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
            entityitem.setDefaultPickupDelay();
            if (stack.hasTag()) {
                entityitem.getItem().setTag(stack.getTag().copy());
            }
            world.spawnEntity(entityitem);
        }
        world.removeTileEntity(pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (stack.hasTag() && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BlackHoleUnitTile && Item.getByNameOrId(stack.getTag().getString(BlackHoleUnitTile.NBT_ITEMSTACK)) != null) {
            BlackHoleUnitTile tile = (BlackHoleUnitTile) world.getTileEntity(pos);
            if (stack.getTag().hasKey(BlackHoleUnitTile.NBT_ITEMSTACK) && stack.getTag().hasKey(BlackHoleUnitTile.NBT_META)) {
                ItemStack item = new ItemStack(Item.getByNameOrId(stack.getTag().getString(BlackHoleUnitTile.NBT_ITEMSTACK)), 1, stack.getTag().getInt(BlackHoleUnitTile.NBT_META));
                if (stack.getTag().hasKey(BlackHoleUnitTile.NBT_ITEM_NBT))
                    item.setTag(stack.getTag().getCompound(BlackHoleUnitTile.NBT_ITEM_NBT));
                tile.setStack(item);
            }
            if (stack.getTag().hasKey(BlackHoleUnitTile.NBT_AMOUNT))
                tile.setAmount(stack.getTag().getInt(BlackHoleUnitTile.NBT_AMOUNT));
        }
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = super.getTooltip(stack);
        if (stack.hasTag() && Item.getByNameOrId(stack.getTag().getString(BlackHoleUnitTile.NBT_ITEMSTACK)) != null) {
            if (stack.getTag().hasKey(BlackHoleUnitTile.NBT_ITEMSTACK) && stack.getTag().hasKey(BlackHoleUnitTile.NBT_META)) {
                tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.item").getUnformattedText() + " " + new TextComponentTranslation(new ItemStack(Item.getByNameOrId(stack.getTag().getString(BlackHoleUnitTile.NBT_ITEMSTACK)), 1, stack.getTag().getInt(BlackHoleUnitTile.NBT_META)).getTranslationKey() + ".name").getUnformattedText());
            }
            if (stack.getTag().hasKey(BlackHoleUnitTile.NBT_AMOUNT))
                tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.amount").getUnformattedText() + " " + stack.getTag().getInt(BlackHoleUnitTile.NBT_AMOUNT));
        }
        return tooltip;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "eae", "cmc",
                'p', ItemRegistry.plastic,
                'e', Items.ENDER_EYE,
                'a', Items.ENDER_PEARL,
                'c', "chestWood",
                'm', "MACHINE_CASING");
    }

    public ItemStack getItemStack(ItemStack blackHole) {
        NBTTagCompound compound = blackHole.getTag();
        ItemStack stack = ItemStack.EMPTY;
        if (compound == null || !compound.hasKey(BlackHoleUnitTile.NBT_ITEMSTACK)) return stack;
        Item item = Item.getByNameOrId(compound.getString(BlackHoleUnitTile.NBT_ITEMSTACK));
        if (item != null) {
            stack = new ItemStack(item, 1, compound.hasKey(BlackHoleUnitTile.NBT_META) ? compound.getInt(BlackHoleUnitTile.NBT_META) : 0);
            if (compound.hasKey(BlackHoleUnitTile.NBT_ITEM_NBT))
                stack.setTag(compound.getCompound(BlackHoleUnitTile.NBT_ITEM_NBT));
        }
        return stack;
    }

    public int getAmount(ItemStack blackHole) {
        NBTTagCompound compound = blackHole.getTag();
        int amount = 0;
        if (compound != null && compound.hasKey(BlackHoleUnitTile.NBT_AMOUNT)) {
            amount = compound.getInt(BlackHoleUnitTile.NBT_AMOUNT);
        }
        return amount;
    }

    public void setAmount(ItemStack blackHole, int amount) {
        NBTTagCompound compound = blackHole.getTag();
        if (compound != null) {
            compound.setInt(BlackHoleUnitTile.NBT_AMOUNT, amount);
        }
    }

    public void setItemStack(ItemStack hole, ItemStack item) {
        if (!hole.hasTag()) hole.setTag(new NBTTagCompound());
        hole.getTag().setString(BlackHoleUnitTile.NBT_ITEMSTACK, item.getItem().getRegistryName().toString());
        hole.getTag().setInt(BlackHoleUnitTile.NBT_META, item.getMetadata());
        if (item.hasTag())
            hole.getTag().setTag(BlackHoleUnitTile.NBT_ITEM_NBT, item.getTag());
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

}
