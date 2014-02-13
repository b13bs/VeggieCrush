package com.kingdom.veggiecrush;

import android.graphics.Bitmap;

public class Veggie {
	
	public static enum VeggieKind { CAROT, TOMATO, ONION };
	
	VeggieKind kind;
	Bitmap bitmap;
	
	public Veggie (VeggieKind kind)
	{
		//TODO: charger le bon bitmap selon la sorte de légume
	}
}
