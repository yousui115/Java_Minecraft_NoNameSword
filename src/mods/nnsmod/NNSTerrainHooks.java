package mods.nnsmod;

import mods.nnsmod.structure.MapGenAbandonedSmithy;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;

public class NNSTerrainHooks
{
   @ForgeSubscribe
   public void moddedMapGen(InitMapGenEvent event)
    {
       //★InitMapGenEvent.EventType が渡されている
       EventType  type        = event.type;

       //★ChunkProviderGenerate の色々なMapGen が渡されている
       // ●final宣言の為、触れない
       MapGenBase originalGen = event.originalGen;  // public final MapGenBase
       // ●なので、こちらに新規のMapGenを追加する。
       MapGenBase newGen      = event.newGen;       // public MapGenBase

       //■散在する造形物が作りたい
       if (type != EventType.SCATTERED_FEATURE) { return; }

       //■自作MapGenを渡す
       event.newGen = new MapGenAbandonedSmithy();
       //event.setResult(Event.Result.ALLOW);

    }
}
