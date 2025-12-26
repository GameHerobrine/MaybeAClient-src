package net.minecraft.src;

import java.util.Comparator;

public class EntitySorter implements Comparator {
    private Entity entityForSorting;

    public EntitySorter(Entity var1) {
        this.entityForSorting = var1;
    }

    public int sortByDistanceToEntity(WorldRenderer var1, WorldRenderer var2) {
    	double d1 = var1.distanceToEntitySquared(this.entityForSorting);
    	double d2 = var2.distanceToEntitySquared(this.entityForSorting);
    	if(d1 == d2) return 0;
        return d1 < d2 ? -1 : 1;
    }

    // $FF: synthetic method
    // $FF: bridge method
    public int compare(Object var1, Object var2) {
        return this.sortByDistanceToEntity((WorldRenderer)var1, (WorldRenderer)var2);
    }
}
