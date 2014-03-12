package com.kingdom.veggiecrush;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;

import com.kingdom.veggiecrush.Veggie.VeggieKind;

public class VeggieGrid {

	public static enum Direction { LEFT, RIGHT, UP, DOWN, NONE };
	
	private static final int MOVING_STEPS = 8;

	public Veggie[][] veggies;
	public int nbRows;
	public int nbColumns;
	private int gridWidth;
	private int gridHeight;
	
	private Context context;
	private SoundPool soundPool;
	private int crushSoundId;
	
	public VeggieGrid(Context c, int nbColumns, int nbRows, int gridWidth, int gridHeight)
	{
		this.context = c;
		this.nbColumns = nbColumns;
		this.nbRows = nbRows;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		veggies = new Veggie[nbColumns][nbRows];
		
		// On créé un media player pour les effets sonores des légumes
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		crushSoundId = soundPool.load(context, R.raw.sound_crunch, 1);
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
		
		while(this.verifyCombo(c, false)[0]>0)
		{
			android.util.Log.i("VeggieGrid", "init combo");
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
	
	
	// Fonction qui recoit les positions de 2 items dans la grille et les inverse graduellement avec une animation
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
				veg1.offsetPos.x += (moveVeg1Right ? stepSize : -stepSize);
				veg2.offsetPos.x -= (moveVeg1Right ? stepSize : -stepSize);
				
				try { Thread.sleep(GameView.REFRESH_RATE_MS); } catch (InterruptedException e) { e.printStackTrace();	}
			}
		}
		else if (i1.x == i2.x)
		{
			//déplacement vertical
			boolean moveVeg1Up = i1.y > i2.y;
			int movingDistance = getHauteurCase();
			int stepSize = (int)(movingDistance / (float)MOVING_STEPS);
			while ( Math.abs(veg1.offsetPos.y) < movingDistance )
			{
				veg1.offsetPos.y += (moveVeg1Up ? -stepSize : stepSize);
				veg2.offsetPos.y -= (moveVeg1Up ? -stepSize : stepSize);
				
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
	
	
	// Fonction qui recoit une rangée et inverse tous les légumes 'destroyed' avec leur voisin du haut graduellement avec une animation
	private void switchDestroyedUp(int row, boolean animate)
	{
		// En premier on s'assure qu'il existe des veggies détruits
		boolean workToDo = true;
		for (int i = 0; i < nbColumns; ++i)
		{
			if (veggies[i][row].isDestroyed && !veggies[i][row-1].isDestroyed)
			{
				workToDo = true;
				break;
			}
		}
		
		if (workToDo)
		{
			// On les switch!
			int row2 = row-1;
			if (animate)
			{
				int movingDistance = getHauteurCase();
				int stepSize = (int)(movingDistance / (float)MOVING_STEPS);
				int offset = 0;
				while ( offset < movingDistance )
				{
					for (int i = 0; i < nbColumns; ++i)
					{
						if (veggies[i][row].isDestroyed)
						{
							veggies[i][row].offsetPos.y -= stepSize;
							veggies[i][row2].offsetPos.y += stepSize;
						}
					}
					offset += stepSize;
					try { Thread.sleep(GameView.REFRESH_RATE_MS / 2); } catch (InterruptedException e) { e.printStackTrace();	}
				}
			}
			
			// Déplacement terminé, on change les références dans la grille et met les offset à 0
			for (int i = 0; i < nbColumns; ++i)
			{
				if (veggies[i][row].isDestroyed)
				{
					veggies[i][row].offsetPos.y = 0;
					veggies[i][row2].offsetPos.y = 0;
					Veggie tmp = veggies[i][row];
					veggies[i][row] = veggies[i][row2];
					veggies[i][row2] = tmp;
				}
			}
		}
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
					if (veggies[i][j] != null && !veggies[i][j].isDestroyed)
					{
						drawRectangle = getRectFromIndex(i, j);
						drawRectangle.offset(veggies[i][j].offsetPos.x, veggies[i][j].offsetPos.y);
						c.drawBitmap(veggies[i][j].bitmap, veggies[i][j].bitmapRect, drawRectangle, p);
					}
				}
			}
		}
	}
	
	
	public int[] verifyCombo (Context c, boolean animate)
	{
		boolean detruitLegume = false;
		int nbLegumeDetruit = 0;
		int nbChaineDetruite = 0;
		
		// On parcours la grille avec les légumes 
		for (int j = 0; j < nbRows; ++j)
		{
			for (int i = 0; i < nbColumns; ++i)
			{
				//Vérification à droite et borne pour des chaînes
				if(i+2<=nbRows && veggies[i][j].isDestroyed == false && veggies[i+1][j].isDestroyed == false && veggies[i][j].kind == veggies[i+1][j].kind )
				{
					boolean detruit = verifyDestroy(i, j, Direction.RIGHT);
					detruitLegume |= detruit;
					nbChaineDetruite = detruit ? nbChaineDetruite+1 : nbChaineDetruite;
				}
				
				//Vérification en bas et borne pour des chaînes
				if(j+2<=nbColumns && veggies[i][j].isDestroyed == false && veggies[i][j+1].isDestroyed == false  && veggies[i][j].kind == veggies[i][j+1].kind )
				{
					boolean detruit = verifyDestroy(i, j, Direction.DOWN);
					detruitLegume |= detruit;
					nbChaineDetruite = detruit ? nbChaineDetruite+1 : nbChaineDetruite;
				}
			}
		}
		
		if (detruitLegume)
		{
			if (animate && Settings.isSoundOn(context))
			{
				soundPool.play(crushSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
			}
			nbLegumeDetruit = updateGrid(c, animate);
		}
		
		return new int[]{nbLegumeDetruit, nbChaineDetruite};
	}
	
	
	private int updateGrid(Context c, boolean animate)
	{
		int nbLegumeDetruit = 0;
		Random rand = new Random();
		
		while (existsDestroyedInGrid())
		{
			// On parcours à partir du bas et swap jusqu'à avoir les légumes détruits au top
			for (int j = nbRows-1; j > 0; --j) //On ne vérifie pas la ligne 0 en j pour mettre de nouveaux legumes
			{
				switchDestroyedUp(j, animate);
			}
			
			// Maintenant on met des nouveaux légumes sur la première rangée
			for (int i = 0; i < nbColumns; ++i)
			{
				if (veggies[i][0].isDestroyed == true)
				{
					veggies[i][0] = new Veggie(c, VeggieKind.values()[rand.nextInt(VeggieKind.values().length)]);
					nbLegumeDetruit += 1;
				}
			}
		}
		
		return nbLegumeDetruit;
	}
	
	
	private boolean existsDestroyedInGrid()
	{
		for (int j = 0; j < nbRows; ++j)
		{
			for (int i = 0; i < nbColumns; ++i)
			{
				if (veggies[i][j].isDestroyed)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	private boolean verifyDestroy(int posx, int posy, Direction direction )
	{
		boolean detruitLegume = false;
		int detruire = 0;
		//Vérification de combo vers la direction qu'une paire a déjà été trouvé
		switch (direction) {
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
			//Si une chaine de plusieurs légumes a été trouvé alors les légumes sont mis dans un état à détruire
			if(detruire>=1)
			{
				for (int m = 0; m <= detruire+1; ++m)
				{
					veggies[posx+m][posy].isDestroyed = true;
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
			//Si une chaine de plusieurs légumes a été trouvé alors les légumes sont mis dans un état à détruire
			if(detruire>=1)
			{
				for (int m = 0; m <= detruire+1; ++m)
				{
					veggies[posx][posy+m].isDestroyed = true;
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
