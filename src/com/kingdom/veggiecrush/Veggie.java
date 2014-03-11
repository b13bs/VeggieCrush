package com.kingdom.veggiecrush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public class Veggie {
	
	public static enum VeggieKind { BROCOLI, CARROT, EGGPLANT, GREEN_PEPPER, HOT_PEPPER, POTATO, TOMATO };
	
	VeggieKind kind;
	Bitmap bitmap;
	Rect bitmapRect;
	Point offsetPos;
	boolean isDestroyed;
	
	// Un veggie représente un élément de la grille
	// Il comporte un type, une image et un décalage d'affichage
	public Veggie (Context c, VeggieKind kind)
	{
		this.kind = kind;
		this.isDestroyed = false;
		
		// Assigner le bon bitmap en fonction deu type de légume recu
		switch (kind)
		{
		case BROCOLI:
			bitmap = Settings.bitmapBroccoli;
			break;
		case CARROT:
			bitmap = Settings.bitmapCarrot;
			break;
		case EGGPLANT:
			bitmap = Settings.bitmapEggplant;
			break;
		case GREEN_PEPPER:
			bitmap = Settings.bitmapGreenPepper;
			break;
		case HOT_PEPPER:
			bitmap = Settings.bitmapHotPepper;
			break;
		case POTATO:
			bitmap = Settings.bitmapPotato;
			break;
		case TOMATO:
			bitmap = Settings.bitmapTomato;
			break;
		}

		// Le rectangle qui sera utilisé pour dessiner
		bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		
		offsetPos = new Point(0, 0);
	}
}
