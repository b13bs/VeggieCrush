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
	
	// Fonction qui initialise la grille de jeu avec des items plac�s al�atoirement
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
		
		while(this.verifyCombo(c)[0]>0)
		{
			android.util.Log.i("VeggieGrid", "again");
		}
		
	}
	
	// Fontion qui retourne la position dans la grille � partir d'une position en pixel
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
			//d�placement horizontal
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
			//d�placement vertical
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
		
		// D�placement termin�, on change les r�f�rences dans la grille et met les offset � 0
		veg1.offsetPos.x = 0;
		veg1.offsetPos.y = 0;
		veg2.offsetPos.x = 0;
		veg2.offsetPos.y = 0;
		veggies[i1.x][i1.y] = veg2;
		veggies[i2.x][i2.y] = veg1;
	}
	
	
	// Affiche tous les items de la grille sur un canvas recu en param�tre
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
	
	public int[] verifyCombo(Context c)
	{
		boolean detruitLegume = false;
		int nbLegumeDetruit = 0;
		int nbChaineDetruite = 0;
		// On parcours la grille avec les l�gumes
		for (int j = 0; j < nbColumns; ++j)
		{
			for (int i = 0; i < nbRows; ++i)
			{
				//V�rification � droite et borne
				if(i+2<=nbRows && veggies[i][j].destroy == false && veggies[i+1][j].destroy == false && veggies[i][j].kind == veggies[i+1][j].kind )
				{
					boolean detruit = verifyDestroy(i, j, Direction.RIGHT);
					detruitLegume |= detruit;
					nbChaineDetruite = detruit ? nbChaineDetruite+1 : nbChaineDetruite;
				}
				
				//V�rification en bas et borne
				if(j+2<=nbColumns && veggies[i][j].destroy == false && veggies[i][j+1].destroy == false  && veggies[i][j].kind == veggies[i][j+1].kind )
				{
					boolean detruit = verifyDestroy(i, j, Direction.DOWN);
					detruitLegume |= detruit;
					nbChaineDetruite = detruit ? nbChaineDetruite+1 : nbChaineDetruite;
				}
			}
		}
		if(detruitLegume)
			nbLegumeDetruit = updateGrid(c);
		return new int[]{nbLegumeDetruit, nbChaineDetruite};
	}
	
	public int updateGrid(Context c)
	{
		int nbLegumeDetruit = 0;
		// On parcours � l'envers pour trouver les null
		Random rand = new Random();
		for (int j = nbRows-1 ; j > 0; --j)
		{
			for (int i = nbColumns-1; i >= 0; --i) //On ne v�rifie pas la derni�re ligne en i pour remettre des legumes
			{
				if(veggies[i][j].destroy == true)
				{
					for(int m = j-1; m>=0; --m)
					{
						if(veggies[i][m].destroy == false)
						{
							veggies[i][j] = new Veggie(c, veggies[i][m].kind); 
							veggies[i][m].destroy = true;
							android.util.Log.i("update", "i: "+ i + " j: " + j + " Up Veggie ");
							break;
						}
					}
					if(veggies[i][j].destroy == true)
					{
						veggies[i][j] = new Veggie(c, VeggieKind.values()[rand.nextInt(VeggieKind.values().length)]);
						nbLegumeDetruit += 1;
						android.util.Log.i("update", "i: "+ i + " j: " + j + " New Veggie ");
						
					}
				}
			}
		}
		
		for(int i = 0; i < nbColumns; ++i)
		{
			if(veggies[i][0].destroy == true)
			{
				veggies[i][0] = new Veggie(c, VeggieKind.values()[rand.nextInt(VeggieKind.values().length)]);
				nbLegumeDetruit += 1;
				android.util.Log.i("update", "i: "+ i + " j: 0" + " New Veggie ");
			}
		}
		
		return nbLegumeDetruit;
	}
	
	private boolean verifyDestroy(int posx, int posy, Direction direction )
	{
		boolean detruitLegume = false;
		int detruire = 0;
		switch (direction) {
		case LEFT:
			for (int k = posx-2; k > 0; --k)
			{
				if(veggies[k][posy].kind == veggies[posx][posy].kind)
				{
					detruire +=1;
				}
				else 
				{
					break;
				}
			}
			if(detruire>=1)
			{
				for (int m = 0; m <= detruire+1; ++m)
				{
					veggies[posx-m][posy].destroy = true;
					detruitLegume = true;
				}
			}
			break;
		case RIGHT:
			for (int k = posx+2; k < nbRows; ++k)
			{
				if(veggies[k][posy].kind == veggies[posx][posy].kind)
				{
					detruire +=1;
				}
				else
				{
					break;
				}
			}
			if(detruire>=1)
			{
				for (int m = 0; m <= detruire+1; ++m)
				{
					veggies[posx+m][posy].destroy = true;
					detruitLegume = true;
				}
			}	
			break;
		case UP:
			for (int k = posy-2; k > 0; --k)
			{
				if(veggies[posx][k].kind == veggies[posx][posy].kind)
				{
					detruire +=1;
				}
				else
				{
					break;
				}
			}
			if(detruire>=1)
			{
				for (int m = 0; m <= detruire+1; ++m)
				{
					veggies[posx][posy-m].destroy = true;
					detruitLegume = true;
				}
			}
			break;
		case DOWN:
			for (int k = posy+2; k < nbColumns; ++k)
			{
				if(veggies[posx][k].kind == veggies[posx][posy].kind)
				{
					detruire +=1;
				}
				else
				{
					break;
				}
			}
			if(detruire>=1)
			{
				for (int m = 0; m <= detruire+1; ++m)
				{
					veggies[posx][posy+m].destroy = true;
					detruitLegume = true;
				}
			}
			break;

		default:
			break;
		}
		return detruitLegume;
	}
}
