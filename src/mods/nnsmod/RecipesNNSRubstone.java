package mods.nnsmod;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesNNSRubstone implements IRecipe
{

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world)
    {
        ItemStack itemNNSword = null;
        ItemStack itemRubStone = null;

        for (int idxInv = 0; idxInv < inventorycrafting.getSizeInventory(); ++idxInv)
        {
            ItemStack itemStackCurrent = inventorycrafting.getStackInSlot(idxInv);

            if (itemStackCurrent == null)
            {
                continue;
            }

            if (itemStackCurrent.getItem() instanceof ItemNNS)
            {
                //▼NNSword
                if (itemNNSword != null || itemStackCurrent.getItemDamage() == 0)
                {
                    return false;
                }

                if (ItemNNS.getSwordType(itemStackCurrent) == EnumNNSInfo.Beginning.ordinal())
                {
                    return false;
                }
                itemNNSword = itemStackCurrent;

            }
            else if (itemStackCurrent.getItem() instanceof ItemNNSRubstone)
            {
                //▼強化アイテム
                if (itemRubStone != null)
                {
                    return false;
                }
                itemRubStone = itemStackCurrent;
            }
            else
            {
                return false;
            }
        }

        return itemNNSword != null && itemRubStone != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
    {
        ItemStack itemNNSword = null;

        for (int idxInv = 0; idxInv < inventorycrafting.getSizeInventory(); ++idxInv)
        {
            ItemStack itemStackCurrent = inventorycrafting.getStackInSlot(idxInv);

            if (itemStackCurrent == null)
            {
                continue;
            }

            if (itemStackCurrent.getItem() instanceof ItemNNS)
            {
                //▼NNSword
                itemNNSword = itemStackCurrent.copy();
            }
        }

        int nRepairDamage = Math.min(itemNNSword.getItemDamageForDisplay(), itemNNSword.getMaxDamage() / 4);
        itemNNSword.setItemDamage(itemNNSword.getItemDamageForDisplay() - nRepairDamage);
        return itemNNSword;
    }

    @Override
    public int getRecipeSize()
    {
        // ?
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

}
