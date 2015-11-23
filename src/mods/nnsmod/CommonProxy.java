package mods.nnsmod;

public class CommonProxy
{
    public void registerRenderers()
    {
        /*
         サーバー側では何もしない
         クライアント側でのみ必要な処理はこのように空のメソッドを用意し,
         CommonProxyを継承したClientProxyで行う
        */
    }

    public void registerTextures()
    {
    }
}
