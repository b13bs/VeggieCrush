package com.kingdom.veggiecrush;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Settings {
	
	public static final String EXTRA_GAME_MODE = "GAME_MODE";
	public static enum GameMode { TIME_ATTACK, BLITZ };
	
	public static final String EXTRA_PLAYER_NAME = "PLAYER_NAME";

	public static final String APP_OPTIONS_PREFS = "APP_OPTIONS_PREFS";
	public static final String OPTION_SOUND = "OPTION_SOUND";
	
	public static Bitmap bitmapBroccoli = null;
	public static Bitmap bitmapCarrot = null;
	public static Bitmap bitmapEggplant = null;
	public static Bitmap bitmapGreenPepper = null;
	public static Bitmap bitmapHotPepper = null;
	public static Bitmap bitmapPotato = null;
	public static Bitmap bitmapTomato = null;
	
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
	
	public static void loadBitmaps(Context c)
	{
		bitmapBroccoli = BitmapFactory.decodeResource(c.getResources(), R.drawable.broccoli);
		bitmapCarrot = BitmapFactory.decodeResource(c.getResources(), R.drawable.carrot);
		bitmapEggplant = BitmapFactory.decodeResource(c.getResources(), R.drawable.eggplant);
		bitmapGreenPepper = BitmapFactory.decodeResource(c.getResources(), R.drawable.green_pepper);
		bitmapHotPepper = BitmapFactory.decodeResource(c.getResources(), R.drawable.hot_pepper);
		bitmapPotato = BitmapFactory.decodeResource(c.getResources(), R.drawable.potato);
		bitmapTomato = BitmapFactory.decodeResource(c.getResources(), R.drawable.tomato);
	}
	
	
}
