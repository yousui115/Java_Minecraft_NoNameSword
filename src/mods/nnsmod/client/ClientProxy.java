package mods.nnsmod.client;

import mods.nnsmod.CommonProxy;
import mods.nnsmod.EntityNNS;
import mods.nnsmod.EntityNNSMagic;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// レンダーに関するレジストリ

// クライアント側のみのクラスはこのアノテーションをつける
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public void registerRenderers()
    {
        // ModLoader.addRendererでのmap.putに相当
        // Entityのクラスと描画, モデルを結びつける

        //RenderingRegistry.registerEntityRenderingHandler(EntityDecayMonster.class, new RenderZombie());
        RenderingRegistry.registerEntityRenderingHandler(EntityNNSMagic.class, new RenderNNSMagic());
        RenderingRegistry.registerEntityRenderingHandler(EntityNNS.class, new RenderNNS());

    }

    public void registerTextures()
    {
    }
}