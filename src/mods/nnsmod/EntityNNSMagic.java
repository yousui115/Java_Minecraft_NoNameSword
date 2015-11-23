package mods.nnsmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityNNSMagic extends Entity
{
    /**
     * ★撃った人
     */
    protected EntityLiving thrower;

    /**
     * ★多段Hit防止用List
     */
    protected List<Entity> alreadyHitEntity = new ArrayList<Entity>();


    /**
     * ■コンストラクタ
     * @param par1World
     */
    public EntityNNSMagic(World par1World)
    {
        super(par1World);
    }

    public EntityNNSMagic(World par1World, EntityLiving entityLiving)
    {
        this(par1World);

        //■Y軸のオフセット設定
        yOffset = entityLiving.getEyeHeight()/2.0F;

        //■撃った人
        thrower = entityLiving;

        //■撃った人と、撃った人が（に）乗ってるEntityも除外
        alreadyHitEntity.clear();
        alreadyHitEntity.add(thrower);
        alreadyHitEntity.add(thrower.ridingEntity);
        alreadyHitEntity.add(thrower.riddenByEntity);

        //■生存タイマーリセット
        ticksExisted = 0;

        //■サイズ変更
        setSize(1.0F, 2.0F);

        //■初期位置・初期角度等の設定
        setLocationAndAngles(thrower.posX,
                             thrower.posY + (double)thrower.getEyeHeight()/2D,
                             thrower.posZ,
                             thrower.rotationYaw,
                             thrower.rotationPitch);

        //■初期ベクトル設定
        setNNSMagicVector(0.2F);

        //■プレイヤー位置より一歩進んだ所に出現する
        setPosition(posX + motionX, posY + motionY, posZ + motionZ);
    }

    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {}

    /**
     * ■初期ベクトルとかを決めてる。
     * @param f
     */
    public void setNNSMagicVector(float f)
    {
        //TODO
        //■移動速度設定
        float fYVecOfst = 0.5F;

        //■角度 -> ラジアン 変換
        float fYawDtoR = (  rotationYaw / 180F) * (float)Math.PI;
        float fPitDtoR = (rotationPitch / 180F) * (float)Math.PI;

        //■単位ベクトル
        motionX = -MathHelper.sin(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst;
        motionY = -MathHelper.sin(fPitDtoR) * fYVecOfst;
        motionZ =  MathHelper.cos(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst;
/*
        float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

        motionX /= f2;
        motionY /= f2;
        motionZ /= f2;

        motionX *= f;
        motionY *= f;
        motionZ *= f;
*/
        //motionX = d;
        //motionY = d1;
        //motionZ = d2;

        float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        prevRotationYaw = rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / Math.PI);
        prevRotationPitch = rotationPitch = (float)((Math.atan2(motionY, f3) * 180D) / Math.PI);
    }

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        //super.onUpdate();

        if(!worldObj.isRemote)
        {
            //■Entityとの当り判定
            Entity entity = null;

            //■周辺のEntityをかき集める。
            double dAmbit = 1.0D;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getAABBPool().getAABB(posX - dAmbit, posY - dAmbit, posZ - dAmbit, posX + dAmbit, posY + dAmbit, posZ + dAmbit));

            //■当り判定
            for(int l = 0; l < list.size(); l++)
            {
                Entity entity1 = (Entity)list.get(l);

                //■多段Hitしない。
                if (alreadyHitEntity.contains(entity1) == true)
                {
                    continue;
                }

                //■弓矢を消し去る
                if (entity1 instanceof EntityArrow)
                {
                    entity1.setDead();
                    continue;
                }

                //■当り判定を行わなくて良いEntity
                if(entity1.canBeCollidedWith() == false ||
                   !(entity1 instanceof EntityLiving) && !(entity1 instanceof EntityDragonPart))
                {
                    continue;
                }

                alreadyHitEntity.add(entity1);

                //■相手にダメージ
                if (thrower instanceof EntityPlayer)
                {
                     DamageSource d = DamageSource.causePlayerDamage((EntityPlayer)thrower);
                    entity1.attackEntityFrom(d, 1);
                }
            }

            //■ブロック
            int nPosX = MathHelper.floor_double(posX);
            int nPosY = MathHelper.floor_double(posY);
            int nPosZ = MathHelper.floor_double(posZ);

            for (int idx = nPosX - 1; idx <= nPosX + 1; idx++) {
                for (int idy = nPosY - 1; idy <= nPosY + 1; idy++) {
                    for (int idz = nPosZ - 1; idz <= nPosZ + 1; idz++) {
                        //▼
                        int nBlockID = worldObj.getBlockId(idx, idy, idz);

                        //■
                        if (nBlockID == Block.web.blockID || nBlockID == Block.leaves.blockID)
                        {
                            Block block = Block.blocksList[nBlockID];
                            worldObj.destroyBlock(idx, idy, idz, true);
                            //int nMetaData = worldObj.getBlockMetadata(idx, idy, idz);
                            //block.onBlockDestroyedByPlayer(worldObj, idx, idy, idz, nMetaData);
                        }
                    }
                }
            }

            //■消滅処理
            int nBlockID = worldObj.getBlockId(nPosX, nPosY, nPosZ);
            if (nBlockID > 0 &&
                Block.blocksList[nBlockID].getCollisionBoundingBoxFromPool(worldObj, nPosX, nPosY, nPosZ) != null)
            {
                this.setDead();
            }

        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        setPosition(posX, posY, posZ);

        //■死亡チェック
        if(ticksExisted >= 10) {
            alreadyHitEntity.clear();
            alreadyHitEntity = null;
            setDead();
        }
    }

    /**
     * ■Random
     * @return
     */
    public Random getRand()
    {
        return this.rand;
    }

    /**
     * ■Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     * Liquid = 流体
     */
    @Override
    public boolean isOffsetPositionInLiquid(double par1, double par3, double par5)
    {
        //AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
        //List list = this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb);
        //return !list.isEmpty() ? false : !this.worldObj.isAnyLiquid(axisalignedbb);
        return false;
    }

    /**
     * ■Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    @Override
    public void moveEntity(double par1, double par3, double par5) {}

    /**
     * ■Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    @Override
    protected void dealFireDamage(int par1) {}

    /**
     * ■Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    @Override
    public boolean handleWaterMovement()
    {
        return false;
    }

    /**
     * ■Checks if the current block the entity is within of the specified material type
     */
    @Override
    public boolean isInsideOfMaterial(Material par1Material)
    {
        return false;
    }

    /**
     * ■Whether or not the current entity is in lava
     */
    @Override
    public boolean handleLavaMovement()
    {
        return false;
    }

    /**
     * ■環境光による暗さの描画（？）
     *    EntityXPOrbのぱくり
     */
    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender(float par1)
    {
        float f1 = 0.5F;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        int i = super.getBrightnessForRender(par1);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f1 * 15.0F * 16.0F);

        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }

    /**
     * ■Gets how bright this entity is.
     *    EntityPortalFXのぱくり
     */
    @Override
    public float getBrightness(float par1)
    {
        float f1 = super.getBrightness(par1);
        float f2 = 0.9F;
        f2 = f2 * f2 * f2 * f2;
        return f1 * (1.0F - f2) + f2;
        //return super.getBrightness(par1);
    }

    /**
     * ■NBTの読込
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    /**
     * ■NBTの書出
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    /**
     * ■影のサイズ
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * ■Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    @Override
    public void mountEntity(Entity par1Entity) {}
    @Override
    public void unmountEntity(Entity par1Entity) {}

    /**
     * ■Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {}

    /**
     * ■Called by portal blocks when an entity is within it.
     */
    @Override
    public void setInPortal() {}

    /**
     * ■Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    @Override
    public boolean isBurning()
    {
        return false;
    }

    /**
     * ■ブロック内からの追い出し
     */
    @Override
    protected boolean pushOutOfBlocks(double par1, double par3, double par5)
    {
        return false;
    }

    /**
     * ■Sets the Entity inside a web block.
     */
    @Override
    public void setInWeb() {}

}
