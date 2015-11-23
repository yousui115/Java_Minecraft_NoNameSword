package mods.nnsmod;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public enum EnumNNSInfo
{
    Beginning(   Item.ingotIron.itemID,   4, 0.0F, 0),
    Iron(        Item.ingotIron.itemID,   8, 0.0F, 0),
    Attacker(    Item.ingotGold.itemID, 100, 0.2F, 1),
    Blocker(    Block.obsidian.blockID, 100, 0.1F, 2);
    //保留 Berserker;

    /**
     * ★修理アイテムID
     */
    public int nRepairItemID;
    /**
     * ★最大レベル
     */
    public int nMaxLevel;
    /**
     * ★攻撃力補正
     */
    public float fVsEntityOfst;
    /**
     * ★耐久力補正
     */
    public int nItemDamageOfst;

    /**
     * ■コンストラクタ
     * @param nRepairItemID
     * @param nMaxLevel
     * @param fVsEntityOfst
     * @param nItemDamageOfst
     */
    private EnumNNSInfo(int nRepairItemID, int nMaxLevel, float fVsEntityOfst, int nItemDamageOfst)
    {
        this.nRepairItemID    = nRepairItemID;
        this.nMaxLevel       = nMaxLevel;
        this.fVsEntityOfst   = fVsEntityOfst;
        this.nItemDamageOfst = nItemDamageOfst;
    }

    /**
     * ■修理経験値（最大）
     * @return
     */
    public int getRepairExpMax()
    {
        return nMaxLevel * EnumNNSInfo.getRepairExpRange();
    }

    /**
     * ■攻撃経験値（最大）
     * @return
     */
    public int getHitExpMax()
    {
        int nHitExpMax = 0;

        switch(this)
        {
            case Beginning:
                nHitExpMax = 0;
                break;

            case Iron:
                nHitExpMax = 200;
                break;

            default:
                nHitExpMax = nMaxLevel * EnumNNSInfo.getHitExpRange();
                break;
        }

        return nHitExpMax;
    }

    /**
     * ■ダメージ量
     * @param nHitExp ⇒ getExp(EXP_HIT～ をそのまま入れる
     * @param nLevel  ⇒ getLevel() をそのまま入れる
     * @return
     */
    public int getDamageVsEntity(int nHitExp, int nLevel)
    {
        int nDamage = 0;

        switch(this)
        {
        case Beginning:
            nDamage = 1;
            break;

        case Iron:
            nDamage = 4;
            break;

        default:
            nHitExp = MathHelper.clamp_int(nHitExp, 1, nLevel * EnumNNSInfo.getHitExpRange());
            float fDamage = (float)(nHitExp / EnumNNSInfo.getHitExpRange()) * fVsEntityOfst;
            nDamage = 4 + (int)fDamage;
            break;
        }
        return nDamage;
    }

    /**
     * ■修理経験値（区切り）
     * @return 10
     */
    public static int getRepairExpRange()
    {
        return NNSMod.enumTooMaterial.getMaxUses() / 4;
    }

    /**
     * ■攻撃経験値（区切り）
     * @return 100
     */
    public static int getHitExpRange()
    {
        return 100;
    }

    /**
     * ■矢切りが可能か否か
     * @return
     */
    public boolean canAllowCut()
    {
        boolean canAllowCut = false;

        switch(this)
        {
        case Iron:
        case Attacker:
            canAllowCut = true;
            break;

        default:
            canAllowCut = false;
            break;
        }

        return canAllowCut;
    }
 }
