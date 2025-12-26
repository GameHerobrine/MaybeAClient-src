package net.skidcode.gh.maybeaclient.hacks.settings;

import java.util.HashMap;

public class SettingEnum<T extends Enum<T>> extends SettingMode{
	public HashMap<String, T> map;
	
	public SettingEnum(SettingsProvider hack, String name, T def) {
		super(hack, name, getValues(def));
	}
	
	public void init() {
		map = new HashMap<String, T>();
		Enum<?>[] ordinals = def.getClass().getEnumConstants();
		def = null;
		
		for(int i = 0; i < ordinals.length; ++i) {
			map.put(ordinals[i].toString().toLowerCase(), (T) ordinals[i]);
		}
	}
	
	public T getValue() {
		return map.get(this.currentMode.toLowerCase());
	}
	
	static Enum<?> def;
	public static String[] getValues(Enum<?> e) {
		def = e;
		Enum<?> cnsts[] = e.getClass().getEnumConstants();
		String[] names = new String[cnsts.length];
		names[0] = e.toString();
		
		int j = 1;
		for(int i = 0; i < names.length; ++i) {
			if(cnsts[i] != e) {
				names[j] = cnsts[i].toString();
				++j;
			}
		}
		
		return names;
	}
}
