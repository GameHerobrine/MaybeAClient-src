package net.skidcode.gh.maybeaclient.events;

import java.lang.reflect.Field;

import net.skidcode.gh.maybeaclient.hacks.Hack;

public class EventRegistry {
	//public static HashMap<Integer, ArrayList<EventListener<? extends Event>>> listeners = new HashMap<Integer, ArrayList<EventListener<? extends Event>>>();
	//public static Int2ObjectOpenHashMap<ArrayList<EventListener<? extends Event>>> listeners = new Int2ObjectOpenHashMap<ArrayList<EventListener<? extends Event>>>();
	public static Listeners[] array = new Listeners[4];
	public static sun.misc.Unsafe SAFE;
	
	static {
		try {
			Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			SAFE = (sun.misc.Unsafe)f.get(null);
		}catch(Exception e) {
			throw new RuntimeException("Failed to get Unsafe", e);
		}
	}
	public static void registerListener(Class<? extends Event> class1, EventListener<? extends Event> listener) {
		Event e;
		try {
			e = (Event) SAFE.allocateInstance(class1);
		} catch (InstantiationException ee) {
			throw new RuntimeException(ee);
		}
		
		int id = e.getID();
		if(array.length <= id) {
			Listeners[] n = new Listeners[id+1];
			System.arraycopy(array, 0, n, 0, array.length);
			array = n;
		}
		if(array[id] == null) array[id] = new Listeners();
		array[id].add(listener);
		
		//if(listeners.lookup(e.getID()) == null) listeners.addKey(e.getID(), new ArrayList<EventListener<? extends Event>>());
		//((ArrayList<EventListener<? extends Event>>)listeners.lookup(e.getID())).add(listener);
	}
	
	public static void unregisterListener(Class<? extends Event> class1, EventListener<? extends Event> listener) {
		Event e;
		try {
			e = (Event) SAFE.allocateInstance(class1);
		} catch (InstantiationException ee) {
			throw new RuntimeException(ee);
		}
		
		int id = e.getID();
		if(array.length > id && array[id] != null) {
			array[id].remove(listener);
		}
		//if(listeners.lookup(e.getID()) != null) ((ArrayList<EventListener<? extends Event>>)listeners.lookup(e.getID())).remove(listener);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void handleEvent(Event event) {
		//ArrayList<EventListener<? extends Event>> arr = (ArrayList<EventListener<? extends Event>>) listeners.lookup(event.getID());
		Listeners arr;
		if(array.length > event.getID()) {
			 arr = array[event.getID()];
		}else {
			return;
		}
		EventListener el;
		int index = 0;
		if(arr != null) {
			do {
				el = arr.listeners[index++];
				if(!(el instanceof Hack) || ((Hack)el).status) {
					el.handleEvent(event);
				}
			}while(index < arr.length);
		}
	}
	
	
	public static class Listeners{
		public EventListener[] listeners;
		public int length;
		
		public Listeners() {
			listeners = new EventListener[1];
			length = 0;
		}
		
		public void add(EventListener listener) {
			int size = this.length + 1;
			if(size > listeners.length) {
				EventListener[] arr = new EventListener[this.length*2];
				System.arraycopy(this.listeners, 0, arr, 0, this.listeners.length);
				this.listeners = arr;
			}
			this.listeners[size-1] = listener;
			this.length = size;
		}
		public void remove(EventListener element) {
			for(int i = 0; i < this.length; ++i) {
				if(this.listeners[i] == element) {
					this.remove(i);
					return;
				}
			}
		}
		public void remove(int index) {
			//EventListener element = listeners[index];
			if(index < this.length-1) {
				for(int i = index + 1; i < this.length; ++i){
					this.listeners[i-1] = this.listeners[i];
				}
			}
			--this.length;
		}
	}
}
