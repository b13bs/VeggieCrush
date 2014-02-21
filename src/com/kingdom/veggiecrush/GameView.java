package com.kingdom.veggiecrush;

import java.util.ArrayList;
import java.util.Random;

import com.kingdom.veggiecrush.Veggie.VeggieKind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector.SimpleOnGestureListener;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static enum Direction { LEFT, RIGHT, UP, DOWN };
	
	private static final int REFRESH_RATE_MS = 16;
	private static final int MOVING_STEPS = 8;
	
	private ArrayList<GameListener> listeners = new ArrayList<GameListener>();
	
	private GameViewThread refreshThread;
	private GestureDetector gestDetector;
	private Paint paint;
	
	private final int GRID_SIZE = 8;
	private Veggie[][] grid = new Veggie[GRID_SIZE][GRID_SIZE];
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		gestDetector = new GestureDetector(context, new GestureListener());
		paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		
		getHolder().addCallback(this);
		
		Settings.loadBitmaps(getContext());
		initGameGrid();
	}
	
	public void addGameListener(GameListener l)
	{
		listeners.add(l);
	}
	
	public void removeGameListener(GameListener l)
	{
		listeners.remove(l);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// On ne s'en occupe pas
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// On crée et enregistre le thread pour rafraichir la surface de jeu
		refreshThread = new GameViewThread(this, REFRESH_RATE_MS);
		refreshThread.setRunning(true);
		refreshThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// On arrête le thread de rafraichissement
		boolean success = false;
		refreshThread.setRunning(false);
		while (!success)
		{
			try
			{
				refreshThread.join();
				success = true;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// Fonction qui initialise la grille de jeu avec des items placés aléatoirement
	public void initGameGrid()
	{
		Random rand = new Random();
		for (int j = 0; j < GRID_SIZE; ++j)
		{
			for (int i = 0; i < GRID_SIZE; ++i)
			{
				grid[i][j] = new Veggie(getContext(), VeggieKind.values()[rand.nextInt(VeggieKind.values().length)]);
			}
		}
	}
	
	// Fonction dans laquelle on effectue l'affichage de la zone de jeu
	@Override 
    public void onDraw(Canvas canvas) {
		if (canvas != null)
		{
			// On dessine l'arrière plan
			canvas.drawColor(Color.rgb(46, 97, 0));
			
			// On affiche la grille avec les légumes
			Rect drawRectangle;
			for (int j = 0; j < GRID_SIZE; ++j)
			{
				for (int i = 0; i < GRID_SIZE; ++i)
				{
					if (grid[i][j] != null)
					{
						drawRectangle = getRectFromIndex(i, j);
						drawRectangle.offset(grid[i][j].offsetPos.x, grid[i][j].offsetPos.y);
						canvas.drawBitmap(grid[i][j].bitmap, grid[i][j].bitmapRect, drawRectangle, paint);
					}
				}
			}
		}
    }
	
	// Fonction qui est appelée lorsque l'utilisateur touche l'écran
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		// On redirige l'événement vers le détecteur de gestes
		gestDetector.onTouchEvent(e);
		return true;
	}
	
	// Fontion appelée lorsque le détecteur de gestes détecte un "swipe" dans une certaine direction à partir d'un point p en pixels
	private void onSwipe(Direction d, Point p)
	{
		// On transforme la position source en pixels en une position de la grille
		Point index = getIndexFromPos(p.x, p.y);
		
		// On vérifie que le mouvement est valide (pour les bordures)
		if ( (index.x == 0           && d == Direction.LEFT)  ||
		     (index.x == GRID_SIZE-1 && d == Direction.RIGHT) ||
		     (index.y == 0           && d == Direction.UP)    ||
		     (index.y == GRID_SIZE-1 && d == Direction.DOWN)  )
		{
			//déplacement invalide!
			return;
		}
		
		// On détermine quelle autre case est impliquée
		Point index2;
		switch (d)
		{
			case DOWN:
				index2 = new Point(index.x, index.y + 1);
				break;
			case LEFT:
				index2 = new Point(index.x - 1, index.y);
				break;
			case RIGHT:
				index2 = new Point(index.x + 1, index.y);
				break;
			case UP:
				index2 = new Point(index.x, index.y - 1);
				break;
			default:
				return;
		}
		
		// On vérifie si le déplacement est valide (au moins 3 en ligne)
		// TODO: !!!
		if (true)
		{
			// On change de place
			switchPlace(index, index2);
			for (GameListener l : listeners)
			{
				l.onMove();
			}
		}
		
		crush();
	}
	
	private boolean estDeplacementValide()
	{
		// TODO: vérifier si ca donne une ligne d'au moins 3
		return false;
	}
	
	private void crush()
	{
		// TODO: détruire les lignes de 3 ou +
	}
	
	// Fonction qui recoit les positions de 2 items dans le grille et les inverse graduellement avec une animation
	private void switchPlace(Point i1, Point i2)
	{
		Veggie veg1 = grid[i1.x][i1.y];
		Veggie veg2 = grid[i2.x][i2.y];
		
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
				try { Thread.sleep(REFRESH_RATE_MS); } catch (InterruptedException e) { e.printStackTrace();	}
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
				try { Thread.sleep(REFRESH_RATE_MS); } catch (InterruptedException e) { e.printStackTrace();	}
			}
		}
		
		// Déplacement terminé, on change les références dans la grille et met les offset à 0
		veg1.offsetPos.x = 0;
		veg1.offsetPos.y = 0;
		veg2.offsetPos.x = 0;
		veg2.offsetPos.y = 0;
		grid[i1.x][i1.y] = veg2;
		grid[i2.x][i2.y] = veg1;
	}
	
	// Fontion qui retourne la position dans la grille à partir d'une position en pixel
	private Point getIndexFromPos(int x, int y)
	{
		int i = (int)((x / (float)getWidth()) * GRID_SIZE);
		int j = (int)((y / (float)getHeight()) * GRID_SIZE);
		return new Point(i, j);
	}
	
	// Fonction qui retourne la position en pixel dans la vue en fonction de la position dans la grille
	private Rect getRectFromIndex(int i, int j)
	{
		int itemSize = getWidth() / GRID_SIZE;
		return new Rect(itemSize * i, itemSize * j, itemSize * (i+1), itemSize * (j+1));
	}
	
	// Retoune la largeur d'une case de la grille
	private int getLargeurCase()
	{
		return getWidth() / GRID_SIZE;
	}
	
	// Retoune la hauteur d'une case de la grille
	private int getHauteurCase()
	{
		return getHeight() / GRID_SIZE;
	}
	
	
	// Classe privée dont on se sert pour détecter les gestes de l'utilisateur
	private class GestureListener extends SimpleOnGestureListener
	{
	    private static final int SWIPE_MIN_VELOCITY = 70;
		
	    // 'fling' est un 'swipe'
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
        	final int SWIPE_MIN_DISTANCE = (int)(0.9 * getLargeurCase());
        	
        	int srcX = (int)e1.getX();
        	int srcY = (int)e1.getY();
        	int dstX = (int)e2.getX();
        	int dstY = (int)e2.getY();
        	
        	// En fonction de la position de départ et d'arrivé, on détermine la direction du 'swipe'
        	// En fonction de la distance et de la vélocité, on détermine si le 'swipe' est accepté
            if (srcX - dstX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY)
            {
            	onSwipe(Direction.LEFT, new Point(srcX, srcY));
                return true;
            } 
            else if (dstX - srcX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY)
            {
            	onSwipe(Direction.RIGHT, new Point(srcX, srcY));
                return true;
            }

            if (srcY - dstY > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_MIN_VELOCITY)
            {
            	onSwipe(Direction.UP, new Point(srcX, srcY));
                return true;
            }
            else if (dstY - srcY > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_MIN_VELOCITY)
            {
            	onSwipe(Direction.DOWN, new Point(srcX, srcY));
                return true;
            }
            
            return false;
        }
    }
}
