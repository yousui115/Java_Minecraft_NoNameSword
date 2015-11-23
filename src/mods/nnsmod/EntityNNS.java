package mods.nnsmod;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityNNS extends Entity
{

    public EntityNNS(World par1World)
    {
        super(par1World);
    }

    @Override
    protected void entityInit()
    {
        //■サイズ設定
        setSize(1.0F, 1.0F);

        //■Y軸上の位置調整値
        yOffset = 0.0F;
    }

    @Override
    public void onUpdate()
    {
        //■存在できるかチェック
        if (canStay() == false) {
            setNNSDead(true);
        }
    }

    //■当り判定が仕事するか否か
    @Override
    public boolean canBeCollidedWith()
    {
        //仕事するのでtrue
        return true;
    }

    //■ダメージソースを貰ったらどうするか
    @Override
    public boolean attackEntityFrom(DamageSource damagesource, int i)
    {
        if(!worldObj.isRemote)
        {
            setBeenAttacked();
            setNNSDead(true);
        }
        return true;
    }

    //■その場に留まる事が出来るかどうか
    public boolean canStay()
    {
        int nX = MathHelper.floor_double(posX);
        int nY = MathHelper.floor_double(posY);
        int nZ = MathHelper.floor_double(posZ);

        //▼EntityBonfireが既に置いてあるとfalse
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);
        for(int l = 0; l < list.size(); l++)
        {
            Entity entity1 = (Entity)list.get(l);
            if (entity1 instanceof EntityNNS) {
                return false;
            }
        }

        //▼設置場所にブロックがあったらfalse
        int nBlockId = worldObj.getBlockId(nX, nY, nZ);
        if (nBlockId != 0) { return false; }

        //▼設置場所の下にブロックがないとfalse
        nBlockId = worldObj.getBlockId(nX, nY - 1, nZ);
        if (nBlockId == 0) { return false; }

        return true;
    }

    //■プレイヤーが右クリックすると呼ばれる
    @Override
    public boolean interact(EntityPlayer player)
    {
        ItemStack currentItem = player.getCurrentEquippedItem();
        if (currentItem != null) { return false; }
        if (player.experienceLevel < 11)
        {
            if (!this.worldObj.isRemote) {
                player.addChatMessage("その資格はない");
            }
            return true;
        }

        player.swingItem();

        if (!this.worldObj.isRemote)
        {
            ItemStack itemStack = new ItemStack(NNSMod.item_NNS);
            //  ★Type = Beginning
            ItemNNS.setSwordType(itemStack, EnumNNSInfo.Beginning.ordinal());
            //  ★耐久値はギリギリ
            ItemNNS.addItemDamage(itemStack, itemStack.getMaxDamage() - 2, 0, null);
            ItemNNS.setExp(ItemNNS.EXP_REPAIR, itemStack, 1);
            player.setCurrentItemOrArmor(0, itemStack);

            setNNSDead(false);
        }

        return true;
    }

    protected void setNNSDead(boolean isBreak)
    {
        this.setDead();

        if (isBreak)
        {
            worldObj.playSoundEffect(posX, posY, posZ, "random.break", 1.0F, 1.0F);
        }

    }

    //■おそらく、ピストン対策
    @Override
    public void moveEntity(double d, double d1, double d2)
    {
        if(!worldObj.isRemote && d * d + d1 * d1 + d2 * d2 > 0.0D)
        {
            //setEntityDead();
            //worldObj.entityJoinedWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Item.coal, 1, 1)));
            setNNSDead(true);
        }
    }

    //■おそらく、ピストン対策
    @Override
    public void addVelocity(double d, double d1, double d2)
    {
        if(!worldObj.isRemote && d * d + d1 * d1 + d2 * d2 > 0.0D)
        {
            //setEntityDead();
            //worldObj.entityJoinedWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Item.coal, 1, 1)));
            setNNSDead(true);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

}
