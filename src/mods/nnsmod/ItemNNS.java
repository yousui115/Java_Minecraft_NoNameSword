package mods.nnsmod;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNNS extends ItemSword
{
    /**
     * ★NBTTag名
     */
    public static String MASTER_NAME  = "MasterName";
    public static String SWORD_NAME   = "SwordName";
    public static String SWORD_TYPE   = "SwordType";
    public static String SWORD_DAMAGE = "SwordDamage";
    public static String STACK_DAMAGE = "StackDamage";
    public static String EXP_REPAIR   = "RepairExp";
    public static String EXP_HIT      = "HitExp";
    public static String NORMAL_FLAG  = "NormalFlag";
    public static String TRIAL_FLAG   = "TrialFlag";
    public static int FLAG_BITMASK_NG     = 0x1;
    public static int FLAG_BITMASK_REPAIR = 0x2;
    public static int FLAG_BITMASK_EFFECT = 0x4;
    public static int FLAG_BITMASK_LVMAX  = 0x8;
    public static int FLAG_BITMASK_NOTICED = 0x10;

    /**
     * ★無銘剣の種類
     */
    protected static EnumNNSInfo arrEnumNNS[] = EnumNNSInfo.values();

    /**
     * ★無銘剣のアイコン群
     */
    protected static Icon[] arrIconNNS = new Icon[arrEnumNNS.length];
    protected static Icon iconInv = null;

    /**
     * ■コンストラクタ
     * @param par1
     * @param par2EnumToolMaterial
     */
    public ItemNNS(int par1, EnumToolMaterial par2EnumToolMaterial)
    {
        super(par1, par2EnumToolMaterial);
    }

    /**
     * ■Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
    {
        return true;
    }

    /**
     * ■onBlockDestroyed
     */
    @Override
    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving)
    {
        if ((double)Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D)
        {
            this.addItemDamage(par1ItemStack, 2, 0, par7EntityLiving);
        }

        return true;
    }

    /**
     * ■Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        int nSwordType = this.getSwordType(par1ItemStack);
        if (this.canRepairable(par1ItemStack, par3EntityPlayer) &&
            par1ItemStack == par3EntityPlayer.getCurrentEquippedItem() &&
            this.arrEnumNNS[nSwordType] == EnumNNSInfo.Attacker)
        {
            //▼「装備中」のNNS(Attacker)である。
            if (0 < par3EntityPlayer.swingProgressInt &&
                par3EntityPlayer.swingProgressInt < 3)
            {
                //▼剣を振るってる最中に右クリック
                if (!par2World.isRemote)
                {
                    //▼サーバーサイド
                    // ■衝撃波
                    EntityNNSMagic entityMagic = new EntityNNSMagic(par2World, par3EntityPlayer);
                    if (entityMagic != null) {
                      par2World.spawnEntityInWorld(entityMagic);
                    }
                }
                // ■SE
                par3EntityPlayer.playSound("random.bow", 0.7F, 1.0F);

            }
        }
        return super.onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
    }

    /**
     * ■Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
        //TODO
        //■
        if (par3Entity == null || !(par3Entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer)par3Entity;

        //■マスターの有無
        if (this.getMasterName(par1ItemStack) == null)
        {
            //▼マスター無
            if (par1ItemStack.hasDisplayName())
            {
                //▼マスター無 かつ オリジナル名
                // ■マスター登録
                this.setMasterName(par1ItemStack, player.getEntityName());
                this.setSwordName(par1ItemStack, par1ItemStack.getDisplayName());
                this.setFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_EFFECT);
                //if (!par2World.isRemote)
                //{
                //    player.addChatMessage("マスターを認識しました。");
                //}
                player.playSound("nns.recognition", 1.0F, 1.0F);
            }
            else
            {
                //▼マスター無 かつ 無銘
                // ■なにもしない
                par1ItemStack.setRepairCost(0);
            }
        }
        else
        {
            //▼マスター有
            if (this.isMasterName(par1ItemStack, player.getEntityName())) {
                //▼マスター有 かつ 本人
                this.addFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_EFFECT);
                par1ItemStack.setRepairCost(0);
            } else {
                //▼マスター有 かつ 別人
                this.delFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_EFFECT);
                par1ItemStack.setRepairCost(9999);
                //■銘が変わっていないか否か
                /*if (!this.getSwordName(par1ItemStack).contentEquals(par1ItemStack.getDisplayName()))
                {
                    //▼変わってる
                    this.addFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_NG);
                }*/
            }

            //■銘付けは一度のみ
            if (!this.getSwordName(par1ItemStack).contentEquals(par1ItemStack.getDisplayName()))
            {
                par1ItemStack.setItemName(this.getSwordName(par1ItemStack));
            }
        }

        //■
        if (this.getFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_LVMAX))
        {
            if (!this.getFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_NOTICED))
            {
                if (!par2World.isRemote)
                {
                    player.addChatMessage("レベルが最大に達しました。");
                }
                player.playSound("nns.lv_max", 1.0F, 1.0F);
                this.addFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_NOTICED);
            }
            this.delFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_LVMAX);
        }

        //■金床結果処理
        checkState(par1ItemStack);
    }


    /**
     * ■マスターが保有時、光る
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        boolean hasEffect = false;
        if (this.getSwordDamage(par1ItemStack) != par1ItemStack.getItemDamage())
        {
            //▼結果側
            hasEffect = this.getFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_EFFECT) &&
                        !this.getFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_NG);
        }
        else
        {
            //▼その他
            hasEffect = this.getFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_EFFECT) &&
                        this.canRepairable(par1ItemStack);
        }
        return hasEffect;
    }

    /**
     * ■マルチ描画するか否か
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    /**
     * ■Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        //TODO
        if (!this.canRepairable(par1ItemStack))
        {
            return false;
        }

        boolean isRepairable = false;

        //■金床結果から直接入れてしまった場合
        if (this.getSwordDamage(par1ItemStack) != par1ItemStack.getItemDamage())
        {
            checkState(par1ItemStack);
        }

        //■強化
        if (par1ItemStack.itemID == par2ItemStack.itemID)
        {
            //TODO
            //▼同素材強化
            //String par1IS_MasterN = this.getMasterName(par1ItemStack);
            String par2IS_MasterN = this.getMasterName(par2ItemStack);
            if (par2IS_MasterN == null)
            {
                //マスター無 + マスター[無] = OK
                //マスター有 + マスター[無] = OK
                this.addFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_REPAIR);
                this.delFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_NG);
            }
            else
            {
                //マスター無 + マスター[有] = NG
                //マスター有 + マスター[有] = NG
                //this.setFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_NG);
                this.addFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_REPAIR);
                this.delFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_NG);

            }
            isRepairable = true;
        }
        else
        {
            //▼アイテム強化
            int nSwordType = this.getSwordType(par1ItemStack);
            int nRItemID   = this.arrEnumNNS[nSwordType].nRepairItemID;
            if (nRItemID == par2ItemStack.itemID &&
                !this.getFlag(NORMAL_FLAG, par1ItemStack, FLAG_BITMASK_NG))
            {
                //▼強化用アイテムである かつ 強化可能
                isRepairable = true;
                this.addFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_REPAIR);
                this.delFlag(TRIAL_FLAG, par1ItemStack, FLAG_BITMASK_NG);
            }
        }

        return isRepairable;
    }

    /**
     * ■アイコン情報読込 ＆ 取得
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);

        String strName = this.getUnlocalizedName();
        strName = strName.replaceAll("item.", "");
        strName = strName.replaceAll("Beginning", "");

        for (EnumNNSInfo enumNNS : this.arrEnumNNS)
        {
            this.arrIconNNS[enumNNS.ordinal()] = par1IconRegister.registerIcon(strName + enumNNS.toString());
        }

        iconInv = par1IconRegister.registerIcon(strName + "Inv");
    }

    /* =========================================================== FORGE METHOD OVERRIDE ===============================================================*/
    /**
     * Called when a player drops the item into the world,
     * returning false from this will prevent the item from
     * being removed from the players inventory and spawning
     * in the world
     *
     * @param player The player that dropped the item
     * @param item The item stack, before the item is removed.
     */
    @Override
    public boolean onDroppedByPlayer(ItemStack itemStack, EntityPlayer player)
    {
        return false;
    }

    /**
     * ■Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity targetEntity)
    {
        //■生物が対象
        if (targetEntity != null && !(targetEntity instanceof EntityLiving))
        {
            return false;
        }

        EntityLiving targetLiving = (EntityLiving)targetEntity;

        //■無敵時間 もしくは 死んでる
        if (targetLiving.hurtTime != 0 || !targetLiving.isEntityAlive())
        {
            return false;
        }

        //■経験値設定
        this.addItemDamage(itemStack, 1, (targetEntity instanceof IMob) ? 2 : 1, player);

        return false;
    }

    /**
     * ■Return the correct icon for rendering based on the supplied ItemStack and render pass.
     *
     * Defers to {@link #getIconFromDamageForRenderPass(int, int)}
     * @param itemStack to render for
     * @param pass the multi-render pass
     * @return the icon
     */
    @Override
    public Icon getIcon(ItemStack itemStack, int pass)
    {
        if (pass == 1)
        {
            return iconInv;
        }

        int nType = this.getSwordType(itemStack);
        return this.arrIconNNS[nType];
    }

    /**
     * ■Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param itemstack1 The item
     * @param itemstack2 The book
     * @return if the enchantment is allowed
     */
    @Override
    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return false;
    }

    /**
     * ■An itemstack sensitive version of getDamageVsEntity - allows items to handle damage based on
     * itemstack data, like tags. Falls back to getDamageVsEntity.
     *
     * @param par1Entity The entity being attacked (or the attacking mob, if it's a mob - vanilla bug?)
     * @param itemStack The itemstack
     * @return the damage
     */
    @Override
    public int getDamageVsEntity(Entity par1Entity, ItemStack itemStack)
    {
        int nSwordType = this.getSwordType(itemStack);
        if (!ItemNNS.getFlag(NORMAL_FLAG, itemStack, FLAG_BITMASK_EFFECT))
        {
            return 1;
        }
        return this.arrEnumNNS[nSwordType].getDamageVsEntity(this.getExp(EXP_HIT, itemStack),
                                                             this.getLevel(itemStack));
    }

    /**
     * ■Called when a entity tries to play the 'swing' animation.
     *  矢切り用処理記述
     *
     * @param entityLiving The entity swinging the item.
     * @param itemStack The Item stack
     * @return True to cancel any further processing by EntityLiving
     */
    @Override
    public boolean onEntitySwing(EntityLiving entityLiving, ItemStack itemStack)
    {
        if (this.arrEnumNNS[this.getSwordType(itemStack)].canAllowCut())
        {
            // entityLiving.getLookVec() とか使うとより良くなりそう。
            //■矢切り
            double dAmbit = 4D;
            double posX = entityLiving.posX;
            double posY = entityLiving.posY;
            double posZ = entityLiving.posZ;
            AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(posX - dAmbit,
                                                                     posY - dAmbit,
                                                                     posZ - dAmbit,
                                                                     posX + dAmbit,
                                                                     posY + dAmbit,
                                                                     posZ + dAmbit);
            List targetList = entityLiving.worldObj.getEntitiesWithinAABBExcludingEntity(entityLiving, aabb);

            if (targetList.size() > 0 && !entityLiving.worldObj.isRemote)
            {
                boolean isAllowDead = false;
                for (int idx = 0; idx < targetList.size(); idx++)
                {
                    Entity entity = (Entity)targetList.get(idx);
                    if (entity instanceof EntityArrow && !entity.worldObj.isRemote)
                    {
                        entity.setDead();
                        isAllowDead = true;
                    }
                }

                if (isAllowDead)
                {
                    this.addItemDamage(itemStack, 1, 2, entityLiving);
                    entityLiving.playSound("random.bow", 1.0F, 1.0F);
                }
            }
        }

        return false;
    }

    /* =========================================================== 自作 METHOD ===============================================================*/
    /**
     * ■ 状態チェック
     * @param itemStack
     */
    protected void checkState(ItemStack itemStack)
    {
        //TODO
        if (this.getSwordDamage(itemStack) != itemStack.getItemDamage())
        {
            /**
             * ▼剣ダメージが不一致
             */
            if (this.getFlag(TRIAL_FLAG, itemStack, FLAG_BITMASK_NG))
            {
                //▼NG行為を行った
                //ItemNoNameSword.setFlag(NORMAL_FLAG, itemStack, FLAG_BITMASK_NG);
                this.addFlag(NORMAL_FLAG, itemStack, FLAG_BITMASK_NG);
            }
            else if (this.getFlag(TRIAL_FLAG, itemStack, FLAG_BITMASK_REPAIR))
            {
                //▼金床を用いた修理
                int nExp = this.getSwordDamage(itemStack) - itemStack.getItemDamage();
                this.addExp(EXP_REPAIR, itemStack, nExp);

                int nNowExp    = this.getExp(EXP_REPAIR, itemStack);
                int nSwordType = this.getSwordType(itemStack);
                if (nNowExp == this.arrEnumNNS[nSwordType].getRepairExpMax())
                {
                    this.addFlag(NORMAL_FLAG, itemStack, FLAG_BITMASK_LVMAX);
                }
            }
            else
            {
                //▼砥石を用いた修理
            }

            //■ダメージ一致処理
            this.setSwordDamage(itemStack);
            if (itemStack.getItemDamage() == 0)
            {
                this.setStackDamage(itemStack, 0);
            }
        }

        this.setFlag(TRIAL_FLAG, itemStack, 0);
    }


    /**
     * ■アイテムにダメージ値 追加
     * @param itemStack
     * @param nItemDamage
     */
    public static void addItemDamage(ItemStack itemStack, int nDamage, int nHitExp, EntityLiving living)
    {
        //■表示ダメージゲージ 調整用処理
        if (itemStack.getItemDamage() == 0) { itemStack.setItemDamage(1); }

        //■スタックダメージを加味
        nDamage += ItemNNS.getStackDamage(itemStack);

        int nSDSize = ItemNNS.getStackDamageSize(itemStack);
        int nItemDamage  = nDamage / nSDSize;
        int nStackDamage = nDamage % nSDSize;

        //■アイテムにダメージ値 追加
        if (living != null)
        {
            itemStack.damageItem(nItemDamage, living);
        }
        else
        {
            itemStack.setItemDamage(itemStack.getItemDamage() + nItemDamage);
            if (itemStack.getItemDamage() <= 0)
            {
                itemStack.stackSize = 0;
            }
        }

        //■NBTTagに各値 追加
        if (itemStack.getItemDamage() > 0)
        {
            //▼まだ壊れていないのでダメージ追加
            ItemNNS.setSwordDamage(itemStack);
            ItemNNS.setStackDamage(itemStack, nStackDamage);
            //▼攻撃経験値 追加
            if (living instanceof EntityPlayer)
            {
                ItemNNS.addExp(EXP_HIT, itemStack, nHitExp);
            }
        }
    }

    /**
     * ■経験値 追加
     * @param strTag (NBTTag名:EXP_REPAIR or EXP_HIT)
     * @param itemStack
     * @param nExp
     */
    protected static void addExp(String strTag, ItemStack itemStack, int nExp)
    {
        if (ItemNNS.canRepairable(itemStack))
        {
            //▼強化可能条件をクリア
            int nNowExp = ItemNNS.getExp(strTag, itemStack);
            ItemNNS.setExp(strTag, itemStack, nExp + nNowExp);
        }
    }

    /**
     * ■剣のレベル 取得
     * @param itemStack
     * @return
     */
    public static int getLevel(ItemStack itemStack)
    {
        int nExp = ItemNNS.getExp(EXP_REPAIR, itemStack);
        return  nExp / EnumNNSInfo.getRepairExpRange();
    }

    /**
     * ■強化可能か否か
     * ・マスターがいる
     * ・強化NGフラグがfalse
     * @param itemStack
     * @return
     */
    public static boolean canRepairable(ItemStack itemStack)
    {
        boolean canRepairable = false;
        String strMasterN = ItemNNS.getMasterName(itemStack);
        if (strMasterN != null &&
            !ItemNNS.getFlag(NORMAL_FLAG, itemStack, FLAG_BITMASK_NG))
        {
            //▼修理可能条件
            // ・マスターがいる
            // ・強化NGフラグがfalse
            canRepairable = true;
        }
        return canRepairable;
    }

    /**
     * ■強化可能か否か
     * ・現持ち主がマスターである
     * ・マスターがいる
     * ・強化NGフラグがfalse
     * @param itemStack
     * @param player
     * @return
     */
    public static boolean canRepairable(ItemStack itemStack, EntityPlayer player)
    {
        return ItemNNS.isMasterName(itemStack, player.getEntityName()) &&
               canRepairable(itemStack);
    }

    /**
     * ■剣のダメージスタック量 取得
     * @param itemStack
     * @return
     */
    public static int getStackDamageSize(ItemStack itemStack)
    {
        return ItemNNS.getLevel(itemStack) + 1;
    }

    /* =========================================================== NBTTag系 自作 METHOD ===============================================================*/
    /**
     * ■マスターの名前 取得
     * @param itemStack
     * @return
     */
    public static String getMasterName(ItemStack itemStack)
    {
        String strName = null;
        if (itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey(MASTER_NAME))
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            strName = nbt.getString(MASTER_NAME);
        }

        return strName;
    }

    /**
     * ■マスターの名前 設定
     * @param itemStack
     * @param strName
     */
    public static void setMasterName(ItemStack itemStack, String strName)
    {
        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setString(MASTER_NAME, strName);
    }

    /**
     * ■マスターの名前 判断
     * @param itemStack
     * @param strName
     * @return
     */
    public static boolean isMasterName(ItemStack itemStack, String strName)
    {
        String sName = ItemNNS.getMasterName(itemStack);
        if (sName == null) { return false; }
        return strName.contentEquals(sName);
    }

    /**
     * ■剣の名前 取得（従来のTagとは別）
     * @param itemStack
     * @return
     */
    public static String getSwordName(ItemStack itemStack)
    {
        String strName = null;
        if (itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey(MASTER_NAME))
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            strName = nbt.getString(SWORD_NAME);
        }

        return strName;
    }

    /**
     * ■剣の名前 設定（従来のTagとは別）
     * @param itemStack
     * @param strName
     */
    public static void setSwordName(ItemStack itemStack, String strName)
    {
        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setString(SWORD_NAME, strName);
    }

    /**
     * ■剣の発達度合い 取得 (NBTTag)
     * @param itemStack
     * @return nSwordType (0～arrEnumNNS.length)
     */
    public static int getSwordType(ItemStack itemStack)
    {
        int nSwordType = 0;

        if (itemStack.hasTagCompound())
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            nSwordType = nbt.getInteger(SWORD_TYPE);
        }

        nSwordType = MathHelper.clamp_int(nSwordType, 0, arrEnumNNS.length - 1);

        return nSwordType;
    }

    /**
     * ■剣の発達度合い 設定 (NBTTag)
     * @param itemStack
     * @param nSwordType (0～arrEnumNNS.length:正常　それ以外は[0]or[length-1]に再設定
     */
    public static void setSwordType(ItemStack itemStack, int nSwordType)
    {
        nSwordType = MathHelper.clamp_int(nSwordType, 0, arrEnumNNS.length - 1);

        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setInteger(SWORD_TYPE, nSwordType);
    }

    /**
     * ■剣のダメージ値 取得 (NBTTag)（金床結果判定の際に用いる）
     * @param itemStack
     * @return
     */
    public static int getSwordDamage(ItemStack itemStack)
    {
        int nSwordDamage = 0;

        if (itemStack.hasTagCompound())
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            nSwordDamage = nbt.getInteger(SWORD_DAMAGE);
        }

        return nSwordDamage;
    }

    /**
     * ■剣のダメージ値 設定 (NBTTag)（金床結果判定の際に用いる）
     * @param itemStack
     * @param nSwordDamage
     */
    public static void setSwordDamage(ItemStack itemStack)
    {
        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setInteger(SWORD_DAMAGE, itemStack.getItemDamage());
    }

    /**
     * ■アイテムダメージスタック 取得
     * @param itemStack
     * @return
     */
    public static int getStackDamage(ItemStack itemStack)
    {
        int nStackDamage = 0;

        if (itemStack.hasTagCompound())
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            nStackDamage = nbt.getInteger(STACK_DAMAGE);
        }

        return nStackDamage;
    }

    /**
     * ■アイテムダメージスタック 設定
     * @param itemStack
     * @param nStackDamage
     */
    public static void setStackDamage(ItemStack itemStack, int nStackDamage)
    {
        nStackDamage = MathHelper.clamp_int(nStackDamage, 0, ItemNNS.getLevel(itemStack));

        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setInteger(STACK_DAMAGE, nStackDamage);
    }

    /**
     * ■経験値 取得
     * @param strTag (NBTTag名:EXP_REPAIR or EXP_HIT)
     * @param itemStack
     * @return nExp (0:Err, 1以上:正常）
     */
    public static int getExp(String strTag, ItemStack itemStack)
    {
        int nExp = 0;

        if (itemStack.hasTagCompound())
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            nExp = nbt.getInteger(strTag);
        }

        int nSwordType = ItemNNS.getSwordType(itemStack);

        if (strTag.contentEquals(EXP_REPAIR))
        {
            nExp = MathHelper.clamp_int(nExp, 1, arrEnumNNS[nSwordType].getRepairExpMax());
        }
        else if (strTag.contentEquals(EXP_HIT))
        {
            nExp = MathHelper.clamp_int(nExp, 1, arrEnumNNS[nSwordType].getHitExpMax());
        }
        else
        {
            System.out.println("[NNS_Err] ItemNoNameSword.getExp -> strTag");
        }

        return nExp;
    }

    /**
     * ■経験値 設定
     * @param strTag (NBTTag名:EXP_REPAIR or EXP_HIT)
     * @param itemStack
     * @param nExp
     */
    public static void setExp(String strTag, ItemStack itemStack, int nExp)
    {
        int nSwordType = ItemNNS.getSwordType(itemStack);
        if  (strTag.contentEquals(EXP_REPAIR))
        {
            nExp = MathHelper.clamp_int(nExp, 1, arrEnumNNS[nSwordType].getRepairExpMax());
        }
        else if (strTag.contentEquals(EXP_HIT))
        {
            nExp = MathHelper.clamp_int(nExp, 1, arrEnumNNS[nSwordType].getHitExpMax());
        }
        else
        {
            System.out.println("[NNS_Err] ItemNoNameSword.setExp -> strTag");
            return;
        }

        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setInteger(strTag, nExp);
    }

    /**
     * ■フラグ 取得（指定）
     * @param itemStack
     * @return
     */
    public static boolean getFlag(String strTag, ItemStack itemStack, int nBitMask)
    {
        return (ItemNNS.getFlag(strTag, itemStack) & nBitMask) == nBitMask;
    }

    /**
     * ■フラグ 取得（全体)
     * @param strTag
     * @param itemStack
     * @return
     */
    public static int getFlag(String strTag, ItemStack itemStack)
    {
        int nFlag = 0;

        if (itemStack.hasTagCompound())
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            nFlag = nbt.getInteger(strTag);
        }

        return nFlag;
    }

    /**
     * ■フラグ 設定（指定）
     * @param itemStack
     * @param isNgFlag
     */
    public static void setFlag(String strTag, ItemStack itemStack, int nBitMask)
    {
        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = itemStack.getTagCompound();
        nbt.setInteger(strTag, nBitMask);
    }

    /**
     * ■フラグ 追加
     * @param strTag
     * @param itemStack
     * @param nBitMask
     */
    public static void addFlag(String strTag, ItemStack itemStack, int nBitMask)
    {
        nBitMask |= ItemNNS.getFlag(strTag, itemStack);
        ItemNNS.setFlag(strTag, itemStack, nBitMask);
    }

    /**
     * ■フラグ 削除
     * @param strTag
     * @param itemStack
     * @param nBitMask
     */
    public static void delFlag(String strTag, ItemStack itemStack, int nBitMask)
    {
        int nNowBit = ItemNNS.getFlag(strTag, itemStack);
        nNowBit &= ~nBitMask;
        ItemNNS.setFlag(strTag, itemStack, nNowBit);

    }
}
