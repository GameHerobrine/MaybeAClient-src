package net.minecraft.src;

public class Material {
    public static final Material air;
    public static final Material grassMaterial;
    public static final Material ground;
    public static final Material wood;
    public static final Material rock;
    public static final Material iron;
    public static final Material water;
    public static final Material lava;
    public static final Material leaves;
    public static final Material plants;
    public static final Material sponge;
    public static final Material cloth;
    public static final Material fire;
    public static final Material sand;
    public static final Material circuits;
    public static final Material glass;
    public static final Material tnt;
    public static final Material field_4262_q;
    public static final Material ice;
    public static final Material snow;
    public static final Material builtSnow;
    public static final Material cactus;
    public static final Material clay;
    public static final Material pumpkin;
    public static final Material portal;
    public static final Material cakeMaterial;
    private boolean canBurn;
    private boolean field_27285_A;
    private boolean field_28128_D;
    public final MapColor materialMapColor;

    public Material(MapColor var1) {
        this.materialMapColor = var1;
    }

    public boolean getIsLiquid() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean getCanBlockGrass() {
        return true;
    }

    public boolean getIsSolid() {
        return true;
    }

    private Material func_28127_i() {
        this.field_28128_D = true;
        return this;
    }

    private Material setBurning() {
        this.canBurn = true;
        return this;
    }

    public boolean getBurning() {
        return this.canBurn;
    }

    public Material func_27284_f() {
        this.field_27285_A = true;
        return this;
    }

    public boolean func_27283_g() {
        return this.field_27285_A;
    }

    public boolean func_28126_h() {
        return this.field_28128_D ? false : this.getIsSolid();
    }

    static {
        air = new MaterialTransparent(MapColor.field_28212_b);
        grassMaterial = new Material(MapColor.field_28211_c);
        ground = new Material(MapColor.field_28202_l);
        wood = (new Material(MapColor.field_28199_o)).setBurning();
        rock = new Material(MapColor.field_28201_m);
        iron = new Material(MapColor.field_28206_h);
        water = new MaterialLiquid(MapColor.field_28200_n);
        lava = new MaterialLiquid(MapColor.field_28208_f);
        leaves = (new Material(MapColor.field_28205_i)).setBurning().func_28127_i();
        plants = new MaterialLogic(MapColor.field_28205_i);
        sponge = new Material(MapColor.field_28209_e);
        cloth = (new Material(MapColor.field_28209_e)).setBurning();
        fire = new MaterialTransparent(MapColor.field_28212_b);
        sand = new Material(MapColor.field_28210_d);
        circuits = new MaterialLogic(MapColor.field_28212_b);
        glass = (new Material(MapColor.field_28212_b)).func_28127_i();
        tnt = (new Material(MapColor.field_28208_f)).setBurning().func_28127_i();
        field_4262_q = new Material(MapColor.field_28205_i);
        ice = (new Material(MapColor.field_28207_g)).func_28127_i();
        snow = (new MaterialLogic(MapColor.field_28204_j)).func_27284_f().func_28127_i();
        builtSnow = new Material(MapColor.field_28204_j);
        cactus = (new Material(MapColor.field_28205_i)).func_28127_i();
        clay = new Material(MapColor.field_28203_k);
        pumpkin = new Material(MapColor.field_28205_i);
        portal = new MaterialPortal(MapColor.field_28212_b);
        cakeMaterial = new Material(MapColor.field_28212_b);
    }
}
