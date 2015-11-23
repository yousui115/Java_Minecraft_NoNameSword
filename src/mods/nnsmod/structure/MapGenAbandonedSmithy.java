package mods.nnsmod.structure;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureScatteredFeatureStart;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenAbandonedSmithy extends MapGenScatteredFeature
{
    protected boolean canSpawn    = false;
    //protected boolean canSpawnDef = false;
    /**
     * ■コンストラクタ
     */
    public MapGenAbandonedSmithy()
    {
        super();
    }

    /**
     * ■建造物をスポーンさせられるか否か
     */
    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        //■スポーン条件
        BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(par1 * 16 + 8, par2 * 16 + 8);

        //TODO
        //if (biomegenbase == BiomeGenBase.taiga)

        int nNowX = par1 * 16 + 8;
        int nNowZ = par2 * 16 + 8;
        int nSpawnX = this.worldObj.getWorldInfo().getSpawnX();
        int nSpawnZ = this.worldObj.getWorldInfo().getSpawnZ();

        int nSpawnY = this.worldObj.getWorldInfo().getSpawnY();

        if (nSpawnY != 0 &&
            nNowX + 8 >= nSpawnX && nSpawnX >= nNowX - 7 &&
            nNowZ + 8 >= nSpawnZ && nSpawnZ >= nNowZ - 7)// &&
            //biomegenbase == BiomeGenBase.taiga)
        {
            canSpawn = true;
        }
        else
        {
            canSpawn = false;
        }

        return canSpawn ? true : super.canSpawnStructureAtCoords(par1, par2);
    }

    /**
     * ■建造物の構成要素を作成
     */
    protected StructureStart getStructureStart(int par1, int par2)
    {
        if (canSpawn)
        {
            return new StructureAbandonedSmithyStart(this.worldObj, this.rand, par1, par2);
        }

        return new StructureScatteredFeatureStart(this.worldObj, this.rand, par1, par2);
    }
}
