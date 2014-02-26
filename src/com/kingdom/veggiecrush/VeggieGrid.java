package com.kingdom.veggiecrush;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.kingdom.veggiecrush.Veggie.VeggieKind;

public class VeggieGrid {

	public static enum Direction { LEFT, RIGHT, UP, DOWN };
	
	private static final int MOVING_STEPS = 8;

	public Veggie[][] veggies;
	public int nbRows;
	public int nbColumns;
	private int gridWidth;
	private int gridHeight;
	
	
	public VeggieGrid(int nbColumns, int nbRows, int gridWidth, int gridHeight)
	{
		this.nbColumns = nbColumns;
		this.nbRows = nbRows;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		veggies = new Veggie[nbColumns][nbRows];
	}
	
	// Fonction qui initialise la grille de jeu avec des items placés aléatoirement
	public void resetVeggieGrid(Context c)
	{
		Random rand = new Random();
		for (int j = 0; j < nbRows; ++j)
		{
			for (int i = 0; i < nbColumns; ++i)
			{
				veggies[i][j] = new Veggie(c, VeggieKind.values()[rand.nextInt(VeggieKind.values().length)]);
			}
		}
	}
	
	// Fontion qui retourne la position dans la grille à partir d'une position en pixel
	public Point getIndexFromPos(int x, int y)
	{
		int i = (int)((x / (float)gridWidth) * nbColumns);
		int j = (int)((y / (float)gridHeight) * nbRows);
		return new Point(i, j);
	}
	
	// Fonction qui retourne la position en pixel dans la vue en fonction de la position dans la grille
	public Rect getRectFromIndex(int i, int j)
	{
		return new Rect(getLargeurCase() * i, getHauteurCase() * j, getLargeurCase() * (i+1), getHauteurCase() * (j+1));
	}
	
	// Retoune la largeur d'une case de la grille
	public int getLargeurCase()
	{
		return gridWidth / nbColumns;
	}
	
	// Retoune la hauteur d'une case de la grille
	public int getHauteurCase()
	{
		return gridHeight / nbRows;
	}
	
	
	// Fonction qui recoit les positions de 2 items dans le grille et les inverse graduellement avec une animation
	public void switchPlace(Point i1, Point i2)
	{
		Veggie veg1 = veggies[i1.x][i1.y];
		Veggie veg2 = veggies[i2.x][i2.y];
		
		if (i1.y == i2.y)
		{
			//déplacement horizontal
			boolean moveVeg1Right = i1.x < i2.x;
			int movingDistance = getLargeurCase();
			int stepSize = (int)(movingDistance / (float)MOVING_STEPS);
			while ( Math.abs(veg1.offsetPos.x) < movingDistance )
			{
				if (moveVeg1Right)
				{
					veg1.offsetPos.x += stepSize;
					veg2.offsetPos.x -= stepSize;
				}
				else
				{
					veg1.offsetPos.x -= stepSize;
					veg2.offsetPos.x += stepSize;
				}
				try { Thread.sleep(GameView.REFRESH_RATE_MS); } catch (InterruptedException e) { e.printStackTrace();	}
			}
		}
		else if (i1.x == i2.x)
		{
			//déplacement vertical
			boolean moveVeg1Down = i1.y < i2.y;
			int movingDistance = getHauteurCase();
			int stepSize = (int)(movingDistance / (float)MOVING_STEPS);
			while ( Math.abs(veg1.offsetPos.y) < movingDistance )
			{
				if (moveVeg1Down)
				{
					veg1.offsetPos.y += stepSize;
					veg2.offsetPos.y -= stepSize;
				}
				else
				{
					veg1.offsetPos.y -= stepSize;
					veg2.offsetPos.y += stepSize;
				}
				try { Thread.sleep(GameView.REFRESH_RATE_MS); } catch (InterruptedException e) { e.printStackTrace();	}
			}
		}
		
		// Déplacement terminé, on change les références dans la grille et met les offset à 0
		veg1.offsetPos.x = 0;
		veg1.offsetPos.y = 0;
		veg2.offsetPos.x = 0;
		veg2.offsetPos.y = 0;
		veggies[i1.x][i1.y] = veg2;
		veggies[i2.x][i2.y] = veg1;
	}
	
	
	// Affiche tous les items de la grille sur un canvas recu en paramètre
	public void drawVeggiesToCanvas(Canvas c, Paint p)
	{
		if (c != null && veggies != null)
		{
			Rect drawRectangle;
			for (int j = 0; j < nbRows; ++j)
			{
				for (int i = 0; i < nbColumns; ++i)
				{
					if (veggies[i][j] != null)
					{
						drawRectangle = getRectFromIndex(i, j);
						drawRectangle.offset(veggies[i][j].offsetPos.x, veggies[i][j].offsetPos.y);
						c.drawBitmap(veggies[i][j].bitmap, veggies[i][j].bitmapRect, drawRectangle, p);
					}
				}
			}
		}
	}
	
	
}
