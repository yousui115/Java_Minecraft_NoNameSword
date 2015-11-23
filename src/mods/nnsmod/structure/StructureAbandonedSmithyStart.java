package mods.nnsmod.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureAbandonedSmithyStart extends StructureStart
{
    public StructureAbandonedSmithyStart(World par1World, Random par2Random, int par3, int par4)
    {
        //ComponentScatteredFeatureSwampHut componentscatteredfeatureswamphut = new ComponentScatteredFeatureSwampHut(par2Random, par3 * 16, par4 * 16);
        //this.components.add(componentscatteredfeatureswamphut);

        ComponentAbandonedSmithy smithy = new ComponentAbandonedSmithy(par2Random, par3 * 16, par4 * 16);
        this.components.add(smithy);

        this.updateBoundingBox();
    }
}
