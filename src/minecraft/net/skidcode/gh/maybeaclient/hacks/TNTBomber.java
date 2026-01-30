package net.skidcode.gh.maybeaclient.hacks;

import net.minecraft.src.*;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.utils.Direction;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;
import org.lwjgl.input.Keyboard;

public class TNTBomber extends Hack implements EventListener {
    public TNTBomber() {
        super("TNTBomber", "Auto places and ignites tnt", Keyboard.KEY_NONE, Category.MOVEMENT);
        EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
    }

    public int findSlotInHotbar(int itemId) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.mainInventory[i];
            if (item == null) continue;
            if (item.itemID == itemId) return i;
        }
        return -1;
    }

    //reliq skid haha
    public int getPossiblePlaceSide(int xx, int yy, int zz) {
        ItemStack item = mc.thePlayer.getCurrentEquippedItem();
        for(int i = 0; i < 6; ++i) {
            int x = xx;
            int y = yy;
            int z = zz;
            if(i == 0) y = yy + 1;
            if(i == 1) --y;
            if(i == 2) z = zz + 1;
            if(i == 3) --z;
            if(i == 4) x = xx + 1;
            if(i == 5) --x;
            int placeon = mc.theWorld.getBlockId(x, y, z);
            if(placeon != 0 &&
                    mc.theWorld.canBlockBePlacedAt(((ItemBlock)item.getItem()).blockID, xx, yy, zz, false, i))
                return i;
        }
        return 6;
    }

    public void placeBlock(int x, int y, int z) {
        int face = this.getPossiblePlaceSide(x, y, z);
        if(face != 6) {
            if(face == 0) ++y;
            if(face == 1) --y;
            if(face == 2) ++z;
            if(face == 3) --z;
            if(face == 4) ++x;
            if(face == 5) --x;
            PlayerUtils.placeBlockUnsafe(x, y, z, face);
        }

    }

    public int getHitSide(int x, int y, int z) {
        int offX = x;
        int offY = y;
        int offZ = z;
        if(offX > 0) {
            offX = 1;
        }

        if(offX < 0) {
            offX = -1;
        }

        if(offY > 0) {
            offY = 1;
        }

        if(offY < 0) {
            boolean offY1 = true;
        }

        if(offZ > 0) {
            offZ = 1;
        }

        if(offZ < 0) {
            offZ = -1;
        }

        return offX == 1 ? 4 : (offX == -1 ? 5 : (offZ == 1 ? 2 : (offZ == -1 ? 3 : 0)));
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof EventPlayerUpdatePre) {
            int tntSlot = -1;
            int flintNSteelSlot = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack item = mc.thePlayer.inventory.mainInventory[i];
                if (item == null) continue;
                if (item.itemID == 259) {
                    flintNSteelSlot = i;
                } else if (item.itemID == Block.tnt.blockID) {
                    tntSlot = i;
                }
            }
            if (tntSlot == -1 || flintNSteelSlot == -1) return;
            int origSlot = mc.thePlayer.inventory.currentItem;
            int xOffset = mc.thePlayer.posX == Math.abs(mc.thePlayer.posX) ? 0 : 1;
            int zOffset = mc.thePlayer.posZ == Math.abs(mc.thePlayer.posZ) ? 0 : 1;
            this.setSlot(tntSlot);
            Direction direction = PlayerUtils.getDirection();

            if (direction.z()) {
                // Place 3 straight lines in front (even columns at z offsets 0, 2, 4)
                for (int row = 0; row < 5; row++) {
                    for (int col = -2; col <= 2; col += 2) {
                        int placeX = (int) mc.thePlayer.posX + col - xOffset;
                        int placeY = (int) mc.thePlayer.posY - 2;
                        int placeZ = (int) mc.thePlayer.posZ + (direction == Direction.ZPOS ? row : -row) - zOffset;
                        this.placeBlock(placeX, placeY, placeZ);
                    }
                }
                // Ignite 3 blocks behind (even columns at z offset -1)
                for (int col = -2; col <= 2; col += 2) {
                    int igniteX = (int) mc.thePlayer.posX + col - xOffset;
                    int igniteY = (int) mc.thePlayer.posY - 2;
                    int igniteZ = (int) mc.thePlayer.posZ + (direction == Direction.ZPOS ? -1 : 1) - zOffset;
                    if (mc.theWorld.getBlockId(igniteX, igniteY, igniteZ) == Block.tnt.blockID) {
                        if (mc.thePlayer.inventory.currentItem != flintNSteelSlot) this.setSlot(flintNSteelSlot);
                        PlayerUtils.destroyBlockInstant(igniteX, igniteY, igniteZ, this.getHitSide(igniteX, igniteY, igniteZ));
                        if (mc.thePlayer.inventory.currentItem != tntSlot) this.setSlot(tntSlot);
                    }
                }
            } else {
                // Place 3 straight lines in front (even rows at x offsets 0, 2, 4)
                for (int row = 0; row < 5; row++) {
                    for (int col = -2; col <= 2; col += 2) {
                        int placeX = (int) mc.thePlayer.posX + (direction == Direction.XPOS ? row : -row) - xOffset;
                        int placeY = (int) mc.thePlayer.posY - 2;
                        int placeZ = (int) mc.thePlayer.posZ + col - zOffset;
                        this.placeBlock(placeX, placeY, placeZ);
                    }
                }
                // Ignite 3 blocks behind (even rows at x offset -1)
                for (int col = -2; col <= 2; col += 2) {
                    int igniteX = (int) mc.thePlayer.posX + (direction == Direction.XPOS ? -1 : 1) - xOffset;
                    int igniteY = (int) mc.thePlayer.posY - 2;
                    int igniteZ = (int) mc.thePlayer.posZ + col - zOffset;
                    if (mc.theWorld.getBlockId(igniteX, igniteY, igniteZ) == Block.tnt.blockID) {
                        if (mc.thePlayer.inventory.currentItem != flintNSteelSlot) this.setSlot(flintNSteelSlot);
                        PlayerUtils.destroyBlockInstant(igniteX, igniteY, igniteZ, this.getHitSide(igniteX, igniteY, igniteZ));
                        if (mc.thePlayer.inventory.currentItem != tntSlot) this.setSlot(tntSlot);
                    }
                }
            }

            this.setSlot(origSlot);
        }
    }

    private void setSlot(int slotId) {
        mc.thePlayer.inventory.currentItem = slotId;
        if (mc.isMultiplayerWorld()) ((PlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }
}
