package nettion.features.module.modules.render;

import nettion.utils.render.ColorUtils;
import nettion.features.value.values.Numbers;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class XRay extends Module {
    private static final Numbers<Double> range = new Numbers<>("Range", 40.0, 8.0, 500.0, 1.0);
    public static int alpha;
    public static List<BlockPos> blockPosList = new ArrayList<>();

    public XRay() {
        super("XRay", ModuleType.Render);
    }

    @Override
    public void onEnable() {
        int n_ = 4;
        for (int i = -n_; i <= n_; ++i) {
            for (int j = -n_; j <= n_; ++j) {
                for (int k = -n_; k <= n_; ++k) {
                    EntityPlayerSP entityPlayerSP = mc.thePlayer;
                    int n2 = (int)entityPlayerSP.posX + i;
                    int n3_ = (int)entityPlayerSP.posY + j;
                    int n4_ = (int)entityPlayerSP.posZ + k;
                    BlockPos blockPos3 = new BlockPos(n2, n3_, n4_);
                    if (blockPosList.contains(blockPos3)) continue;
                    blockPosList.add(blockPos3);
                }
            }
        }
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        for (BlockPos blockPos : blockPosList) {
            if (!(this.getDistance(blockPos.getX(), blockPos.getZ()) <= (Double)range.getValue())) continue;
            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
            if (block == Blocks.diamond_ore) {
                this.render3D(blockPos, 0, 255, 255);
                continue;
            }
            if (block == Blocks.iron_ore) {
                this.render3D(blockPos, 225, 225, 225);
                continue;
            }
            if (block == Blocks.lapis_ore) {
                this.render3D(blockPos, 0, 0, 255);
                continue;
            }
            if (block == Blocks.redstone_ore) {
                this.render3D(blockPos, 255, 0, 0);
                continue;
            }
            if (block == Blocks.coal_ore) {
                this.render3D(blockPos, 0, 30, 30);
                continue;
            }
            if (block == Blocks.emerald_ore) {
                this.render3D(blockPos, 0, 255, 0);
                continue;
            }
            if (block != Blocks.gold_ore) {
                continue;
            }
            this.render3D(blockPos, 255, 255, 0);
        }
    }

    private void render3D(BlockPos blockPos, int n, int n2, int n3) {
        RenderUtils.drawSolidBlockESP(blockPos, ColorUtils.getColor(n, n2, n3));
    }

    public static int getDistance() {
        return range.getValue().intValue();
    }

    public double getDistance(double d, double d2) {
        double d3 = mc.thePlayer.posX - d;
        double d4 = mc.thePlayer.posZ - d2;
        return MathHelper.sqrt_double(d3 * d3 + d4 * d4);
    }
}
