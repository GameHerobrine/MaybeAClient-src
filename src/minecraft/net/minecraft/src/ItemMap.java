package net.minecraft.src;

import net.skidcode.gh.maybeaclient.Client;

public class ItemMap extends ItemMapBase {
    public static int scale = 3;

	protected ItemMap(int var1) {
        super(var1);
        this.setMaxStackSize(1);
    }

    public static MapData func_28013_a(short var0, World var1) {
        (new StringBuilder()).append("map_").append(var0).toString();
        MapData var3 = (MapData)var1.loadItemData(MapData.class, "map_" + var0);
        if (var3 == null) {
            int var4 = var1.getUniqueDataId("map");
            String var2 = "map_" + var4;
            var3 = new MapData(var2);
            var1.setItemData(var2, var3);
        }

        return var3;
    }

    public MapData func_28012_a(ItemStack var1, World var2) {
        (new StringBuilder()).append("map_").append(var1.getItemDamage()).toString();
        MapData var4 = (MapData)var2.loadItemData(MapData.class, "map_" + var1.getItemDamage());
        if (var4 == null) {
            var1.setItemDamage(var2.getUniqueDataId("map"));
            String var3 = "map_" + var1.getItemDamage();
            var4 = new MapData(var3);
            var4.field_28180_b = var2.getWorldInfo().getSpawnX();
            var4.field_28179_c = var2.getWorldInfo().getSpawnZ();
            var4.field_28177_e = (byte) ItemMap.scale;
            var4.field_28178_d = (byte)var2.worldProvider.worldType;
            var4.markDirty();
            var2.setItemData(var3, var4);
        }
        var4.field_28177_e = (byte) ItemMap.scale;
        
        return var4;
    }

