package mods.nnsmod;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesNNSEnhance implements IRecipe
{
    public static HashMap<Integer, ArrayList<EnumNNSInfo>> mapInfo = new HashMap<Integer, ArrayList<EnumNNSInfo>>();

    public RecipesNNSEnhance()
    {
        //TODO もっとやり方がある。だがめんどいからこれで
        ArrayList<EnumNNSInfo> listInfo = new ArrayList<EnumNNSInfo>();
        //★Beginning
        listInfo.add(EnumNNSInfo.Iron);
        mapInfo.put(Integer.valueOf(EnumNNSInfo.Beginning.ordinal()), listInfo);
        //★Iron
        listInfo = new ArrayList<EnumNNSInfo>();
        listInfo.add(EnumNNSInfo.Attacker);
        listInfo.add(EnumNNSInfo.Blocker);
        mapInfo.put(Integer.valueOf(EnumNNSInfo.Iron.ordinal()), listInfo);
    }

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world)
    {
        ItemStack itemNNSword = null;
        ItemStack itemRepairItem = null;
        //boolean isMatche = true;

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
                if (itemNNSword != null)
                {
                    return false;
                }
                itemNNSword = itemStackCurrent;
            }
            else
            {
                //▼強化アイテム
                if (itemRepairItem != null)
                {
                    return false;
                }
                itemRepairItem = itemStackCurrent;
            }
        }

        boolean isMatche = false;
        if (itemNNSword != null && itemRepairItem != null)
        {
            int nSwordType = ItemNNS.getSwordType(itemNNSword);
            int nNowExp = ItemNNS.getExp(ItemNNS.EXP_REPAIR, itemNNSword);

            if (nSwordType == EnumNNSInfo.Beginning.ordinal() &&
                itemRepairItem.itemID == Item.enderPearl.itemID)
            {
                //▼強化先強化アイテム かつ 必須経験値に達している
                isMatche = true;
            }
            else
            {
                ArrayList<EnumNNSInfo> list = mapInfo.get(Integer.valueOf(nSwordType));
                for (EnumNNSInfo info : list)
                {
                    if (info.nRepairItemID == itemRepairItem.itemID &&
                        nNowExp >= ItemNNS.arrEnumNNS[nSwordType].getRepairExpMax())
                    {
                        //▼強化先強化アイテム かつ 必須経験値に達している
                        isMatche = true;
                    }
                }
            }
        }

        return isMatche;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
    {
        ItemStack itemNNSword = null;
        //ItemStack itemRepairItem = null;
        int nRepairItemID = 0;

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
            else
            {
                //▼強化アイテム
                nRepairItemID = itemStackCurrent.itemID;
            }
        }

        int nSwordType = ItemNNS.getSwordType(itemNNSword);
        if (nSwordType == EnumNNSInfo.Beginning.ordinal() &&
            nRepairItemID == Item.enderPearl.itemID)
        {
          //▼
            ItemNNS.setSwordType(itemNNSword, EnumNNSInfo.Iron.ordinal());
        }
        else
        {
            ArrayList<EnumNNSInfo> list = mapInfo.get(Integer.valueOf(nSwordType));
            for (EnumNNSInfo info : list)
            {
                if (info.nRepairItemID == nRepairItemID)
                {
                    //▼
                    ItemNNS.delFlag(ItemNNS.NORMAL_FLAG, itemNNSword, ItemNNS.FLAG_BITMASK_NOTICED);
                    ItemNNS.setSwordType(itemNNSword, info.ordinal());
                }
            }
        }

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
