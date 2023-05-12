package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution
{
    private final double scaledWidthD;
    private final double scaledHeightD;
    public static int scaledWidth;
    public static int scaledHeight;
    private int scaleFactor;

    public ScaledResolution(Minecraft p_i46445_1_)
    {
        this.scaledWidth = p_i46445_1_.displayWidth;
        this.scaledHeight = p_i46445_1_.displayHeight;
        this.scaleFactor = 1;
        boolean flag = p_i46445_1_.isUnicode();
        int i = p_i46445_1_.gameSettings.guiScale;

        if (i == 0)
        {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
        {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public ScaledResolution(Minecraft mcIn, int p_i46324_2_, int p_i46324_3_)
    {
        this.scaledWidth = p_i46324_2_;
        this.scaledHeight = p_i46324_3_;
        this.scaleFactor = 1;
        boolean var4 = mcIn.isUnicode();
        int var5 = mcIn.gameSettings.guiScale;

        if (var5 == 0)
        {
            var5 = 1000;
        }

        while (this.scaleFactor < var5 && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
        {
            ++this.scaleFactor;
        }

        if (var4 && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight()
    {
        return this.scaledHeight;
    }

    public double getScaledWidth_double()
    {
        return this.scaledWidthD;
    }

    public double getScaledHeight_double()
    {
        return this.scaledHeightD;
    }

    public int getScaleFactor()
    {
        return this.scaleFactor;
    }

    public int getScaledWidthStatic(final Minecraft minecraft) {
        if (minecraft.currentScreen != null) {
            return this.getScaledWidth();
        }
        switch (Minecraft.getMinecraft().gameSettings.guiScale) {
            case 0: {
                return this.getScaledWidth() * 2;
            }
            case 1: {
                return (int)(this.getScaledWidth() * 0.5);
            }
            case 3: {
                return (int)(this.getScaledWidth() * 1.5);
            }
            default: {
                return this.getScaledWidth();
            }
        }
    }

    public int getScaledHeightStatic(final Minecraft minecraft) {
        if (minecraft.currentScreen != null) {
            return this.getScaledHeight();
        }
        switch (Minecraft.getMinecraft().gameSettings.guiScale) {
            case 0: {
                return this.getScaledHeight() * 2;
            }
            case 1: {
                return (int)(this.getScaledHeight() * 0.5);
            }
            case 3: {
                return (int)(this.getScaledHeight() * 1.5);
            }
            default: {
                return this.getScaledHeight();
            }
        }
    }
}