    public void func_28011_a(World world, Entity entity, MapData var3) {
        if (world.worldProvider.worldType == var3.field_28178_d) {
            short xSize = 128;
            short zSize = 128;
            int scale = 1 << var3.field_28177_e;
            int xCenter = var3.field_28180_b;
            int zCenter = var3.field_28179_c;
            int xStart = MathHelper.floor_double(entity.posX - (double)xCenter) / scale + xSize / 2;
            int zStart = MathHelper.floor_double(entity.posZ - (double)zCenter) / scale + zSize / 2;
            int var11 = 128 / scale;
            if (world.worldProvider.field_6478_e) {
                var11 /= 2;
            }

            ++var3.field_28175_g;
            for(int x = xStart - var11 + 1; x < xStart + var11; ++x) {
                if ((x & 15) == (var3.field_28175_g & 15)) {
                    int var13 = 255;
                    int var14 = 0;
                    double prevAvgHeightValue = 0.0D;

                    for(int z = zStart - var11 - 1; z < zStart + var11; ++z) {
                        if (x >= 0 && z >= -1 && x < xSize && z < zSize) {
                            int var18 = x - xStart;
                            int var19 = z - zStart;
                            boolean var20 = var18 * var18 + var19 * var19 > (var11 - 2) * (var11 - 2);
                            int blockX = (xCenter / scale + x - xSize / 2) * scale;
                            int blockZ = (zCenter / scale + z - zSize / 2) * scale;
                            byte var23 = 0;
                            byte var24 = 0;
                            byte var25 = 0;
                            int[] maxBlocksCnt = new int[256];
                            Chunk chunk = world.getChunkFromBlockCoords(blockX, blockZ);
                            int chunkCoordX = blockX & 15;
                            int chuunkCoordZ = blockZ & 15;
                            int var30 = 0;
                            double avgHeightValue = 0.0D;
                            int xx;
                            int zz;
                            int y;
                            int yy;
                            if (world.worldProvider.field_6478_e) {
                                xx = blockX + blockZ * 231871;
                                xx = xx * xx * 31287121 + xx * 11;
                                int var10001;
                                if ((xx >> 20 & 1) == 0) {
                                    var10001 = Block.dirt.blockID;
                                    maxBlocksCnt[var10001] += 10;
                                } else {
                                    var10001 = Block.stone.blockID;
                                    maxBlocksCnt[var10001] += 10;
                                }

                                avgHeightValue = 100.0D;
                            } else {
                                for(xx = 0; xx < scale; ++xx) {
                                    for(zz = 0; zz < scale; ++zz) {
                                    	//combine multiple blocks into one
                                        y = chunk.getHeightValue(xx + chunkCoordX, zz + chuunkCoordZ) + 1;
                                        int idbelow = 0;
                                        if (y > 1) {
                                            boolean hasblockbelow = false;

                                            find_highest_block:
                                            while(true) {
                                                hasblockbelow = true;
                                                idbelow = chunk.getBlockID(xx + chunkCoordX, y - 1, zz + chuunkCoordZ);
                                                if (idbelow == 0) {
                                                    hasblockbelow = false;
                                                } else if (y > 0 && idbelow > 0 && Block.blocksList[idbelow].blockMaterial.materialMapColor == MapColor.field_28212_b) {
                                                    hasblockbelow = false;
                                                }

                                                if (!hasblockbelow) {
                                                    --y;
                                                    idbelow = chunk.getBlockID(xx + chunkCoordX, y - 1, zz + chuunkCoordZ);
                                                }

                                                if (hasblockbelow) {
                                                    if (idbelow == 0 || !Block.blocksList[idbelow].blockMaterial.getIsLiquid()) {
                                                        break;
                                                    }

                                                    yy = y - 1;
                                                    //boolean var39 = false;

                                                    while(true) {
                                                        int idd = chunk.getBlockID(xx + chunkCoordX, yy--, zz + chuunkCoordZ);
                                                        ++var30;
                                                        if (yy <= 0 || idd == 0 || !Block.blocksList[idd].blockMaterial.getIsLiquid()) {
                                                            break find_highest_block;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        avgHeightValue += (double)y / (double)(scale * scale);
                                        int var10002 = maxBlocksCnt[idbelow]++;
                                    }
                                }
                            }

                            var30 /= scale * scale;
                            //int var10000 = var23 / (scale * scale);
                            //var10000 = var24 / (scale * scale);
                            //var10000 = var25 / (scale * scale);
                            xx = 0;
                            zz = 0;

                            for(y = 0; y < 256; ++y) {
                                if (maxBlocksCnt[y] > xx) {
                                    zz = y;
                                    xx = maxBlocksCnt[y];
                                }
                            }

                            double var41 = (avgHeightValue - prevAvgHeightValue) * 4.0D / (double)(scale + 4) + ((double)(x + z & 1) - 0.5D) * 0.4D;
                            
                            byte coltype = 1;
                            if (var41 > 0.6D) {
                                coltype = 2;
                            }

                            if (var41 < -0.6D) {
                                coltype = 0;
                            }

                            yy = 0;
                            if (zz > 0) {
                                MapColor var44 = Block.blocksList[zz].blockMaterial.materialMapColor;
                                if (var44 == MapColor.field_28200_n) {
                                    var41 = (double)var30 * 0.1D + (double)(x + z & 1) * 0.2D;
                                    coltype = 1;
                                    if (var41 < 0.5D) {
                                        coltype = 2;
                                    }

                                    if (var41 > 0.9D) {
                                        coltype = 0;
                                    }
                                }

                                yy = var44.colorIndex;
                            }

                            prevAvgHeightValue = avgHeightValue;
                            if (z >= 0 && var18 * var18 + var19 * var19 < var11 * var11 && (!var20 || (x + z & 1) != 0)) {
                                byte var45 = var3.field_28176_f[x + z * xSize];
                                byte var40 = (byte)(yy * 4 + coltype);
                                if (var45 != var40) {
                                    if (var13 > z) {
                                        var13 = z;
                                    }

                                    if (var14 < z) {
                                        var14 = z;
                                    }

                                    var3.field_28176_f[x + z * xSize] = var40;
                                }
                            }
                        }
                    }

                    if (var13 <= var14) {
                        var3.func_28170_a(x, var13, var14);
                    }
                }
            }

        }
    }

    public void onUpdate(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
        if (!var2.multiplayerWorld) {
            MapData var6 = this.func_28012_a(var1, var2);
            if (var3 instanceof EntityPlayer) {
                EntityPlayer var7 = (EntityPlayer)var3;
                var6.func_28169_a(var7, var1);
            }

            if (var5) {
                this.func_28011_a(var2, var3, var6);
            }

        }
    }

    public void onCreated(ItemStack var1, World var2, EntityPlayer var3) {
        var1.setItemDamage(var2.getUniqueDataId("map"));
        String var4 = "map_" + var1.getItemDamage();
        MapData var5 = new MapData(var4);
        var2.setItemData(var4, var5);
        var5.field_28180_b = MathHelper.floor_double(var3.posX);
        var5.field_28179_c = MathHelper.floor_double(var3.posZ);
        var5.field_28177_e = 3;
        var5.field_28178_d = (byte)var2.worldProvider.worldType;
        var5.markDirty();
    }
}
