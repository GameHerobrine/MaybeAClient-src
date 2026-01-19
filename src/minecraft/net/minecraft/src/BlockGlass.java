package net.minecraft.src;

import java.util.Random;

import net.skidcode.gh.maybeaclient.hacks.XRayHack;

public class BlockGlass extends BlockBreakable {
    public BlockGlass(int var1, int var2, Material var3, boolean var4) {
        super(var1, var2, var3, var4);
    }

    public int quantityDropped(Random var1) {
        return 0;
    }

    public int getRenderBlockPass() {
    	if(XRayHack.INSTANCE.status && XRayHack.INSTANCE.mode.currentMode.equalsIgnoreCase("Opacity")) {
    		return XRayHack.INSTANCE.blockChooser.blocks[this.blockID] ? 0 : 1;
    	}
        return 0;
    }
}
