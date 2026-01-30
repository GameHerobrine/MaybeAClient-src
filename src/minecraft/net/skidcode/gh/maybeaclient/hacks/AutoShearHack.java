package net.skidcode.gh.maybeaclient.hacks;

import net.minecraft.src.*;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class AutoShearHack extends Hack implements EventListener {
    public SettingBoolean killShip = new SettingBoolean(this, "ShearAura", false);
    public SettingDouble radius = new SettingDouble(this, "Radius", 6.0f, 0, 10);

    public AutoShearHack() {
        super("AutoShear", "Automatically shears all sheep around you", Keyboard.KEY_NONE, Category.MISC);
        this.addSetting(killShip);
        this.addSetting(radius);
        EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
    }

    @Override
    public void handleEvent(Event event) {
        if(event instanceof EventPlayerUpdatePost) {
            int shearsSlot = this.getItemSlot(Item.shears);
            if (shearsSlot == -1) return;
            int prev = mc.thePlayer.inventory.currentItem;
            int swordSlot = this.getItemSlot(Item.swordWood, Item.swordGold, Item.swordDiamond, Item.swordSteel, Item.swordStone);
            double rad = this.radius.getValue();
            List<Entity> entitiesNearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                    mc.thePlayer,
                    AxisAlignedBB.getBoundingBox(
                            mc.thePlayer.posX - rad, mc.thePlayer.posY - rad, mc.thePlayer.posZ - rad,
                            mc.thePlayer.posX + rad, mc.thePlayer.posY + rad, mc.thePlayer.posZ + rad
                    )
            );
            for (Entity entity : entitiesNearby) {
                if (entity instanceof EntitySheep) {
                    EntitySheep sheep = (EntitySheep) entity;
                    if (sheep.getSheared()) continue;
                    if (this.killShip.getValue() && sheep.deathTime == 0) {
                        if (swordSlot != -1) this.checkSlot(swordSlot);
                        mc.playerController.attackEntity(mc.thePlayer, sheep);
                        if (sheep.deathTime > 0) this.sheerSheep(shearsSlot, sheep);
                        continue;
                    }
                    this.sheerSheep(shearsSlot, sheep);
                }
            }
            this.checkSlot(prev);
        }
    }

    public void checkSlot(int slot) {
        if (mc.thePlayer.inventory.currentItem != slot) {
            mc.thePlayer.inventory.currentItem = slot;
            if (mc.isMultiplayerWorld()) ((PlayerControllerMP) mc.playerController).syncCurrentPlayItem();
        }
    }

    public void sheerSheep(int slot, EntitySheep sheep) {
        this.checkSlot(slot);
        mc.playerController.interactWithEntity(mc.thePlayer, sheep);
    }

    public int getItemSlot(Item... items) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack == null) continue;
            for (Item item : items) {
                if (mc.thePlayer.inventory.mainInventory[i].getItem() == item) return i;
            }
        }
        return -1;
    }
}
