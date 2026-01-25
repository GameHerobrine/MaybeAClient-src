package net.skidcode.gh.maybeaclient.hacks;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntitySheep;
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
                        mc.playerController.attackEntity(mc.thePlayer, sheep);
                        if (sheep.deathTime > 0) mc.playerController.interactWithEntity(mc.thePlayer, sheep);
                        continue;
                    }
                    mc.playerController.interactWithEntity(mc.thePlayer, sheep);
                }
            }
        }
    }
}
