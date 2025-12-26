package net.skidcode.gh.maybeaclient.utils;

public class BlockPosHashSet
{

    public BlockPosHashSet()
    {
        threshold = 12;
        slots = new Element[16];
    }
	private static int hash(int a, int b) {
		return (a + b + 1) * (a + b) / 2 + b;
	}
	
    private static int computeHash(int x, int y, int z)
    {
        return hash(x, hash(y, z));
    }

    private static int getSlotIndex(int i, int j)
    {
        return i & j - 1;
    }

    public boolean contains(int x, int y, int z)
    {
        int j = computeHash(x, y, z);
        for(Element el = slots[getSlotIndex(j, slots.length)]; el != null; el = el.prev)
        {
            if(el.xPos == x && el.yPos == y && el.zPos == z) return true;
        }

        return false;
    }

    public boolean add(int x, int y, int z)
    {
    	int hash = computeHash(x, y, z);
        int slot = getSlotIndex(hash, slots.length);
        for(Element el = slots[slot]; el != null; el = el.prev)
        {
            if(el.xPos == x && el.yPos == y && el.zPos == z){
                return false;
            }
        }

        insert(hash, x, y, z, slot);
        return true;
    }

    private void grow(int i)
    {
    	Element amchashentry[] = slots;
        int j = amchashentry.length;
        if(j == 0x40000000)
        {
            threshold = 0x7fffffff;
            return;
        } else
        {
        	Element amchashentry1[] = new Element[i];
            copyTo(amchashentry1);
            slots = amchashentry1;
            threshold = (int)((float)i * 0.75F);
            return;
        }
    }

    private void copyTo(Element amchashentry[])
    {
    	Element amchashentry1[] = slots;
        int i = amchashentry.length;
        for(int j = 0; j < amchashentry1.length; j++)
        {
        	Element mchashentry = amchashentry1[j];
            if(mchashentry == null)
            {
                continue;
            }
            amchashentry1[j] = null;
            do
            {
            	Element mchashentry1 = mchashentry.prev;
                int k = getSlotIndex(mchashentry.hash, i);
                mchashentry.prev = amchashentry[k];
                amchashentry[k] = mchashentry;
                mchashentry = mchashentry1;
            } while(mchashentry != null);
        }

    }

    public boolean remove(int x, int y, int z)
    {
    	Element mchashentry = removeEntry(x, y, z);
        return mchashentry != null;
    }

    final Element removeEntry(int x, int y, int z)
    {
        int j = computeHash(x, y, z);
        int k = getSlotIndex(j, slots.length);
        Element slot = slots[k];
        Element el;
        Element prev;
        for(el = slot; el != null; el = prev)
        {
            prev = el.prev;
            if(el.xPos == x && el.yPos == y && el.zPos == z)
            {
                count--;
                if(slot == el)
                {
                    slots[k] = prev;
                } else
                {
                    slot.prev = prev;
                }
                return el;
            }
            slot = el;
        }

        return el;
    }

    public void clearMap()
    {
        Element amchashentry[] = slots;
        for(int i = 0; i < amchashentry.length; i++)
        {
            amchashentry[i] = null;
        }

        count = 0;
    }

    private void insert(int keyHash, int x, int y, int z, int slot)
    {
    	Element mchashentry = slots[slot];
        slots[slot] = new Element(x, y, z, keyHash, mchashentry);
        if(count++ >= threshold)
        {
            grow(2 * slots.length);
        }
    }

    public transient Element slots[];
    public transient int count;
    public int threshold;
    public final float growFactor = 0.75F;
    
    public class Element{
    	public final int xPos, yPos, zPos, hash;
    	public Element prev;
    	
    	public Element(int x, int y, int z, int hash, Element prev) {
    		this.xPos = x;
    		this.yPos = y;
    		this.zPos = z;
    		this.hash = hash;
    		this.prev = prev;
    	}
    	
    }
}
