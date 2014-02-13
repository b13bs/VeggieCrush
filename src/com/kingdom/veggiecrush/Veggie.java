package com.kingdom.veggiecrush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class Veggie {
	
	public static enum VeggieKind { CAROT, TOMATO, ONION };
	
	VeggieKind kind;
	Bitmap bitmap;
	Rect bitmapRect;
	
	public Veggie (Context c, VeggieKind kind)
	{
		//TODO: charger le bon bitmap selon la sorte de légume
		bitmap = Settings.bitmapTomato;
		bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	}
}
