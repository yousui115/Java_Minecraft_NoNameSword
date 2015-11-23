package mods.nnsmod;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "NoNameSwordMod", name = "No Name Sword Mod", version = "MC1.5.1 : Forge7.7.1.651 : v1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class NNSMod
{
    // クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = "mods.nnsmod.client.ClientProxy", serverSide = "mods.nnsmod.CommonProxy")
    public static CommonProxy proxy;

    // 自身のインスタンス
    @Mod.Instance("NoNameSwordMod")
    public static NNSMod instance;

    // ★Item
    public static Item item_NNS;
    public static int  itemID_NNS;

    public static Item item_NNSRubstone;
    public static int  itemID_NNSRubstone;

    // ★Entity
    public static int  entityID_NNSMagic;
    public static int  entityID_NNS;

    // ★Enum
    public static EnumToolMaterial enumTooMaterial;

    // ★Flag
    public static boolean canEnderChestRespawn;

    private static boolean isDebug = false;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

        try
        {
            cfg.load();
            Property prop;

            //●ItemID
            // ■無名剣のアイテムID
            prop = cfg.getItem("ItemID_NoNameSword", 7001);
            prop.comment = "「無銘剣」のアイテムID";
            itemID_NNS = prop.getInt() - 256;

            // ■専用砥石のアイテムID
            prop = cfg.getItem("ItemID_NNSRubStone", 7002);
            prop.comment = "「専用砥石」のアイテムID";
            itemID_NNSRubstone = prop.getInt() - 256;

            //●EntityID
            // ■無銘剣魔法のエンティティID
            prop = cfg.get("Entity", "EntityID_NNSMagic", 245);
            prop.comment = "「無銘剣魔法」のエンティティID";
            entityID_NNSMagic = prop.getInt();

            // ■無銘剣のエンティティID
            prop = cfg.get("Entity", "EntityID_NNS", 246);
            prop.comment = "「突き立てられた無銘剣」のエンティティID";
            entityID_NNS = prop.getInt();

            //●Flag
            // ■無銘剣のエンダーチェスト収納
            prop = cfg.get("Other", "canEnderChestRespawn", true);
            prop.comment = "プレイヤー死亡時、「無銘剣」がエンダーチェストに収納(される=true : されない=false)";
            canEnderChestRespawn = prop.getBoolean(true);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Error Message");
        }
        finally
        {
            cfg.save();
        }
    }

    @Mod.Init
    public void init(FMLInitializationEvent event)
    {
        //★Enum
        enumTooMaterial = EnumHelper.addToolMaterial("NONAME", 0, 41, 0.0F, 0, 0);

        //★Item
        // ●NNS
        item_NNS = (new ItemNNS(itemID_NNS, enumTooMaterial))
                                               .setNoRepair()
                                               .setUnlocalizedName("nnsmod:NoNameSword_Beginning");
        // ▼アイテム名登録
        LanguageRegistry.addName(item_NNS,  "No Name Sword");
        LanguageRegistry.instance().addNameForObject(item_NNS, "ja_JP", "無銘剣");
        // ▼アイテムの独自モデル表示
//        MinecraftForgeClient.registerItemRenderer(itemNoNameSword.itemID, (IItemRenderer)itemNoNameSword);

        // ●NNSRubstone
        item_NNSRubstone = (new ItemNNSRubstone(itemID_NNSRubstone))
                                               .setUnlocalizedName("nnsmod:NNSRubstone");
        // ▼アイテム名登録
        LanguageRegistry.addName(item_NNSRubstone, "NNS Rubstone");
        LanguageRegistry.instance().addNameForObject(item_NNSRubstone, "ja_JP", "専用砥石");
        GameRegistry.addRecipe(
                new ItemStack(item_NNSRubstone),
                new Object[]
                {
                    "#@",
                    '#', Block.sandStone,
                    '@', Block.blockClay
                });


        // ▼構造物チェストへ収納
        ItemStack itemStack = new ItemStack(item_NNS);
        //  ★Type = Beginning
        ItemNNS.setSwordType(itemStack, EnumNNSInfo.Beginning.ordinal());
        //  ★耐久値はギリギリ
        ItemNNS.addItemDamage(itemStack, itemStack.getMaxDamage() - 2, 0, null);
        ItemNNS.setExp(ItemNNS.EXP_REPAIR, itemStack, 1);
        //ChestGenHooks.getInfo(ChestGenHooks.BONUS_CHEST).addItem(new WeightedRandomChestContent(itemStack, 0, 1, 1));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(itemStack, 0, 1, 1));
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(itemStack, 0, 1, 1));
        ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(new WeightedRandomChestContent(itemStack, 0, 1, 1));

        //■デバッグレシピ
        if(isDebug)
        {
            GameRegistry.addRecipe(
                    itemStack,
                    new Object[]
                    {
                        "#",
                        '#', Block.dirt,
                    });
        }

        //■レシピ追加
        GameRegistry.addRecipe(new RecipesNNSEnhance());
        GameRegistry.addRecipe(new RecipesNNSRubstone());

        //★Entity
        // ●魔法
        EntityRegistry.registerGlobalEntityID(EntityNNSMagic.class, "NNSMagic", entityID_NNSMagic);
        EntityRegistry.registerModEntity(EntityNNSMagic.class, "NNSMagic", 1, this, 250, 1, true);

        // ●突き立てられた剣
        EntityRegistry.registerGlobalEntityID(EntityNNS.class, "NNS", entityID_NNS);
        EntityRegistry.registerModEntity(EntityNNS.class, "NNS", 2, this, 250, 1, false);

        //■ プロキシ設定
        // サーバー側は何もしない, クライアント側ではレンダーの登録とかが行われる
        proxy.registerRenderers();
        proxy.registerTextures();

        //■イベントの追加
        MinecraftForge.EVENT_BUS.register(new NNSEventHooks());
        MinecraftForge.TERRAIN_GEN_BUS.register(new NNSTerrainHooks());

    }
}
