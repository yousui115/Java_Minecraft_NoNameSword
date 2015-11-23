package mods.nnsmod.client;

import mods.nnsmod.EntityNNS;
import mods.nnsmod.NNSMod;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderNNS extends Render
{

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
    {
        if (entity instanceof EntityNNS)
        {
            doRenderNNS((EntityNNS)entity, d0, d1, d2, f, f1);
        }
    }

    protected void doRenderNNS(EntityNNS entNNS, double d0, double d1, double d2, float f, float f1)
    {
        Tessellator tesse = Tessellator.instance;
        //this.loadTexture(FishPrintMod.TEX_NAME_ITEM);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)d0, (float)d1, (float)d2);
        //GL11.glRotatef(45.0f, 0, 1, 0);
        //GL11.glScalef(0.5f, 0.5f, 0.5f);
        //GL11.glRotatef(var1.rotationYaw, 0, 1, 0);
        GL11.glTranslatef(0.0f, 0.0f, -0.8f);
        //GL11.glRotatef(15.0f, 1, 0, 0);
        GL11.glRotatef(90.0f, 0, 1, 0);
        GL11.glRotatef(120.0f, 0, 0, 1);
        //GL11.glTranslatef(-0.5f, -0.5f, 0.0f);


        //▼
        for (int idx = 0; idx < 2; idx++)
        {
            //int nSubID = itemstack.getItemDamage();
            //int nIconNo = ItemNewFood.arItemNewFood[nSubID].nIconIndex[idx];
            //String str = ItemNewFood.iconName[nIconNo];
            this.loadTexture("/mods/nnsmod/textures/items/NoNameSword_Beginning.png");

            //int nIconIndex = NNSMod.item_NNS.getIconIndex(itemstack, idx);
            Icon iconIndex = NNSMod.item_NNS.getIconFromDamageForRenderPass(0, 0);

            //int nColor = itemstack.getItem().getColorFromItemStack(itemstack, idx);
            //float fColorR = (float)(nColor >> 16 & 255) / 255.0F;
            //float fColorG = (float)(nColor >> 8 & 255) / 255.0F;
            //float fColorB = (float)(nColor & 255) / 255.0F;
            //float nX0 = (float)(nIconIndex % 16 * 16 + 0) / 256.0F;
            //float nX1 = (float)(nIconIndex % 16 * 16 + 16) / 256.0F;
            //float nY0 = (float)(nIconIndex / 16 * 16 + 0) / 256.0F;
            //float nY1 = (float)(nIconIndex / 16 * 16 + 16) / 256.0F;
            float nX0 = 0.0F;
            float nX1 = 1.0F;
            float nY0 = 0.0F;
            float nY1 = 1.0F;;
            float fVal0 = 0.0625F;
            //GL11.glColor4f(fColorR, fColorG, fColorB, 1.0f);
            ItemRenderer.renderItemIn2D(tesse, nX1, nY0, nX0, nY1, iconIndex.getSheetWidth(), iconIndex.getSheetHeight(), fVal0);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
