package mods.nnsmod.structure;

import java.util.Random;

import mods.nnsmod.EntityNNS;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraftforge.common.ChestGenHooks;

public class ComponentAbandonedSmithy extends StructureComponent
{
    private int averageGroundLevel = -1;
    private boolean hasMadeChest;

    public ComponentAbandonedSmithy(Random par1Random, int nChnkCntrX, int nChnkCntrZ)
    {
        super(0);
        int par5 = 10;
        int par6 = 6;
        int par7 = 7;
        this.boundingBox = new StructureBoundingBox(nChnkCntrX, 64, nChnkCntrZ,
                                                    nChnkCntrX + par5 - 1,
                                                    64 + par6 - 1,
                                                    nChnkCntrZ + par7 - 1);
        this.coordBaseMode = 0;
    }

    @Override
    public boolean addComponentParts(World p1World, Random p2Rnd, StructureBoundingBox p3SBB)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(p1World, p3SBB);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
        }

//        this.placeBlockAtCurrentPosition(p1World,                         //World
//                                         Block.blockDiamond.blockID,      //BlockID
//                                         0,                               //BlockMetaData
//                                         7,                               //基準点からのオフセットX
//                                         1,                               //                      Y
//                                         5,                               //                      Z
//                                         p3SBB);                          //BoundingBox(建設可能指定領域)


        int voidID = 0;

        /**
         * ◆地上部作成
         */
        //■空間作り（0空間：家全体）
        this.fillWithBlocks(p1World, p3SBB, 0, 1, 0, 9, 4, 6, voidID, voidID, false);

        //■床
        this.fillWithBlocks(p1World, p3SBB, 0, 0, 0, 9, 0, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        for (int idx = 0; idx <= 9; idx++)
        {
            for(int idz = 0; idz <= 6; idz++)
            {
                int numRnd = p2Rnd.nextInt(4);
                if (numRnd == 0)
                {
                    this.placeBlockAtCurrentPosition(p1World, Block.stoneSingleSlab.blockID , 3, idx, 0, idz, p3SBB);
                }
                else if (numRnd == 1)
                {
                    this.placeBlockAtCurrentPosition(p1World, Block.cobblestoneMossy.blockID , 0, idx, 0, idz, p3SBB);
                }
            }
        }

        //■天井
        this.fillWithBlocks(p1World, p3SBB, 0, 4, 0, 9, 4, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        for (int idx = 0; idx <= 9; idx++)
        {
            for(int idz = 0; idz <= 6; idz++)
            {
                int numRnd = p2Rnd.nextInt(4);
                if (numRnd == 0)
                {
                    this.placeBlockAtCurrentPosition(p1World, voidID, voidID, idx, 4, idz, p3SBB);
                }
                else if (numRnd == 1)
                {
                    this.placeBlockAtCurrentPosition(p1World, Block.cobblestoneMossy.blockID , voidID, idx, 4, idz, p3SBB);
                }
            }
        }

        //■天井（ハーフブロック：装飾）
        this.fillWithBlocks(p1World, p3SBB, 0, 5, 0, 9, 5, 6, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
        setRandomBlock(p1World, p2Rnd, p3SBB, 0, 5, 0, 9, 5, 6, voidID , voidID);

        //■天井（装飾内の0空間）
        this.fillWithBlocks(p1World, p3SBB, 1, 5, 1, 8, 5, 5, voidID, voidID, false);

        //■柱（木：南東）
//        this.fillWithBlocks(p1World, p3SBB, 0, 1, 0, 0, 4, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 0, 1, 0, 0, 2, 0, Block.wood.blockID, 1, Block.wood.blockID, 1, false);

        //■柱（木：入り口右）
//        this.fillWithBlocks(p1World, p3SBB, 3, 1, 0, 3, 4, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 3, 2, 0, 3, 4, 0, Block.wood.blockID, 1, Block.wood.blockID, 1, false);

        //■柱（木：北東）
//        this.fillWithBlocks(p1World, p3SBB, 0, 1, 6, 0, 4, 6, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 0, 1, 6, 0, 4, 6, Block.wood.blockID, 1, Block.wood.blockID, 1, false);

        //■壁（木材：南）
//        this.fillWithBlocks(p1World, p3SBB, 1, 1, 0, 2, 3, 0, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 1, 1, 0, 2, 3, 0, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
        setRandomBlock(p1World, p2Rnd, p3SBB, 1, 1, 0, 2, 3, 0, voidID , voidID);

        //■壁（木材：入り口真上）
//        this.placeBlockAtCurrentPosition(p1World, Block.planks.blockID, voidID, 3, 3, 1, p3SBB);
        this.placeBlockAtCurrentPosition(p1World, Block.planks.blockID, 1, 3, 3, 1, p3SBB);

        //■壁（木材：入り口左）
//        this.fillWithBlocks(p1World, p3SBB, 3, 1, 2, 3, 3, 2, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 3, 2, 2, 3, 3, 2, Block.planks.blockID, 1, Block.planks.blockID, 1, false);

        //■壁（木材：南）
//        this.fillWithBlocks(p1World, p3SBB, 4, 1, 3, 5, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 4, 1, 3, 5, 3, 3, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
        setRandomBlock(p1World, p2Rnd, p3SBB, 4, 2, 3, 5, 3, 3, voidID , voidID);

        //■壁（木材：東）
//        this.fillWithBlocks(p1World, p3SBB, 0, 1, 1, 0, 3, 5, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 0, 1, 1, 0, 3, 5, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
        setRandomBlock(p1World, p2Rnd, p3SBB, 0, 1, 1, 0, 3, 5, voidID , voidID);

        //■壁（木材：北）
//        this.fillWithBlocks(p1World, p3SBB, 1, 1, 6, 5, 3, 6, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocksMeta(p1World, p3SBB, 1, 1, 6, 5, 3, 6, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
        setRandomBlock(p1World, p2Rnd, p3SBB, 1, 1, 6, 5, 3, 6, voidID , voidID);

        //■支柱（柵：２本）
//        this.fillWithBlocks(p1World, p3SBB, 5, 1, 0, 5, 3, 0, Block.fence.blockID, Block.fence.blockID, false);
//        this.fillWithBlocks(p1World, p3SBB, 9, 1, 0, 9, 3, 0, Block.fence.blockID, Block.fence.blockID, false);
        this.placeBlockAtCurrentPosition(p1World, Block.cobblestoneWall.blockID, 1, 5, 1, 0, p3SBB);
        this.fillWithBlocks(p1World, p3SBB, 9, 2, 0, 9, 3, 0, Block.cobblestoneWall.blockID, Block.cobblestoneWall.blockID, false);

        //■空間作り（丸石で敷き詰め：黒曜石付近）
        this.fillWithBlocks(p1World, p3SBB, 6, 1, 4, 9, 4, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        setRandomBlock(p1World, p2Rnd, p3SBB, 6, 1, 4, 9, 4, 6, Block.cobblestoneMossy.blockID , voidID);
        setRandomBlock(p1World, p2Rnd, p3SBB, 6, 1, 4, 9, 4, 6, voidID, voidID);
        this.placeBlockAtCurrentPosition(p1World, Block.cobblestone.blockID, voidID, 6, 1, 4, p3SBB);

        //■黒曜石
        this.placeBlockAtCurrentPosition(p1World, Block.obsidian.blockID, voidID, 7, 1, 5, p3SBB);
        this.placeBlockAtCurrentPosition(p1World, Block.obsidian.blockID, voidID, 8, 1, 5, p3SBB);

        //■鉄柵
//        this.placeBlockAtCurrentPosition(p1World, Block.fenceIron.blockID, 0, 9, 2, 5, p3SBB);
        this.placeBlockAtCurrentPosition(p1World, Block.fenceIron.blockID, voidID, 9, 2, 4, p3SBB);

        //■0空間（黒曜石上部）
        this.fillWithBlocks(p1World, p3SBB, 7, 2, 4, 8, 2, 5, voidID, voidID, false);

        //■壁（丸石：竈下）
        this.placeBlockAtCurrentPosition(p1World, Block.cobblestone.blockID, voidID, 6, 1, 3, p3SBB);

        //■竈
        this.placeBlockAtCurrentPosition(p1World, Block.furnaceIdle.blockID, 2, 6, 2, 3, p3SBB);
        //this.placeBlockAtCurrentPosition(p1World, Block.furnaceIdle.blockID, voidID, 6, 3, 3, p3SBB);

        //■金床（のつもり？）
        //this.placeBlockAtCurrentPosition(p1World, Block.stoneDoubleSlab.blockID, voidID, 8, 1, 1, p3SBB);
        this.placeBlockAtCurrentPosition(p1World, Block.anvil.blockID, 1, 8, 1, 1, p3SBB);
        //int n = p1World.getBlockMetadata(8, 1, 1);

        //■窓ガラス（東：手前）
//        this.placeBlockAtCurrentPosition(p1World, Block.thinGlass.blockID, voidID, 0, 2, 2, p3SBB);
        //■窓ガラス（東：奥）
//        this.placeBlockAtCurrentPosition(p1World, Block.thinGlass.blockID, voidID, 0, 2, 4, p3SBB);
        //■窓ガラス（北：手前）
        this.placeBlockAtCurrentPosition(p1World, Block.thinGlass.blockID, voidID, 2, 2, 6, p3SBB);
        //■窓ガラス（北：奥）
//        this.placeBlockAtCurrentPosition(p1World, Block.thinGlass.blockID, voidID, 4, 2, 6, p3SBB);

        //■テーブル足
        this.placeBlockAtCurrentPosition(p1World, Block.fence.blockID, voidID, 2, 1, 4, p3SBB);
        //■テーブル
//        this.placeBlockAtCurrentPosition(p1World, Block.pressurePlatePlanks.blockID, voidID, 2, 2, 4, p3SBB);

        //■椅子とかなんやら
        this.placeBlockAtCurrentPosition(p1World, Block.planks.blockID, 1, 1, 1, 5, p3SBB);
        this.placeBlockAtCurrentPosition(p1World, Block.stairsWoodSpruce.blockID, this.getMetadataWithOffset(Block.stairsWoodSpruce.blockID, 2), 2, 1, 5, p3SBB);
//        this.placeBlockAtCurrentPosition(p1World, Block.stairsWoodOak.blockID, this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 1), 1, 1, 4, p3SBB);
        this.placeBlockAtCurrentPosition(p1World, Block.flowerPot.blockID , voidID, 1, 2, 5, p3SBB);

        //■チェスト
        if (!this.hasMadeChest)
        {
            int nOfstX = this.getXWithOffset(5, 5);
            int nOfstY = this.getYWithOffset(1);
            int nOfstZ = this.getZWithOffset(5, 5);

            if (p3SBB.isVecInside(nOfstX, nOfstY, nOfstZ))
            {
                this.hasMadeChest = true;
                this.generateStructureChestContents(p1World, p3SBB, p2Rnd,
                                                    5, 1, 5,
                                                    ChestGenHooks.getItems(ChestGenHooks.VILLAGE_BLACKSMITH, p2Rnd),
                                                    ChestGenHooks.getCount(ChestGenHooks.VILLAGE_BLACKSMITH, p2Rnd));
            }
        }

        /**
         * ◆地下部作成
         */
        //■空間作り（土で埋める）
        for (int idz = 2; idz <= 6; ++idz)
        {
            for (int idx = 1; idx <= 6; ++idx)
            {
                this.fillCurrentPositionBlocksDownwardsCoercion(p1World, Block.dirt.blockID, voidID, idx, -1, idz, -7, p3SBB);
            }
        }

        //■0空間
        this.fillWithBlocksMetaCoercion(p1World, p3SBB, 3, -4, 3, 5, -3, 5, voidID, voidID, voidID, voidID, false);

        //■トラップドア
        this.placeBlockAtCurrentPosition(p1World, Block.trapdoor.blockID, 2, 5, 1, 4, p3SBB);

        //■梯子用壁
        this.placeBlockAtCurrentPosition(p1World, Block.cobblestoneMossy.blockID, voidID, 6, 0, 4, p3SBB);

        //■はしご
        //this.placeBlockAtCurrentPosition(p1World, Block.ladder.blockID, 4, 5, 0, 4, p3SBB);
        for (int idx = 0; idx >= -4; idx--) {
            this.placeBlockAtCurrentPositionCoercion(p1World, Block.ladder.blockID, 4, 5, idx, 4, p3SBB);
        }

        //■エンダーチェスト
        this.placeBlockAtCurrentPositionCoercion(p1World, Block.enderChest.blockID, 5, 2, -6, 4, p3SBB);

        /**
         * ◆剣を突き立てる
         */
        int nX = this.getXWithOffset(3, 4);
        int nY = this.getYWithOffset(-4);
        int nZ = this.getZWithOffset(3, 4);

        EntityNNS entNNS = new EntityNNS(p1World);
        entNNS.setLocationAndAngles((double)nX + 0.5D, (double)nY, (double)nZ + 0.5D, 0.0F, 0.0F);
        if (!p1World.isRemote && entNNS.canStay())
        {
            p1World.spawnEntityInWorld(entNNS);
        }

        //■外見の見栄え的な
        for (int idz = 0; idz < 7; ++idz)
        {
            for (int idx = 0; idx < 10; ++idx)
            {
                //◆(X,Z)をfor文でまわして、Y軸方向に修正を加える

                //■建築物の上のブロックを除去
                this.clearCurrentPositionBlocksUpwards(p1World, idx, 6, idz, p3SBB);
                //■建築物の下が空間なら丸石を詰める
                if (2 <= idz && idz <= 6 && 1 <= idx && idx <= 6) {
                    this.fillCurrentPositionBlocksDownwards(p1World, Block.cobblestone.blockID, 0, idx, -8, idz, p3SBB);
                } else {
                    this.fillCurrentPositionBlocksDownwards(p1World, Block.cobblestone.blockID, 0, idx, -1, idz, p3SBB);
                }

            }
        }

        return true;
    }

    /**
     * Used to generate chests with items in it. ex: Temple Chests, Village Blacksmith Chests, Mineshaft Chests.
     */
    @Override
    protected boolean generateStructureChestContents(World par1World, StructureBoundingBox par2StructureBoundingBox, Random par3Random, int par4, int par5, int par6, WeightedRandomChestContent[] par7ArrayOfWeightedRandomChestContent, int par8)
    {
        int i1 = this.getXWithOffset(par4, par6);
        int j1 = this.getYWithOffset(par5);
        int k1 = this.getZWithOffset(par4, par6);

        if (par2StructureBoundingBox.isVecInside(i1, j1, k1) && par1World.getBlockId(i1, j1, k1) != Block.chest.blockID)
        {
            par1World.setBlock(i1, j1, k1, Block.chest.blockID, 0, 2);
            TileEntityChest chest = (TileEntityChest)par1World.getBlockTileEntity(i1, j1, k1);

            if (chest != null)
            {
                //WeightedRandomChestContent.generateChestContents(par3Random, par7ArrayOfWeightedRandomChestContent, tileentitychest, par8);
/*                {new WeightedRandomChestContent(Item.diamond.itemID, 0, 1, 3, 3),
                 new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 5, 10),
                 new WeightedRandomChestContent(Item.ingotGold.itemID, 0, 1, 3, 5),
                 new WeightedRandomChestContent(Item.bread.itemID, 0, 1, 3, 15),
                 new WeightedRandomChestContent(Item.appleRed.itemID, 0, 1, 3, 15),
                 new WeightedRandomChestContent(Item.pickaxeIron.itemID, 0, 1, 1, 5),
                 new WeightedRandomChestContent(Item.swordIron.itemID, 0, 1, 1, 5),
                 new WeightedRandomChestContent(Item.plateIron.itemID, 0, 1, 1, 5),
                 new WeightedRandomChestContent(Item.helmetIron.itemID, 0, 1, 1, 5),
                 new WeightedRandomChestContent(Item.legsIron.itemID, 0, 1, 1, 5),
                 new WeightedRandomChestContent(Item.bootsIron.itemID, 0, 1, 1, 5),
                 new WeightedRandomChestContent(Block.obsidian.blockID, 0, 3, 7, 5),
                 new WeightedRandomChestContent(Block.sapling.blockID, 0, 3, 7, 5)}
*/
                chest.setInventorySlotContents( 0, new ItemStack(Item.ingotIron, 4));
                chest.setInventorySlotContents( 7, new ItemStack(Item.ingotGold, 2));
                chest.setInventorySlotContents(14, new ItemStack(Item.ingotIron, 4));
                chest.setInventorySlotContents(21, new ItemStack(Block.obsidian, 2));
                chest.setInventorySlotContents(25, new ItemStack(Item.enderPearl, 1));
                //chest.setInventorySlotContents(25, new ItemStack(Item.expBottle, 4));

            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the
     * levels in the BB's horizontal rectangle).
     */
    protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox)
    {
        int i = 0;
        int j = 0;

        for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k)
        {
            for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l)
            {
                if (par2StructureBoundingBox.isVecInside(l, 64, k))
                {
                    i += Math.max(par1World.getTopSolidOrLiquidBlock(l, k), par1World.provider.getAverageGroundLevel());
                    ++j;
                }
            }
        }

        if (j == 0)
        {
            return -1;
        }
        else
        {
            return i / j;
        }
    }

    protected void setRandomBlock(World wld, Random rnd, StructureBoundingBox p3SBB,
                                  int x1, int y1, int z1, int x2, int y2, int z2, int id, int meta)
    {
        if (x1 > x2) { int x = x1; x1 = x2; x2 = x; }
        if (y1 > y2) { int y = y1; y1 = y2; y2 = y; }
        if (z1 > z2) { int z = z1; z1 = z2; z2 = z; }

        for (int idx = x1; idx <= x2; idx++)
        {
            for(int idy = y1; idy <= y2; idy++)
            {
                for(int idz = z1; idz <= z2; idz++)
                {
                    if (rnd.nextBoolean())
                    {
                        this.placeBlockAtCurrentPosition(wld, id , meta, idx, idy, idz, p3SBB);
                    }
                }
            }
        }
    }

    protected void fillWithBlocksMeta(World par1World, StructureBoundingBox par2StructureBoundingBox,
                                      int par3, int par4, int par5, int par6, int par7, int par8,
                                      int blockID1, int metaID1, int blockID2, int metaID2, boolean isCoercion)
    {
        for (int i2 = par4; i2 <= par7; ++i2)
        {
            for (int j2 = par3; j2 <= par6; ++j2)
            {
                for (int k2 = par5; k2 <= par8; ++k2)
                {
                    if (!isCoercion || this.getBlockIdAtCurrentPosition(par1World, j2, i2, k2, par2StructureBoundingBox) != 0)
                    {
                        if (i2 != par4 && i2 != par7 && j2 != par3 && j2 != par6 && k2 != par5 && k2 != par8)
                        {
                            this.placeBlockAtCurrentPosition(par1World, blockID2, metaID2, j2, i2, k2, par2StructureBoundingBox);
                            //this.placeBlockAtCurrentPositionCoercion(par1World, blockID2, metaID2, j2, i2, k2, par2StructureBoundingBox);
                        }
                        else
                        {
                            this.placeBlockAtCurrentPosition(par1World, blockID1, metaID1, j2, i2, k2, par2StructureBoundingBox);
                            //this.placeBlockAtCurrentPositionCoercion(par1World, blockID1, metaID1, j2, i2, k2, par2StructureBoundingBox);
                        }
                    }
                }
            }
        }
    }

    protected void fillCurrentPositionBlocksDownwardsCoercion(World wld, int id, int meta, int x, int y1, int z, int y2, StructureBoundingBox p7SBB)
    {
        int nX  = this.getXWithOffset(x, z);
        int nY1 = this.getYWithOffset(y1);
        int nY2 = this.getYWithOffset(y2);
        int nZ  = this.getZWithOffset(x, z);

        if (p7SBB.isVecInside(nX, nY1, nZ))
        {
            //while ((wld.isAirBlock(j1, k1, l1) || wld.getBlockMaterial(j1, k1, l1).isLiquid()) && k1 > 1)
            while (nY1 >= nY2)
            {
                wld.setBlock(nX, nY1, nZ, id, meta, 2);
                --nY1;
            }
        }
    }

    protected void fillWithBlocksMetaCoercion(World par1World, StructureBoundingBox par2StructureBoundingBox,
                                              int par3, int par4, int par5, int par6, int par7, int par8,
                                              int blockID1, int metaID1, int blockID2, int metaID2, boolean isCoercion)
    {
        for (int i2 = par4; i2 <= par7; ++i2)
        {
            for (int j2 = par3; j2 <= par6; ++j2)
            {
                for (int k2 = par5; k2 <= par8; ++k2)
                {
                    if (!isCoercion || this.getBlockIdAtCurrentPosition(par1World, j2, i2, k2, par2StructureBoundingBox) != 0)
                    {
                        if (i2 != par4 && i2 != par7 && j2 != par3 && j2 != par6 && k2 != par5 && k2 != par8)
                        {
                            //this.placeBlockAtCurrentPosition(par1World, blockID2, metaID2, j2, i2, k2, par2StructureBoundingBox);
                            this.placeBlockAtCurrentPositionCoercion(par1World, blockID2, metaID2, j2, i2, k2, par2StructureBoundingBox);
                        }
                        else
                        {
                            //this.placeBlockAtCurrentPosition(par1World, blockID1, metaID1, j2, i2, k2, par2StructureBoundingBox);
                            this.placeBlockAtCurrentPositionCoercion(par1World, blockID1, metaID1, j2, i2, k2, par2StructureBoundingBox);
                        }
                    }
                }
            }
        }
    }

    protected void placeBlockAtCurrentPositionCoercion(World par1World, int par2, int par3, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox)
    {
        int j1 = this.getXWithOffset(par4, par6);
        int k1 = this.getYWithOffset(par5);
        int l1 = this.getZWithOffset(par4, par6);

        //if (par7StructureBoundingBox.isVecInside(j1, k1, l1))
        //{
            par1World.setBlock(j1, k1, l1, par2, par3, 2);
        //}
    }
}
