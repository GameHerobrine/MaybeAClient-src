package net.skidcode.gh.maybeaclient.utils;

import net.skidcode.gh.maybeaclient.gui.click.Tab;

public interface InputHandler {
	public void onKeyPress(int keycode);
	public void onKeyRelease(int keycode);
	
	public void onInputFocusStop();
}
