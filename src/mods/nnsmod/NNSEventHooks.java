package mods.nnsmod;

//イベント指定のためのアノテーション
import java.util.Iterator;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
//各イベントクラス

public class NNSEventHooks
{
    /**
     * ■プレイヤーがアイテムを放り投げた。（Qキー や インベントリ外)
     * @param event
     */
    @ForgeSubscribe
    public void onPlayerTossEvent(ItemTossEvent event)
    {
        //TODO EntityItemを自作して、消失しないようにする
        EntityPlayer player = event.player;
        EntityItem     item = event.entityItem;
        World         world = event.player.worldObj;
        ItemStack itemStack = item.getEntityItem();

        if (itemStack != null &&
            NNSMod.item_NNS.itemID == itemStack.itemID &&
            !world.isRemote)
        {
            ItemNNS.delFlag(ItemNNS.NORMAL_FLAG, itemStack, ItemNNS.FLAG_BITMASK_EFFECT);
        }
    }

    /**
     * ■EntityItem(ドロップ状態のアイテム)をプレイヤーが取得したときのイベント
     * @param event
     */
/*    @ForgeSubscribe
    public void onGetEntityItem(EntityItemPickupEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        EntityItem     item = event.item;
        World         world = event.entityPlayer.worldObj;
        ItemStack itemStack = item.getEntityItem();

        if (NNSMod.item_NNS.itemID == itemStack.itemID &&
            !world.isRemote)
        {
            String strName = ItemNNS.getMasterName(itemStack);
            if (strName != null && player.getEntityName().contentEquals(strName))
            {
                ItemNNS.setFlag(ItemNNS.NORMAL_FLAG, itemStack, ItemNNS.FLAG_BITMASK_EFFECT);
            }
        }
    }
*/
    /**
     * ■Blocker用
     * @param event
     */
    @ForgeSubscribe
    public void onLivingAttack(LivingAttackEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)) { return; }

        EntityPlayer player = (EntityPlayer)event.entityLiving;
        DamageSource source = event.source;
        int nDamage         = event.ammount;
        ItemStack currentItem = player.getCurrentEquippedItem();

        if (currentItem != null && currentItem.getItem() instanceof ItemNNS &&
            ItemNNS.getSwordType(currentItem) == EnumNNSInfo.Blocker.ordinal() &&
            ItemNNS.isMasterName(currentItem, player.getEntityName()) &&
            player.isUsingItem() &&
            !source.isUnblockable())
        {
            //▼「NoNameSword(Blocker:マスター)持ち」 かつ 「ブロッキング中」 かつ 「ブロック可能な攻撃」
            // ■スパアマ(下のonLivingHurt()でダメージ処理)
            player.hurtResistantTime = player.maxHurtResistantTime;

            try
            {
                ModLoader.setPrivateValue(EntityLiving.class, player, 81, 0);
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }

        }

    }

    /**
     * ■
     * @param event
     */
    @ForgeSubscribe
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)) { return; }

        EntityPlayer player = (EntityPlayer)event.entityLiving;
        DamageSource source = event.source;
        int nDamage         = event.ammount;
        ItemStack currentItem = player.getCurrentEquippedItem();

        if (currentItem != null && currentItem.getItem() instanceof ItemNNS &&
            ItemNNS.getSwordType(currentItem) == EnumNNSInfo.Blocker.ordinal() &&
            ItemNNS.isMasterName(currentItem, player.getEntityName()) &&
            player.isUsingItem() &&
            !source.isUnblockable())
        {
            //▼「NoNameSword(Blocker:マスター)持ち」 かつ 「ブロッキング中」 かつ 「ブロック可能な攻撃」
            // ■ダメージ半減
            event.ammount = nDamage >> 1;
        }
    }


    @ForgeSubscribe
    public void onDethDropsItem(PlayerDropsEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP) ||
            !NNSMod.canEnderChestRespawn)
        {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
        World world = event.entityPlayer.worldObj;

        if (!world.isRemote)
        {
            EntityItem targetItem = null;
            ItemStack target = null;

            for (Iterator<EntityItem> itr = event.drops.iterator(); itr.hasNext(); )
            {
                EntityItem entityItem = itr.next();
                ItemStack itemStack = entityItem.getEntityItem();
                if (NNSMod.item_NNS.itemID == itemStack.itemID &&
                    ItemNNS.getMasterName(itemStack) != null &&
                    ItemNNS.getMasterName(itemStack).contentEquals(player.getEntityName()))
                {
                    //▼「無銘剣」である かつ マスターが保持している
                    if (target == null)
                    {
                        target = itemStack.copy();
                        targetItem = entityItem;
                    }
                    else
                    {
                        //target = ItemNNS.getLevel(target) >= ItemNNS.getLevel(itemStack) ? target : itemStack.copy();

                        int nTageLv = ItemNNS.getLevel(target);
                        int nItrLv  = ItemNNS.getLevel(itemStack);
                        if (nTageLv < nItrLv)
                        {
                            target = itemStack.copy();
                            targetItem = entityItem;
                        }
                    }
                }
            }

            if (target != null)
            {
                InventoryBasic end = player.getInventoryEnderChest();
                try
                {
                    ItemStack[] items = (ItemStack[])ModLoader.getPrivateValue(InventoryBasic.class, end, 2);
                    for (int idx = 0; idx < items.length; idx++)
                    {
                        if(items[idx] == null)
                        {
                            target.setItemDamage(0);
                            //ItemNNS.setSwordDamage(target);
                            items[idx] = target;
                            event.drops.remove(targetItem);
                            break;
                        }
                    }
                }
                catch(Exception e)
                {
                    System.out.println("[NNS_ERR] NNSEventHooks.onDethDropsItem");
                }
            }

        }
    }

    //■SEのロード
/*    @ForgeSubscribe
    //@SideOnly(Side.CLIENT)
    public void onSoundLoadEvent(SoundLoadEvent event)
    {
        File file = new File(FMLClientHandler.instance().getClient().mcDataDir, "mods/nnsmod/sound/shinzo03.ogg");
        event.manager.addSound("shinzo03", file);
    }
*/
}
