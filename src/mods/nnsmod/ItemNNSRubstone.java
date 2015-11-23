package mods.nnsmod;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemNNSRubstone extends Item
{
    /**
     * ■コンストラクタ
     */
    public ItemNNSRubstone(int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * ■クリエイティブモードでアイテム追加
     */
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }
}
