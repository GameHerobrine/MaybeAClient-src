package lunatrius.schematica;

public class BlockStat {
	public final int id;
	public final int meta;
	public int toPlace = 0;
	public int donePlace = 0;
	public int toRemove = 0;
	public int metaInvalid = 0;
	
	public BlockStat(int id, int meta) {
		this.id = id;
		this.meta = meta;
	}
	
	public void empty() {
		this.toPlace = this.toRemove = this.metaInvalid = this.donePlace = 0;
	}
	
	public boolean isEmpty() {
		return this.toPlace == 0 && this.metaInvalid == 0 && this.toRemove == 0 && this.donePlace == 0;
	}
	
	public static final byte PLACE = 1;
	public static final byte DONEPLACE = 2;
	public static final byte REMOVE = 3;
	public static final byte METAINVALID = 4;
}
