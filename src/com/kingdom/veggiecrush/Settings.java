package com.kingdom.veggiecrush;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	
	public static final String EXTRA_GAME_MODE = "GAME_MODE";
	public static enum GAME_MODE { TIME_ATTACK, BLITZ };
	
	public static final String EXTRA_PLAYER_NAME = "PLAYER_NAME";

	public static final String APP_OPTIONS_PREFS = "APP_OPTIONS_PREFS";
	public static final String OPTION_SOUND = "OPTION_SOUND";
	
	
	public static boolean isSoundOn(Context c)
	{
		SharedPreferences appOptions = c.getSharedPreferences(APP_OPTIONS_PREFS, 0);
	    boolean soundOn = appOptions.getBoolean(OPTION_SOUND, true);
	    return soundOn;
	}
	
	public static void setSoundOn(Context c, boolean state)
	{
		SharedPreferences appOptions = c.getSharedPreferences(APP_OPTIONS_PREFS, 0);
	    SharedPreferences.Editor editor = appOptions.edit();
	    editor.putBoolean(OPTION_SOUND, state);
	    editor.commit();
	}
	
}
